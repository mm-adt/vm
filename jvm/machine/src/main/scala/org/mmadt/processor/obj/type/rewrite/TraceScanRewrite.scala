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

import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.trace.ModelOp
import org.mmadt.language.obj.op.{BranchInstruction, OpInstResolver, TraceInstruction}
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, Lst, Obj}
import org.mmadt.storage.StorageFactory.qZero

object TraceScanRewrite extends Rewrite {

  private def getPolyOrObj(obj: Obj): Obj = obj.domain match {
    case alst: Lst[_] => alst.glist.head
    case _ => obj
  }

  override def apply[A <: Obj](obj: A, writer: Writer): A = {
    val rewrites = ModelOp.getRewrites(OpInstResolver.applyRewrites(obj).model).sortBy(x => -x.domainObj.trace.length)
    var a: Obj = obj
    var b: Obj = a
    rewrites.foreach(d => {
      a = b
      b = b.domainObj
      val range = getPolyOrObj(d.range)
      val domain = getPolyOrObj(d)
      val domainTrace = domain.trace.map(x => x._2)
      val length = domainTrace.length
      while (!a.root) {
        val aTrace: List[Inst[Obj, Obj]] = a.trace.map(x => x._2).map(x => rewriteInstArgs(x, writer)).take(length)
        if (aTrace.length == length) {
          val aTraceRewrite = aTrace.zip(domainTrace).map(x => mapInstructions(x._1, x._2))
          if (aTraceRewrite.forall(x => x.alive)) { // the entire window matches, write the range instructions to the type
            b = writer(range.trace.map(x => x._2), aTraceRewrite, b)
            for (_ <- 1 to length) a = a.linvert
          } else { // the window doesn't match, write only the next instruction to the type and try the window shifted over one
            b = aTrace.headOption.map(x => x.exec(b)).get
            a = a.linvert
          }
        } else {
          b = aTrace.foldLeft(b)((x, y) => y.exec(x)) // the window has gone over the instruction length, write unmatched instructions to type
          a = a.domain
        }
      }
    })
    if (!b.equals(obj)) b = TraceScanRewrite(b, writer)
    b.asInstanceOf[A]
  }

  private def rewriteInstArgs(inst: Inst[Obj, Obj], rewrite: Writer): Inst[Obj, Obj] = inst match {
    case _: TraceInstruction => inst
    case _: BranchInstruction => inst
    case _ => OpInstResolver.resolve(inst.op, inst.args.map {
      case atype: Type[_] => TraceScanRewrite(atype, rewrite)
      case avalue: Value[_] => avalue
    })
  }
  private def mapInstructions(lhs: Inst[Obj, Obj], rhs: Inst[Obj, Obj]): Inst[Obj, Obj] = {
    if (lhs.equals(rhs)) return lhs
    if (lhs.op != rhs.op || lhs.args.length != rhs.args.length) return lhs.q(qZero)
    val args = lhs.args.zip(rhs.args).map(x => if (x._1.equals(x._2)) x._2 else if (x._1.test(x._2)) x._1.compute(x._2) else x._2.q(qZero))
    if (args.forall(_.alive)) OpInstResolver.resolve(lhs.op, args) else lhs.q(qZero)
  }

  // def chooseRewrite(range: List[Inst[Obj, Obj]], trace: List[Inst[Obj, Obj]], query: Obj): Obj = query.split(range.foldLeft(query.domainObj)((x, y) => y.exec(x)) `|` trace.filter(x => x.op != Tokens.rewrite).foldLeft(__.asInstanceOf[Obj])((x, y) => y.exec(x))).merge
  def replaceRewrite(range: List[Inst[Obj, Obj]], trace: List[Inst[Obj, Obj]], query: Obj): Obj = {
    if (range.length == trace.length) {
      query.compute(trace
        .zip(range)
        .map(x => OpInstResolver.resolve[Obj, Obj](x._2.op, x._1.args.zip(x._2.args).map(y => y._1.compute(y._2))))
        .foldLeft(query.domainObj)((x, y) => y.exec(x)))
    } else {
      range.foldLeft(query)((x, y) => y.exec(x))
    }
  }
}
