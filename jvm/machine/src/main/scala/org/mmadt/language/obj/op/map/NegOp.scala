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
import org.mmadt.language.obj._
import org.mmadt.storage.StorageFactory.qOne
import org.mmadt.storage.obj.value.VInst

import scala.util.Try

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait NegOp[O <: Obj] {
  this: O =>
  def neg(): this.type = NegOp().exec(this)
  final def unary_-(): this.type = this.neg()
}

object NegOp {
  def apply[O <: Obj](): NegInst[O] = new NegInst[O]

  class NegInst[O <: Obj](q: IntQ = qOne) extends VInst[O, O]((Tokens.neg, Nil), q) {
    override def q(q: IntQ): this.type = new NegInst(q).asInstanceOf[this.type]
    override def exec(start: O): O = {
      Try(start match {
        case aint: Int => start.clone(ground = -aint.ground)
        case areal: Real => start.clone(ground = -areal.ground)
      }).getOrElse(start).via(start, this)
    }
  }

}