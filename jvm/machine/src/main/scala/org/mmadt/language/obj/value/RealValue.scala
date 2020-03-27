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

import org.mmadt.language.obj.Real
import org.mmadt.language.obj.`type`.{IntType, RealType, Type}
import org.mmadt.language.obj.op.initial.StartOp
import org.mmadt.storage.StorageFactory.vbool

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait RealValue extends Real
  with ObjValue
  with Value[Real] {

  override val value:Double
  def value(java:Double):this.type

  override def plus(other:Type[Real]):RealType = this.start[RealType]().plus(other)
  override def plus(other:Value[Real]):this.type = this.value(this.value + other.asInstanceOf[RealValue].value)
  override def mult(other:Type[Real]):RealType = this.start[RealType]().mult(other)
  override def mult(other:Value[Real]):this.type = this.value(this.value * other.asInstanceOf[RealValue].value)
  override def neg():this.type = this.value(-this.value)
  override def one():RealValue = this.value(1.0d)
  override def gt(other:Value[Real]):BoolValue = vbool(value = this.value > other.asInstanceOf[RealValue].value,q = this.q)
  override def gte(other:Value[Real]):BoolValue = vbool(value = this.value >= other.asInstanceOf[RealValue].value,q = this.q)
  override def lt(other:Value[Real]):BoolValue = vbool(value = this.value < other.asInstanceOf[RealValue].value,q = this.q)
  override def lte(other:Value[Real]):BoolValue = vbool(value = this.value <= other.asInstanceOf[RealValue].value,q = this.q)
  override def zero():RealValue = this.value(0.0d)
}
