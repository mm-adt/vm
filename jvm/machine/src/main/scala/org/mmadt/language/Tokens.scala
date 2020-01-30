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

package org.mmadt.language

import org.mmadt.machine.obj.Obj
import org.mmadt.machine.obj.impl.{TBool, TInt, TObj}

/**
  * @author Marko A. Rodriguez (http://markorodriguez.com)
  */
object Tokens {

  val as = "as"
  val id = "id"
  val plus = "plus"
  val mult = "mult"
  val gt = "gt"

  def symbol(obj: Obj): String = obj match {
    case o: TBool => "bool"
    case o: TInt => "int"
    case o: TObj => "obj"
    case _ => throw new Exception("Error: " + obj)
  }

}
