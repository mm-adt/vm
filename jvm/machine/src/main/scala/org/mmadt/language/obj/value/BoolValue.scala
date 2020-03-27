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

import org.mmadt.language.obj.Bool
import org.mmadt.language.obj.`type`.BoolType
import org.mmadt.language.obj.op.initial.StartOp

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait BoolValue extends Bool
  with ObjValue
  with Value[Bool] {

  override val value:Boolean
  def value(java:Boolean):this.type

  override def and(bool:BoolType):BoolType = this.start[Bool]().and(bool)
  override def and(other:BoolValue):this.type = this.value(this.value && other.value)
  override def or(bool:BoolType):BoolType = this.start[Bool]().or(bool)
  override def or(other:BoolValue):this.type = this.value(this.value || other.value)
}