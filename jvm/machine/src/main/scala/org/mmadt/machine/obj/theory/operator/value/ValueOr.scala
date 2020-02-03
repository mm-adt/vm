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

import org.mmadt.machine.obj.theory.obj.`type`.BoolType
import org.mmadt.machine.obj.theory.obj.value.{BoolValue, Value}
import org.mmadt.machine.obj.theory.operator.`type`.{TypeOr, TypePlus}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ValueOr[V <: Value[V]] extends Value[V] {

  def or(bool: Boolean): BoolValue = this.or(bool) //
  def or(bool: BoolValue): BoolValue //
  def or(bool: BoolType): BoolType = this.start().asInstanceOf[TypeOr[BoolType]].or(bool)

  final def |(bool: Boolean): BoolValue = this.or(bool) //
  final def |(bool: BoolValue): BoolValue = this.or(bool) //
  final def |(bool: BoolType): BoolType = this.or(bool) //
}