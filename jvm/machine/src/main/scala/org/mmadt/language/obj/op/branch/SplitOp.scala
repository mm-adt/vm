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
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait SplitOp {
  this: Obj =>
  def split(branches: Poly[Obj]): branches.type = SplitOp(branches).exec(this)
  final def -<(branches: Poly[Obj]): branches.type = this.split(branches)
  def split(branches: __): Poly[Obj] = SplitOp(branches).exec(this).asInstanceOf[Poly[Obj]]
  final def -<(branches: __): Poly[Obj] = this.split(branches)
}

object SplitOp extends Func[Obj, Obj] {
  override val preArgs: Boolean = false
  def apply(branches: Obj): Inst[Obj, branches.type] = new VInst[Obj, branches.type](g = (Tokens.split, List(branches)), func = this) with BranchInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    val apoly: Poly[Obj] = inst.arg0[Obj] match {
      case bpoly: Poly[_] => bpoly
      case aobj: Obj => start ~~> aobj match {
        case bpoly: Poly[Obj] => bpoly.clone(via = aobj.via)
        case _ => return lst.via(start, inst)
      }
    }
    val newPoly: Poly[Obj] = apoly.scalarMult(start.clone(q = qOne, via = (start, inst))) // unit the start
    newPoly.clone(via = (start, SplitOp(newPoly).hardQ(inst.q))).hardQ(BranchInstruction.multPolyQ(start, apoly, inst.clone(_ => List(apoly))).q)
  }
}