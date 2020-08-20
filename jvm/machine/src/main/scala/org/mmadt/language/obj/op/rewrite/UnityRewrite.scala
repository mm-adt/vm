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
object UnityRewrite extends Func[Obj, Obj] {
  def apply(): Inst[Obj, Obj] = new VInst[Obj, Obj](g = (Tokens.rule_unity, Nil), func = this) with RewriteInstruction

  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    start match {
      // case _: __ => start.via(start, inst)
      case atype: Type[_] => atype.trace.map(x => x._2).foldLeft(atype.domainObj)((a, b) => {
        if (b.op == Tokens.branch) {
          b.arg0[Obj] match {
            case alst: Lst[Obj] if alst.gsep == Tokens.`;` =>
              val temp = alst.glist.map(x => IdRewrite.stripId(x)) .map(x => removeRules(x)).filter(x => !x.trace.forall(x => ModelOp.isMetaModel(x._2)))
              if (temp.isEmpty) a
              else if (temp.size == 1) temp.head.trace.map(x => x._2).foldLeft(a)((r, t) => t.exec(r))
              else return a
            case _ => b.exec(a)
          }
        } else
          b.exec(a)
      })
      case _ => start
    }
  }
  def processList(a: Obj, b: Lst[Obj], inst: Inst[Obj, Obj]): Obj = {
    if (b.glist.exists(x => !x.alive)) return zeroObj
    val start = if (__.isAnonRoot(a)) b.glist.head.domainObj else a
    val branches: List[Obj] = b.glist.map(x => IdRewrite.stripId(x)).map(x => removeRules(x)).filter(x => !x.trace.forall(x => ModelOp.isMetaModel(x._2)))
    val end: Obj = {
      if (branches.isEmpty) start
      else if (branches.size == 1) branches.head.trace.map(x => x._2).foldLeft(start)((x, y) => y.exec(x))
      else if (b.glist.forall(z => Type.isIdentity(z))) BranchInstruction.brchType[Obj](b)
      else BranchInstruction.brchType[Obj](b).clone(via = (start, BranchOp(b.clone(_ => branches))))
    }
    IdRewrite.backPropagateQ(IdRewrite.stripId(end), inst.q)
  }
  def removeRules(atype: Obj): Obj = {
    if (atype.isInstanceOf[Value[_]]) return atype
    atype.trace.map(x => x._2).foldLeft(atype.domainObj)((a, b) => if (b.op.startsWith("rule:")) a else b.exec(a))
  }
}
