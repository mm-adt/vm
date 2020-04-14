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
import org.mmadt.language.obj.`type`.{BoolType, Type}
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait EqsOp {
  this: Obj =>
  def eqs(other: Type[_]): BoolType = bool.via(asType(this), EqsOp(other))
  def eqs(other: Value[_]): Bool = (this match {
    case avalue: Value[_] => bool(avalue.value.equals(other.value))
    case _: Type[_] => bool
  }).via(this, EqsOp(other))
  // TODO final def ===(other: T): BoolType = this.eq(other)
  // TODO final def ===(other: V): Bool = this.eq(other)
}

object EqsOp {
  def apply(other: Obj): Inst[Obj, Bool] = new EqsInst(other)

  class EqsInst[O <: Obj with EqsOp](other: Obj, q: IntQ = qOne) extends VInst[O, Bool]((Tokens.lt, List(other)), q) {
    override def q(quantifier: IntQ): this.type = new EqsInst[O](other, quantifier).asInstanceOf[this.type]
    override def exec(start: O): Bool = {
      val inst = new EqsInst[O](Inst.resolveArg(start, other), q)
      (inst.arg0[O]() match {
        case avalue: Value[O] => start.eqs(avalue)
        case atype: Type[O] => start.eqs(atype)
      }).via(start, inst)
    }
  }

}