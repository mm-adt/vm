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
import org.mmadt.storage.StorageFactory.{bool, qOne, strm}
import org.mmadt.storage.obj.value.VInst

import scala.util.Try

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait LteOp[O <: Obj] {
  this: O =>
  def lte(anon: __): Bool = LteOp(anon).exec(this)
  def lte(other: O): Bool = LteOp(other).exec(this)
  final def =<(other: O): Bool = this.lte(other)
  final def =<(anon: __): Bool = this.lte(anon)
}

object LteOp {
  def apply[O <: Obj](other: Obj): Inst[O, Bool] = new LteInst[O](other)

  class LteInst[O <: Obj](other: Obj, q: IntQ = qOne) extends VInst[O, Bool](g = (Tokens.lte, List(other)), q = q) {
    override def q(q: IntQ): this.type = new LteInst[O](other, q).asInstanceOf[this.type]
    override def exec(start: O): Bool = {
      val inst = new LteInst[O](Inst.resolveArg(start, other), q)
      Try[Bool]((start match {
        case aint: Int => bool(g = aint.g <= inst.arg0[Int]().g)
        case areal: Real => bool(g = areal.g <= inst.arg0[Real]().g)
        case astr: Str => bool(g = astr.g <= inst.arg0[Str]().g)
      }).via(start, inst)).getOrElse(start match {
        case astrm: Strm[O] => strm[Bool](astrm.values.map(x => this.exec(x)))
        case _ => bool.via(start, inst)
      })
    }
  }

}
