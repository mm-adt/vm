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

package org.mmadt.machine.obj.theory.obj.`type`

import org.mmadt.machine.obj.theory.TypeCommon
import org.mmadt.machine.obj.theory.obj.value.RecValue
import org.mmadt.machine.obj.theory.obj.{Obj, Rec}
import org.mmadt.machine.obj.theory.operator.`type`.{TypeGet, TypePlus}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait RecType[A <: Obj, B <: Obj] extends Rec[A, B]
  with Type[RecType[A, B]]
  with TypePlus[Map[A, B], RecValue[A, B], RecType[A, B]]
  with TypeCommon[Map[A, B], RecValue[A, B], RecType[A, B]]
  with TypeGet[Map[A, B], A, B] {
  def typeValue(): Map[A, B] //
}