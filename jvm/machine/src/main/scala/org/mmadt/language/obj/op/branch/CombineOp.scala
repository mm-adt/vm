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
import org.mmadt.language.obj.op.{BranchInstruction, TraceInstruction}
import org.mmadt.language.obj.{Inst, Lst, Obj}
import org.mmadt.storage.StorageFactory.lst
import org.mmadt.storage.obj.value.VInst

trait CombineOp[A <: Obj] {
  this: Lst[A] =>
  def combine[B <: Obj](other: Lst[B]): Lst[B] = CombineOp[A, B](other).exec(this).asInstanceOf[Lst[B]]
  final def :=[B <: Obj](other: Lst[B]): Lst[B] = this.combine[B](other)
}

object CombineOp extends Func[Lst[Obj], Lst[Obj]] {
  def apply[A <: Obj, B <: Obj](other: Obj): Inst[Obj, Lst[Obj]] = new VInst[Obj, Lst[Obj]](g = (Tokens.combine, List(other)), func = this) with BranchInstruction with TraceInstruction
  override def apply(start: Lst[Obj], inst: Inst[Lst[Obj], Lst[Obj]]): Lst[Obj] = {
    val temp = if (inst.arg0.isInstanceOf[Lst[Obj]]) inst.arg0[Lst[Obj]] else return start // TODO: no via?
    val combinedPoly: Lst[Obj] = lst(g = (temp.gsep, start.glist.zip(temp.glist).map(a => Inst.resolveArg(a._1, a._2))))
    (temp.gsep match {
      case Tokens.| => Lst.keepFirst(combinedPoly)
      case _ => combinedPoly
    }).via(start, inst)
  }
}

