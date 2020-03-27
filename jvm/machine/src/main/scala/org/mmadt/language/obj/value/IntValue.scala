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

package org.mmadt.language.obj.value

import org.mmadt.language.obj.Int
import org.mmadt.language.obj.`type`.{IntType, Type}
import org.mmadt.language.obj.op.initial.StartOp
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait IntValue extends Int
  with ObjValue
  with Value[Int]{

  override val value:Long
  def value(java:Long):this.type

  override def plus(other:Type[Int]):IntType = this.start[IntType]().plus(other)
  override def plus(other:Value[Int]):this.type = this.value(this.value + other.value.asInstanceOf[Long])
  override def mult(other:Type[Int]):IntType = this.start[IntType]().mult(other)
  override def mult(other:Value[Int]):this.type = this.value(this.value * other.value.asInstanceOf[Long])
  override def neg():this.type = this.value(-this.value)
  override def one():IntValue = this.value(1L)
  override def gt(other:Value[Int]):BoolValue = vbool(value = this.value > other.value.asInstanceOf[Long],q = this.q)
  override def gte(other:Value[Int]):BoolValue = vbool(value = this.value >= other.value.asInstanceOf[Long],q = this.q)
  override def lt(other:Value[Int]):BoolValue = vbool(value = this.value < other.value.asInstanceOf[Long],q = this.q)
  override def lte(other:Value[Int]):BoolValue = vbool(value = this.value <= other.value.asInstanceOf[Long],q = this.q)
  override def zero():IntValue = this.value(0L)
}
