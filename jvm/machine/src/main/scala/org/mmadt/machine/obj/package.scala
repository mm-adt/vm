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

package org.mmadt.machine

import org.mmadt.machine.obj.impl.OInt.{i0, i1}

/**
  * @author Marko A. Rodriguez (http://markorodriguez.com)
  */
package object obj {
  type JInst = (String, List[Obj])

  type JQ = (Int, Int)

  lazy val qOne: JQ = (i1, i1)

  lazy val qZero: JQ = (i0, i0)

  lazy val qMark: JQ = (i0, i1)

  object JInst {
    def single(arg: String): JInst = (arg, List())
  }

}
