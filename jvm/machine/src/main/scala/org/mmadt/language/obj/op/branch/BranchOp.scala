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
package org.mmadt.language.obj.op.branch

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, _}
import org.mmadt.storage.StorageFactory.{qOne, zeroObj}
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait BranchOp {
  this: Obj =>
  def branch[O <: Obj](branches: Poly[O]): O = BranchOp(branches).exec(this)
  def branch[O <: Obj](branches: __): O = BranchOp(branches).exec(this)
}

object BranchOp extends Func[Obj, Obj] {
  def apply[A <: Obj](branches: Obj): Inst[Obj, A] = new VInst[Obj, A](g = (Tokens.branch, List(branches)), func = this) with BranchInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    (Inst.oldInst(inst).arg0[Obj] match {
      case apoly: Poly[_] => apoly.hardQ(qOne)
      case _ => inst.arg0[Poly[_]].rangeObj.hardQ(qOne)
    }) match {
      ////////////////////// LST //////////////////////
      case alst: Lst[Obj] => Lst.moduleMult(start, alst) match {
        case blst if blst.isEmpty => zeroObj.via(start, inst)
        case blst: Value[_] => blst.hardQ(q => multQ(q, inst.q)).merge
        case blst: Type[_] =>
          if (1 == blst.size) (start `=>` blst.glist.head).q(inst.q)
          else BranchInstruction.brchType[Obj](blst, inst.q).clone(via = (start, inst.clone(_ => List(blst))))
      }
      ////////////////////// REC //////////////////////
      case arec: Rec[Obj, Obj] => Rec.moduleMult(start, arec) match {
        case brec if brec.isEmpty => zeroObj.via(start, inst)
        case brec: Value[_] => brec.hardQ(q => multQ(q, inst.q)).merge
        case brec: Type[_] =>
          if (1 == brec.size) (start `=>` brec.glist.head).q(inst.q)
          else if (arec.gsep == Tokens.`;`) BranchInstruction.brchType[Obj](brec, inst.q) // TODO: copy the lst pattern for computing last
          else BranchInstruction.brchType[Obj](brec, inst.q).clone(via = (start, inst.clone(_ => List(brec))))
      }
    }
  }
}
