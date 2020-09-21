/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.processor.obj.`type`.rewrite

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Obj.Trace
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.__.{id, _}
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.rewrite.{BranchRewrite, IdRewrite, removeRules}
import org.mmadt.language.obj.op.trace.ModelOp
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.language.obj.op.{BranchInstruction, OpInstResolver, TraceInstruction}
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory.{lst, qZero}

object TraceScanRewrite extends Rewrite {

  private def getPolyOrObj(obj:Obj):Obj = obj.domain match {
    case alst:Lst[_] => alst.glist.head
    case _ => obj
  }

  override def apply[A <: Obj](obj:A, writer:Writer):A = {
    val rewrites = OpInstResolver.applyRewrites(obj).model.rewrites.sortBy(x => -x.domainObj.trace.length)
    var a:Obj = obj
    var b:Obj = a
    rewrites.foreach(rewrite => {
      if (rewrite.equals(lst(__) <= '^(lst(id)))) {
        b = removeRules(BranchRewrite.processType(BranchRewrite().exec(IdRewrite.processType(b))).asInstanceOf[A]) // a faster implementation of id rewrite removal
      } else {
        a = b
        b = b.domainObj
        val range = getPolyOrObj(rewrite.range)
        val domain = getPolyOrObj(rewrite)
        val domainTrace = domain.trace
        val length = domainTrace.length
        while (!a.root) {
          val aTrace:Trace = a.trace.map(x => (x._1, rewriteInstArgs(x._2, writer))).take(length)
          if (aTrace.length == length) {
            val aTraceRewrite = aTrace.zip(domainTrace).map(pair => mapInstructions(pair._1, pair._2))
            if (aTraceRewrite.forall(x => x.isDefined)) { // the entire window matches, write the range instructions to the type
              b = writer(range.trace.map(x => x._2), aTraceRewrite.map(x => x.get._2), b)
              for (_ <- 1 to length) a = a.linvert
            } else { // the window doesn't match, write only the next instruction to the type and try the window shifted over one
              b = aTrace.headOption.map(x => x._2.exec(b)).get
              a = a.linvert
            }
          } else {
            b = aTrace.foldLeft(b)((x, y) => y._2.exec(x)) // the window has gone over the instruction length, write unmatched instructions to type
            a = a.domain
          }
        }
      }
    })
    if (!b.equals(obj)) b = TraceScanRewrite(b, writer)
    // normalize the quantifiers after rewrite
    if (b.equals(obj)) obj
    else if (!b.q.equals(obj.q) && b.trace.forall(x => ModelOp.isMetaModel(x._2))) b.id.q(divQ(obj.q, b.domainObj.q)).asInstanceOf[A]
    else if (b.trace.map(x => x._2).exists(x => x.op == Tokens.branch)) b.asInstanceOf[A]
    else b.asInstanceOf[A].q(divQ(obj.q, b.domainObj.q))
  }

  private def rewriteInstArgs(inst:Inst[Obj, Obj], rewrite:Writer):Inst[Obj, Obj] = inst match {
    case _:TraceInstruction => inst
    case _:BranchInstruction => inst
    case _ => OpInstResolver.resolve(inst.op, inst.args.map {
      case atype:Type[_] => TraceScanRewrite(atype, rewrite)
      case avalue:Value[_] => avalue
    })
  }
  private def mapInstructions(lhs:(Obj, Inst[Obj, Obj]), rhs:(Obj, Inst[Obj, Obj])):Option[(Obj, Inst[Obj, Obj])] = {
    if (lhs.equals(rhs)) return Some(lhs)
    if (lhs._2.op != rhs._2.op || lhs._2.args.length != rhs._2.args.length || !instDomainTyping(lhs._1, rhs._1) || containsVariables(lhs._2)) return None
    val args = lhs._2.args.zip(rhs._2.args).map(x => if (x._1.equals(x._2)) x._2 else if (x._1.test(x._2)) x._1.compute(x._2) else x._2.q(qZero))
    if (args.forall(_.alive)) Some(lhs._1, OpInstResolver.resolve(lhs._2.op, args)) else None
  }

  // if an instruction accesses a variable, don't rewrite it
  private def containsVariables(ainst:Inst[Obj, Obj]):Boolean = ainst.args.exists {
    case atype:Type[_] => atype.trace.exists(y => y._2.op.equals(Tokens.from))
    case _ => false
  }

  // make sure the domain of the inst is the same in type and in rewrite
  private def instDomainTyping(lhs:Obj, rhs:Obj):Boolean = rhs.name.equals(Tokens.anon) || lhs.name.equals(rhs.name) // TODO: should this check quantifiers?

  def replaceRewrite(range:List[Inst[Obj, Obj]], trace:List[Inst[Obj, Obj]], query:Obj):Obj = {
    var model:Model = query.model
    trace.foldLeft(query)((x, y) => {
      val middle = y.exec(x)
      model = mergeAllModels(middle, middle.model)
      middle
    })
    range.foldLeft(query)((x, y) => y.exec(x.model(model)))
  }

  def mergeAllModels(obj:Obj, model:Model):Model = obj.trace.foldLeft(model)((m, x) => {
    x._2.args.foldLeft(m)((a, b) => b.model.update(a)) // TODO: access more paths than just inst args
  })
}
