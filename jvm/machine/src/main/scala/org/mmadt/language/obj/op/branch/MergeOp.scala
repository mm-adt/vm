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
import org.mmadt.language.obj.`type`.{LstType, PolyType, __}
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.value.{PolyValue, Value}
import org.mmadt.language.obj.{Obj, _}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait MergeOp[+A <: Obj] {
  this: Poly[A] =>
  def merge[B <: Obj]: B = MergeOp[A]().exec(this).asInstanceOf[B]
  final def `>-`: A = this.merge[A]
}

object MergeOp extends Func[Obj, Obj] {
  def apply[A <: Obj](): Inst[Poly[A], A] = new VInst[Poly[A], A](g = (Tokens.merge, Nil), func = this)

  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    start match {
      case apoly: Poly[_] if apoly.ctype => __.via(start,inst)
      case apoly: PolyValue[_, _] if apoly.isChoice => apoly.glist.headOption.map(x => BranchInstruction.multPolyQ(x, apoly, inst)).getOrElse(zeroObj)
      case apoly: PolyValue[_, _] if apoly.isParallel => strm(apoly.glist.map(x => BranchInstruction.multPolyQ(x, apoly, inst)))
      case apoly: PolyValue[_, _] if apoly.isSerial => apoly.glist.lastOption.map(x => BranchInstruction.multPolyQ(x, apoly, inst)).getOrElse(zeroObj)
      case apoly: LstType[_] if apoly.isSerial && apoly.glist.lastOption.exists(x => x.isInstanceOf[Value[_]]) => apoly.glist.last.q(inst.q)
      case apoly: PolyType[_, _] => BranchInstruction.multPolyQ(BranchInstruction.brchType[Obj](apoly), lst, inst).clone(via = (start, inst)) // lst is acting a _{1}
      case _ => start.via(start, inst)
    }
  }
}
