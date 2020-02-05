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

package org.mmadt.storage

import org.mmadt.machine.obj.impl.obj._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VRecTest extends FunSuite {

  test("rec values") {
    assertResult("[1:true]")(rec(int(1) -> btrue).toString)
    assertResult("[1:true,2:false]")(rec(int(1) -> btrue, int(2) -> bfalse).toString)
    assertResult("[1:true,2:false]")(rec(int(1) -> btrue).plus(rec(int(2) -> bfalse)).toString)
    println(rec.plus(rec(int(2) -> bfalse)).get(int(2), bool))
    assertResult(bfalse)(rec(int(1) -> btrue) ==> rec.plus(rec(int(2) -> bfalse)).get(int(2),bool))
    assertResult(rec(int(1) -> btrue, int(2) -> bfalse))(rec(int(1) -> btrue) ==> rec.plus(rec(int(2) -> bfalse)))
    assertResult(btrue)(rec(int(1) -> btrue, int(2) -> bfalse).get(int(1)))
    assertResult(bfalse)(rec(int(1) -> btrue, int(2) -> bfalse).get(int(2)))
    assertResult(int(6))(rec(int(1) -> int.plus(5), int(2) -> int.mult(100)).get(int(1)))
    assertResult(int(200))(rec(int(1) -> int.plus(5), int(2) -> int.mult(100)).get(int(2)))
    assertResult(int(200))(rec(int(1) -> int.plus(5), int(2) -> int.mult(100)) ==> rec.get(int(2), int))
    intercept[NoSuchElementException] {
      rec(int(1) -> btrue, int(2) -> bfalse).get(int(3))
    }
  }
}
