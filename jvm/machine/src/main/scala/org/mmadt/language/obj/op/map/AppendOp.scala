/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 *  mm-ADT is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU Affero General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 *  License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 *  You can be released from the requirements of the license by purchasing a
 *  commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language.obj.op.map

import org.mmadt.language.Tokens
import org.mmadt.language.obj.{Inst, IntQ, Lst, Obj}
import org.mmadt.storage.StorageFactory.qOne
import org.mmadt.storage.obj.value.VInst

trait AppendOp[O <: Obj] {
  this: Lst[O] =>
  def append(other: O): this.type = this.via(this, AppendOp[O](other))
  //final def ++(other: Lst[O]): Lst[O] = this.append(other)
}

object AppendOp {
  def apply[O <: Obj](other: Obj): AppendInst[O] = new AppendInst[O](other)

  class AppendInst[O <: Obj](other: Obj, q: IntQ = qOne) extends VInst[Lst[O], Lst[O]]((Tokens.append, List(other)), q) {
    override def q(quantifier: IntQ): this.type = new AppendInst[O](other, quantifier).asInstanceOf[this.type]
    override def exec(start: Lst[O]): Lst[O] = {
      val inst = new AppendInst[O](Inst.resolveArg(start, other), q)
      start.append(inst.arg0[O]()).via(start, inst).asInstanceOf[Lst[O]]
    }
  }
}