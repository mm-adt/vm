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
import org.mmadt.language.obj.value.{BoolValue, Value}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

import scala.util.Try

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait AndOp {
  this: Bool =>
  def and(anon: __): Bool = AndOp(anon).exec(this)
  def and(other: Bool): Bool = AndOp(other).exec(this)
  final def &&(anon: __): Bool = this.and(anon)
  final def &&(bool: Bool): Bool = this.and(bool)
}

object AndOp {
  def apply(other: Obj): AndInst = new AndInst(other)

  class AndInst(other: Obj, q: IntQ = qOne) extends VInst[Bool, Bool]((Tokens.and, List(other)), q) {
    override def q(q: IntQ): this.type = new AndInst(other, q).asInstanceOf[this.type]
    override def exec(start: Bool): Bool = {
      val inst = new AndInst(Inst.resolveArg(start, other), q)
      (start match {
        case _:Strm[_] => start
        case _:BoolValue => start.clone(value = start.value && inst.arg0[Bool]().value)
        case _ => start
      }).via(start, inst)

    }
  }

}
