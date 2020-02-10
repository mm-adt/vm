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

package org.mmadt.processor

import org.mmadt.storage.obj._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ChooseInstTest extends FunSuite {

  test("[choose] w/ types") {
    println(int.choose(
      int -> int.mult(3),
      int.mult(1) -> int.mult(4)))

    println(int(4) ==> int.choose(
      int.mult(2) -> int.mult(3),
      int.mult(1) -> int.mult(4)))
  }

  test("[choose] w/ values") {
    assertResult(int(4))(
      int(0).plus(1).choose(
        int.is(int.gt(2)) -> int.mult(3),
        int -> int.mult(4)))

    assertResult(int(12))(
      int(0).plus(4).choose(
        int.is(int.gt(2)) -> int.mult(3),
        int -> int.mult(4)))

    assertResult(int(42))(
      int(0) ==> int.plus(int(39)).choose(
        int.is(int.gt(40)) -> int.plus(1),
        int.is(int.gt(30)) -> int.plus(2),
        int.is(int.gt(20)) -> int.plus(3),
        int.is(int.gt(10)) -> int.plus(4)).plus(1))

    assertResult(int(33))(
      int(0) ==> int.plus(29).choose(
        int.is(int.gt(40)) -> int.plus(1),
        int.is(int.gt(30)) -> int.plus(2),
        int.is(int.gt(20)) -> int.plus(3),
        int.is(int.gt(10)) -> int.plus(4)).plus(1))
  }

  // TODO: test end type quantifier union


}
