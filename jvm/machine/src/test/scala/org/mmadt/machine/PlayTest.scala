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

import org.mmadt.machine.obj.impl.obj._
import org.mmadt.machine.obj.theory.obj.`type`.BoolType
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class PlayTest extends FunSuite {

  test("value + value") {
    assert(int(1) + int(2) === int(3))
    assert(btrue.value())
    assert((btrue | bfalse) === btrue)
    assert((btrue & bfalse) === bfalse)
    println(int(4) ==> (int.plus(3).mult(int) ==> int.plus(2).gt(5)).asInstanceOf[BoolType])

    println(int.plus(1).choose(
      int.is(int.gt(2)) -> int.mult(3),
      int -> int.mult(4)))


    assertResult(int(4))(
      int(0).plus(1).choose(
        int.is(int.gt(2)) -> int.mult(3),
        int -> int.mult(4)))

    assertResult(int(12))(
      int(0).plus(4).choose(
        int.is(int.gt(2)) -> int.mult(3),
        int -> int.mult(4)))
  }


}
