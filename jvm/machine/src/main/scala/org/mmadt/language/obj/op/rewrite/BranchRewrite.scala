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

package org.mmadt.language.obj.op.rewrite

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.branch.BranchOp
import org.mmadt.language.obj.op.trace.ModelOp
import org.mmadt.language.obj.op.{BranchInstruction, RewriteInstruction}
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, Lst, Obj}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object BranchRewrite extends Func[Obj, Obj] {
  def apply(): Inst[Obj, Obj] = new VInst[Obj, Obj](g = (Tokens.rule_unity, Nil), func = this) with RewriteInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    start match {
      case _: __ if !__.isToken(start) => start.via(start, inst)
      case atype: Type[_] => processType(atype)
      case _ => start
    }
  }
  def processType[A <: Obj](atype: A): A = {
    if (atype.isInstanceOf[Value[_]] || __.isAnon(atype) || __.isToken(atype) || !exists(atype, Tokens.branch)) return atype
    atype.trace.map(x => x._2).foldLeft(atype.domainObj)((a, b) => {
      if (b.op == Tokens.branch) {
        b.arg0[Obj] match {
          case alst: Lst[Obj] if alst.gsep == Tokens.`;` =>
            if (alst.glist.exists(x => !x.alive)) return zeroObj.asInstanceOf[A]
            val start = if (__.isAnonRoot(a)) alst.glist.head.domainObj else a
            val branches: List[Obj] = alst.glist.map(x => IdRewrite.processType(x)).map(x => processObj(x)).map(x => removeRules(x)).filter(x => !x.trace.forall(x => ModelOp.isMetaModel(x._2)))
            val end: Obj = {
              if (branches.isEmpty) start
              else if (branches.size == 1) removeRules(branches.head).trace.map(x => x._2).foldLeft(start)((x, y) => y.exec(x))
              else if (alst.glist.forall(z => Type.isIdentity(z))) BranchInstruction.brchType[Obj](alst)
              else BranchInstruction.brchType[Obj](alst).clone(via = (start, BranchOp(alst.clone(_ => branches))))
            }
            backPropagateQ(IdRewrite.processType(end), b.q)
          case _ => b.exec(a)
        }
      } else
        b.exec(a)
    }).asInstanceOf[A]
  }


  def processObj[A <: Obj](obj: A): A = {
    if (obj.isInstanceOf[Value[_]]) return obj
    obj.trace.map(x => x._2).map(x => downPropagateRule(x, y => BranchRewrite.processType(y))).foldLeft(obj.domainObj)((a, b) => b.exec(a)).asInstanceOf[A]
  }
}
