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

import org.mmadt.machine.obj.theory.ValueCommon
import org.mmadt.machine.obj.theory.obj.`type`.RecType
import org.mmadt.machine.obj.theory.obj.{Obj, Rec}
import org.mmadt.machine.obj.theory.operator.value.ValuePlus

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait RecValue[K <: Obj, V <: Obj] extends Rec[K, V]
  with Value[RecValue[K, V]]
  with ValuePlus[Map[K, V], RecValue[K, V], RecType[K, V]]
  with ValueCommon[RecValue[K, V], RecType[K, V]] {

  override def value(): Map[K, V] //
  override def start(): RecType[K, V] //

  override def plus(other: RecValue[K, V]): RecValue[K, V] = rec[K, V](other.value() ++ this.value()) //
  def get(key: K): V = this.value().get(key).get
}

