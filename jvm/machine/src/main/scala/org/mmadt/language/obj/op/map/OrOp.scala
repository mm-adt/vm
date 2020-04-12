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
trait OrOp {
  this: Obj =>
  def or(other: BoolType): BoolType = this match {
    case atype: BoolType => atype.compose(OrOp(other))
    case avalue: BoolValue => avalue.start().compose(OrOp(other))
  }
  def or(other: BoolValue): this.type = (this match {
    case avalue: BoolValue => avalue.clone(value = avalue.value || other.value, via = (this, OrOp(other)))
    case atype: BoolType => atype.compose(OrOp(other))
  }).asInstanceOf[this.type]

  final def ||(bool: BoolType): BoolType = this.or(bool)
  final def ||(bool: BoolValue): this.type = this.or(bool)
}

object OrOp {
  def apply(other: Obj): OrInst = new OrInst(other)

  class OrInst(other: Obj, q: IntQ = qOne) extends VInst[Bool, Bool]((Tokens.or, List(other)), q) {
    override def q(q: IntQ): this.type = new OrInst(other, q).asInstanceOf[this.type]
    override def exec(start: Bool): Bool = {
      val inst = new OrInst(Inst.resolveArg(start, other), q)
      inst.arg0[Bool]() match {
        case bvalue: BoolValue => start.or(bvalue).via(start, inst)
        case btype: BoolType => start.or(btype).via(start, inst)
      }
    }
  }

}