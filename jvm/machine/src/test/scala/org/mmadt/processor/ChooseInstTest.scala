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

import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ChooseInstTest extends FunSuite {

  test("[choose] w/ types"){
    assertResult("int[choose,[int->int[mult,3]|int[mult,1]->int[mult,4]]]")(
      int.choose(
        int -> int.mult(3),
        int.mult(1) -> int.mult(4)).toString)
    assertResult("int{?}<=int[choose,[int->int[mult,3]|int[mult,1]->int[mult,4]]][is,bool<=int[gt,20]]")(
      int.choose(
        int -> int.mult(3),
        int.mult(1) -> int.mult(4)).is(int.gt(20)).toString)

    assertResult("int{0,30}<=int{30}[choose,[int{30}->int{30}[mult,3]|int{30}[mult,1]->int{30}[mult,4]]][is,bool<=int[gt,20]]")( // TODO: why is {30} not at is?
      int.q(30).choose(
        int -> int.mult(3),
        int.mult(1) -> int.mult(4)).is(int.gt(20)).toString)
  }

  test("[choose] w/ values"){
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
}
