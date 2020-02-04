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

package org.mmadt.machine.obj.theory.obj.value

import org.mmadt.language.Stringer
import org.mmadt.machine.obj.theory.obj.Obj
import org.mmadt.machine.obj.theory.obj.`type`.Type

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Value[V <: Value[V]] extends Obj {

  def value(): Any //
  def start(): Type[_]

  override def toString: String = Stringer.valueString(this) //
  override def equals(other: Any): Boolean = other.isInstanceOf[Value[V]] && other.asInstanceOf[Value[V]].value().equals(this.value()) // TODO: replace this with a ValueEq and TypeEq instructions

}
