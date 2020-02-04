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

package org.mmadt.machine.obj.theory.operator.`type`

import org.mmadt.language.Tokens
import org.mmadt.machine.obj.theory.obj.`type`.Type
import org.mmadt.machine.obj.theory.obj.value.Value

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait TypeMult[J, V <: Value[V], T <: Type[T]] extends Type[T] {

  def mult(other: J): T = this.mult(value[J, V](other)) //
  def mult(other: V): T = this.push(inst(Tokens.mult, other)) //
  def mult(other: T): T = this.push(inst(Tokens.mult, other)) //

  final def *(other: J): T = this.mult(other) //
  final def *(other: V): T = this.mult(other) //
  final def *(other: T): T = this.mult(other) //
}