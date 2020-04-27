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
import org.mmadt.language.obj.{Inst, Int, IntQ, Obj, Real}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

import scala.util.Try

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait MultOp[O <: Obj] {
  this: O =>
  def mult(anon: __): this.type = MultOp(anon).exec(this)
  def mult(arg: O): this.type = MultOp(arg).exec(this)
  final def *(anon: __): this.type = this.mult(anon)
  final def *(arg: O): this.type = this.mult(arg)
}

object MultOp {
  def apply[O <: Obj](obj: Obj): MultInst[O] = new MultInst[O](obj)

  class MultInst[O <: Obj](arg: Obj, q: IntQ = qOne) extends VInst[O, O]((Tokens.mult, List(arg)), q) {
    override def q(q: IntQ): this.type = new MultInst[O](arg, q).asInstanceOf[this.type]
    override def exec(start: O): O = {
      val resolvedArg: Obj = Inst.resolveArg(start, arg)
      Try(start match {
        case aint: Int => start.clone(value = aint.value * resolvedArg.asInstanceOf[Int].value)
        case areal: Real => start.clone(value = areal.value * resolvedArg.asInstanceOf[Real].value)
      }).getOrElse(start).via(start, new MultInst(resolvedArg, this.q))
    }
  }

}

