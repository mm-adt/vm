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
import org.mmadt.language.obj.branch.{Coprod, Prod}
import org.mmadt.language.obj.{Int, IntQ, Lst, Obj, Real, Rec, Str}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ZeroOp[O <: Obj] {
  this: O =>
  def zero(): this.type = ZeroOp().exec(this)
}

object ZeroOp {
  def apply[O <: Obj](): ZeroInst[O] = new ZeroInst[O]

  class ZeroInst[O <: Obj](q: IntQ = qOne) extends VInst[O, O]((Tokens.zero, Nil), q) {
    override def q(q: IntQ): this.type = new ZeroInst[O](q).asInstanceOf[this.type]
    override def exec(start: O): O = {
      (start match {
        case _: Int => int(0)
        case _: Real => real(0.0)
        case _: Str => str(Tokens.empty)
        case alst: Lst[Obj] => alst.clone(value = List.empty[Obj])
        case arec: Rec[Obj, Obj] => arec.clone(value = Map.empty[Obj, Obj])
        case _: Prod[Obj] => prod()
        case _: Coprod[Obj] => coprod()
      }).asInstanceOf[O].q(start.q).via(start, this)
    }
  }

}