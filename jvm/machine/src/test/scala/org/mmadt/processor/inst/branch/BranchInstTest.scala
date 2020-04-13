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

package org.mmadt.processor.inst.branch

import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class BranchInstTest extends FunSuite with TableDrivenPropertyChecks {

  test("[branch] quantifiers") {
    val check: TableFor2[Type[Obj], Obj] =
      new TableFor2[Type[Obj], Obj](("branch", "range"),
        (int.branch(
          int -> int.plus(int).q(2),
          int.plus(2) -> int.plus(int).q(3)), int.q(0, 5)),
        (int.branch(
          int -> int.plus(int).q(2),
          int.plus(2) -> int.plus(int)), int.q(0, 3)),
        (int.branch(
          int -> int.plus(int).q(2),
          int.plus(2) -> str.plus(str)), obj.q(0, 3)),
        (int.branch(
          int -> int.plus(int).q(2),
          int.plus(2) -> int.branch(
            int -> int.plus(int),
            int.id() -> int.plus(int).q(10))), int.q(0, 13)),
      )
    forEvery(check) { (branch, range) => {
      assertResult(range)(branch.range)
    }
    }
  }

  test("[branch] w/ types") {
    assertResult("int{0,2}<=int[branch,[int:int[mult,3]&int{?}<=int[is,bool<=int[gt,0]]:int[mult,4]]]")(
      int.branch(
        int -> int.mult(3),
        int.is(int.gt(0)) -> int.mult(4)).toString)
  }

  test("[branch] w/ values") {
    assertResult(strm(Iterator(int(4))))(
      int(0).plus(1).branch(
        int.is(int.gt(2)) -> int.mult(3),
        int -> int.mult(4)))

    assertResult(int(12, 16))(
      int(0).plus(4).branch(
        int.is(int.gt(2)) -> int.mult(3),
        int -> int.mult(4)))

    assertResult(int(42, 43, 44))(
      int(0) ==> int.plus(int(39)).branch(
        int.is(int.gt(40)) -> int.plus(1),
        int.is(int.gt(30)) -> int.plus(2),
        int.is(int.gt(20)) -> int.plus(3),
        int.is(int.gt(10)) -> int.plus(4)).plus(1))

    assertResult(int(33, 34))(
      int(0) ==> int.plus(29).branch(
        int.is(int.gt(40)) -> int.plus(1),
        int.is(int.gt(30)) -> int.plus(2),
        int.is(int.gt(20)) -> int.plus(3),
        int.is(int.gt(10)) -> int.plus(4)).plus(1))

    assertResult(int(33, 34))(
      int(0) ==> int.plus(29).branch(
        int.is(__.gt(40)) -> int.plus(1),
        int.is(__.gt(30)) -> int.plus(2),
        int.is(__.gt(20)) -> int.plus(3),
        int.is(__.gt(10)) -> int.plus(4)).plus(int(1)))

    assertResult(int(32, 33))(
      int(0) ===> int.plus(29).branch(
        int.is(int.gt(40)) -> __.plus(1),
        int.is(int.gt(30)) -> __.plus(2),
        int.is(int.gt(20)) -> __.plus(3),
        int.is(int.gt(10)) -> __.plus(4)))
  }

  /*test("[branch] w/ traverser state"){
    assertResult(real(2.0,3.0,3.0))(
      real(0.0,1.0,1.0) ===> real.q(3).to("x").plus(1.0).to("y").branch[Obj,Real](
        __.is(__.eqs(1.0)) -> __.from("y"),
        __.is(__.eqs(2.0)) -> __.from("x")
      ).plus(real.from[Real]("y")))
  }*/
}