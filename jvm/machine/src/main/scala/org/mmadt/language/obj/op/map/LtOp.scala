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
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

import scala.util.Try

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait LtOp[O <: Obj] {
  this: O =>
  def lt(anon: __): Bool = LtOp(anon).exec(this)
  def lt(other: O): Bool = LtOp(other).exec(this)
  final def <(other: O): Bool = this.lt(other)
  final def <(anon: __): Bool = this.lt(anon)
}

object LtOp {
  def apply[O <: Obj](other: Obj): Inst[O, Bool] = new LtInst[O](other)

  class LtInst[O <: Obj](other: Obj, q: IntQ = qOne) extends VInst[O, Bool]((Tokens.lt, List(other)), q) {
    override def q(q: IntQ): this.type = new LtInst[O](other, q).asInstanceOf[this.type]
    override def exec(start: O): Bool = {
      val inst = new LtInst[O](Inst.resolveArg(start, other), q)
      Try[Bool]((start match {
        case aint: Int => bool(value = aint.value < inst.arg0[Int]().value)
        case areal: Real => bool(value = areal.value < inst.arg0[Real]().value)
        case astr: Str => bool(value = astr.value < inst.arg0[Str]().value)
      }).via(start, inst)).getOrElse(start match {
        case astrm: Strm[O] => strm[Bool](astrm.values.map(x => this.exec(x)))
        case _ => bool.via(start, inst)
      })
    }
  }

}
