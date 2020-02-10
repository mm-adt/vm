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

package org.mmadt.machine.obj.theory.obj

import org.mmadt.machine.obj.impl.obj.value.VInt
import org.mmadt.machine.obj.theory.obj.`type`.IntType
import org.mmadt.machine.obj.theory.obj.value.IntValue
import org.mmadt.machine.obj.theory.operator._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Int extends Obj
  with PlusOp[Int, IntValue, IntType]
  with MultOp[Int, IntValue, IntType]
  with GtOp[Int, IntValue, IntType]
  with IsOp[Int, IntValue, IntType]
  with ToOp[IntType] {
}

object Int {
  implicit def longToInt(java: Long): IntValue with Int = new VInt(java) //
}
