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
import org.mmadt.language.obj.`type`.BoolType
import org.mmadt.language.obj.value.BoolValue
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait AndOp {
  this: Obj =>
  def and(other: BoolType): BoolType = this match {
    case atype: BoolType => atype.via(atype, AndOp(other))
    case avalue: BoolValue => avalue.start().via(avalue, AndOp(other))
  }
  def and(other: BoolValue): this.type = (this match {
    case avalue: BoolValue => avalue.clone(value = avalue.value && other.value, via = (this, AndOp(other)))
    case atype: BoolType => atype.via(atype, AndOp(other))
  }).asInstanceOf[this.type]

  final def &&(bool: BoolType): BoolType = this.and(bool)
  final def &&(bool: BoolValue): this.type = this.and(bool)
}

object AndOp {
  def apply(other: Obj): AndInst = new AndInst(other)

  class AndInst(other: Obj, q: IntQ = qOne) extends VInst[Bool, Bool]((Tokens.and, List(other)), q) {
    override def q(q: IntQ): this.type = new AndInst(other, q).asInstanceOf[this.type]
    override def exec(start: Bool): Bool = {
      val inst = new AndInst(Inst.resolveArg(start, other), q)
      inst.arg0[Bool]() match {
        case bvalue: BoolValue => start.and(bvalue).via(start, inst)
        case btype: BoolType => start.and(btype).via(start, inst)
      }
    }
  }

}
