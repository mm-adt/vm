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

package org.mmadt.machine.obj.theory.operator.value

import org.mmadt.machine.obj.theory.obj.`type`.{BoolType, Type}
import org.mmadt.machine.obj.theory.obj.value.{BoolValue, Value}
import org.mmadt.machine.obj.theory.operator.`type`.TypeIs

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ValueIs[V <: Value[V], T <: Type[T]] extends Value[V] {
  def is(bool: Boolean): V = this.is(value[Boolean, BoolValue](bool)) //
  def is(bool: BoolValue): V = if (bool.value()) this.asInstanceOf[V] else this.asInstanceOf[V].q(int(0), int(0))
  def is(bool: BoolType): T = this.start().asInstanceOf[TypeIs[T]].is(bool)
}
