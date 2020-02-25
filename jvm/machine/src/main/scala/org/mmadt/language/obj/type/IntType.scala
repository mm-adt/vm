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

package org.mmadt.language.obj.`type`

import org.mmadt.language.obj.Int
import org.mmadt.language.obj.op._
import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.op.map.{EqsOp, GtOp, MultOp, NegOp, PlusOp}
import org.mmadt.language.obj.op.traverser.ToOp
import org.mmadt.language.obj.value.{BoolValue, IntValue, StrValue}
import org.mmadt.storage.obj.value.VInt

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait IntType extends Int
  with Type[IntType] {

  def apply(value:IntValue):IntValue = new VInt(this.name,value.value(),this.q())

  override def to(label:StrValue):this.type = this.compose(ToOp(label))
  override def plus(other:IntType):IntType = this.compose(PlusOp(other))
  override def plus(other:IntValue):this.type = this.compose(PlusOp(other))
  override def mult(other:IntType):IntType = this.compose(MultOp(other))
  override def mult(other:IntValue):this.type = this.compose(MultOp(other))
  override def neg():this.type = this.compose(NegOp())
  override def gt(other:IntType):BoolType = this.bool(GtOp(other))
  override def gt(other:IntValue):BoolType = this.bool(GtOp(other))
  override def gt():BoolType = this.bool(GtOp(this))
  override def eqs(other:IntType):BoolType = this.bool(EqsOp(other))
  override def eqs(other:IntValue):BoolType = this.bool(EqsOp(other))
  override def is(bool:BoolType):IntType = this.compose(IsOp(bool)).q(0,q()._2)
  override def is(bool:BoolValue):this.type = this.compose(IsOp(bool)).q(0,q()._2)
}

