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

import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.traverser.ToOp
import org.mmadt.language.obj.value.{BoolValue,IntValue,StrValue,Value}
import org.mmadt.language.obj.{Int,minZero}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInt

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait IntType extends Int
  with Type[Int]
  with ObjType {

  def apply(value:IntValue):IntValue = new VInt(this.name,value.value(),this.q())


  override def to(label:StrValue):this.type = this.compose(ToOp(label))
  override def plus(other:Type[Int]):IntType = this.compose(PlusOp(other))
  override def plus(other:Value[Int]):this.type = this.compose(PlusOp(other))
  override def mult(other:Type[Int]):IntType = this.compose(MultOp(other))
  override def mult(other:Value[Int]):this.type = this.compose(MultOp(other))
  override def neg():this.type = this.compose(NegOp())
  override def one():IntType = this.compose(OneOp())
  override def gt(other:Type[Int]):BoolType = this.compose(bool,GtOp(other))
  override def gt(other:Value[Int]):BoolType = this.compose(bool,GtOp(other))
  override def eqs(other:Type[Int]):BoolType = this.compose(bool,EqsOp(other))
  override def eqs(other:Value[Int]):BoolType = this.compose(bool,EqsOp(other))
  override def is(bool:BoolType):IntType = this.compose(IsOp(bool)).q(minZero(this.q()))
  override def is(bool:BoolValue):this.type = this.compose(IsOp(bool)).q(minZero(this.q()))
  override def zero():IntType = this.compose(ZeroOp())
}

