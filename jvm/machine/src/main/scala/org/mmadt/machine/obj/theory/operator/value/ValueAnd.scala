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
import org.mmadt.machine.obj.theory.operator.`type`.TypeAnd

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ValueAnd[V <: Value[V]] extends Value[V] {

  def and(bool: Boolean): BoolValue = this.and(bool) //
  def and(bool: BoolValue): BoolValue //
  def and(bool: BoolType): BoolType = this.start().asInstanceOf[TypeAnd[BoolType]].and(bool)

  final def &(bool: Boolean): BoolValue = this.and(bool) //
  final def &(bool: BoolValue): BoolValue = this.and(bool) //
  final def &(bool: BoolType): BoolType = this.and(bool) //
}