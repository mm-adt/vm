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
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{RecType, Type}
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait SplitOp {
  this: Obj =>
  def split(branches: Obj): branches.type = SplitOp(branches).exec(this)
  final def -<(branches: Obj): branches.type = this.split(branches)
}

object SplitOp extends Func[Obj, Obj] {
  def apply(branches: Obj): Inst[Obj, branches.type] = new VInst[Obj, branches.type](g = (Tokens.split, List(branches)), func = this) with BranchInstruction

  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    val startUnit = start.hardQ(qOne)
    val oldInst: Inst[Obj, Poly[Obj]] = Inst.oldInst(inst).asInstanceOf[Inst[Obj, Poly[Obj]]]
    val apoly: Poly[Obj] = oldInst.arg0[Obj] match {
      case x: Poly[Obj] => x
      case _ => if (inst.arg0[Obj].alive) inst.arg0[Poly[Obj]] else return zeroObj
    }
    val newInst: Inst[Obj, Poly[Obj]] = SplitOp(Poly.resolveSlots(startUnit.clone(via = (startUnit, oldInst)), apoly)).hardQ(inst.q)
    (apoly match {
      case _: RecType[_, _] if apoly.isSerial => newInst.arg0[Obj].clone(via = (start, oldInst))
      case _: RecType[_, _] if apoly.isChoice => processFirst(startUnit, oldInst).clone(via = (start, newInst)) // TODO: cause the same resolutions map to the same keys
      //
      case _: Poly[_] if apoly.isChoice => processFirst(startUnit, newInst).clone(via = (start, newInst))
      case _ => newInst.arg0[Poly[Obj]].clone(via = (start, newInst))
    }).hardQ(BranchInstruction.multPolyQ(start, apoly, inst).q)
  }

  private def processFirst(start: Obj, inst: Inst[Obj, Poly[Obj]]): Poly[Obj] = start match {
    case _: Type[_] => inst.arg0[Poly[Obj]]
    case _ => Poly.keepFirst(start, inst.arg0[Poly[Obj]])
  }
}