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
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.{Inst, IntQ, Obj, Lst}
import org.mmadt.storage.StorageFactory.qOne
import org.mmadt.storage.obj.value.VInst

trait AppendOp[A <: Obj] {
  this: Lst[A] =>
  def append(anon: __): this.type = AppendOp(anon).exec(this).asInstanceOf[this.type]
  def append(other: A): this.type = AppendOp(other).exec(this).asInstanceOf[this.type]
  final def +:(other: A): this.type = this.append(other)
  final def +:(anon: __): this.type = this.append(anon)
}

object AppendOp {
  def apply[O <: Obj](other: Obj): AppendInst[O] = new AppendInst[O](other)

  class AppendInst[O <: Obj](other: Obj, q: IntQ = qOne) extends VInst[Lst[O], Lst[O]](g=(Tokens.append, List(other)), q=q) {
    override def q(quantifier: IntQ): this.type = new AppendInst[O](other, quantifier).asInstanceOf[this.type]
    override def exec(start: Lst[O]): Lst[O] = {
      val inst = new AppendInst[O](Inst.resolveArg(start, other), q)
      start.clone(start.glist :+ inst.arg0[O]).via(start, inst)
    }
  }

}