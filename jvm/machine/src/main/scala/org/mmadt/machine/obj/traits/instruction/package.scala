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

package org.mmadt.machine.obj.traits

import org.mmadt.machine.obj.{Bool, TQ}
import org.mmadt.machine.obj.impl.VBool.boolT
import org.mmadt.machine.obj.impl.{VBool, VInt}

/**
  * @author Marko A. Rodriguez (http://markorodriguez.com)
  */
package object instruction {

  trait Instructions extends FilterInstructions with MapInstructions

  trait FilterInstructions {

    def id(): this.type = this

    def is(bool: Bool): this.type = if (bool.eq(boolT)) this else this
  }

  trait MapInstructions {

    def map[A](obj: A): A = obj

    def q(): TQ

    def q(min: VInt, max: VInt): this.type

  }

}
