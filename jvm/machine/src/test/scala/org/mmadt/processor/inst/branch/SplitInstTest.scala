/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.processor.inst.branch

import org.mmadt.language.obj.`type`.{IntType, Type, __}
import org.mmadt.language.obj.op.trace.PathOp.VERTICES
import org.mmadt.language.obj.{Int, Obj, Poly}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

class SplitInstTest extends FunSuite with TableDrivenPropertyChecks {


  test("[split] value, type, strm") {
    val check: TableFor3[Obj, Poly[_<:Obj], Obj] =
      new TableFor3[Obj, Poly[_<:Obj], Obj](("input", "type", "result"),
        (int(1), int.-<(int `;` int), int(1) `;` int(1)),
        (int(1, 2, 3), int.q(3).-<(int.q(3) `,` int.q(3)), strm(List(int(1) `,` 1, int(2) `,` 2, int(3) `,` 3))),
        (int(2), __.-<(int | str), int(2) | obj.q(qZero)),
        (int(4).q(2), int.q(2).-<(int | int.is(__.gt(10))), (int(4) | obj.q(qZero)).q(2)),
        (int(2).q(2), int.q(2).-<(int `;` int.is(__.gt(10))), (int(2) `;` obj.q(qZero)).q(2)),
        (int(2), int.-<(int `;` int.is(__.gt(10))), int(2) `;` obj.q(qZero)),
        (int(2), int.-<(int.-<(int | int.is(__.gt(11))) | int.is(__.gt(10))), (int(2) | obj.q(qZero)) | obj.q(qZero)),
      )
    forEvery(check) { (input, atype, result) => {
      assertResult(result)(input.compute(atype.asInstanceOf[Type[Obj]]))
      assertResult(result)(input ==> atype.asInstanceOf[Type[Obj]])
      assertResult(result)(input ==> atype)
      assertResult(result)(input ==> (input.range ==> atype.asInstanceOf[Type[Obj]]))
      assertResult(result)(input ==> (input.range ==> atype))
    }
    }
  }

  test("lineage preservation (products)") {
    assertResult(int(321))(int(1) ==> int.plus(100).plus(200).split(int `,` bool).merge[Int].plus(20))
    assertResult(int.plus(100).plus(200).split(int | bool).merge[Int].plus(20))(int ==> int.plus(100).plus(200).split(int | bool).merge[Int].plus(20))
    assertResult(int(1) `;` 101 `;` 301 `;` 301 `;` 321)((int(1) ==> int.plus(100).plus(200).split(int `,` bool).merge[Int].plus(20)).path(VERTICES))
  }

  test("lineage preservation (coproducts)") {
    assertResult(int(321, 323))(int(1) ==> int.plus(100).plus(200).split(int `,` int.plus(2)).merge[Int].plus(20))
    assertResult(int.plus(100).plus(200).split(int `,` int.plus(2)).merge[Int].plus(20))(int ==> int.plus(100).plus(200).split(int `,` int.plus(2)).merge[Int].plus(20))
    assertResult(strm(List(
      int(1) `;` 101 `;` 301 `;` 301 `;` 321,
      int(1) `;` 101 `;` 301 `;` 301 `;` 303 `;` 323)))(int(1) ==> int.plus(100).plus(200).split(int `,` int.plus(2)).merge[Int].plus(20).path(VERTICES))
  }

  test("quantifiers") {
    assertResult(int(1))((int(1) | 2).merge)
    assertResult(int(1).q(10))((int(1) | 2).q(10).merge)
    assertResult(int(1).q(10))((int(1) | int(2).q(5)).q(10).merge)
    assertResult(int(1).q(4, 10))((int(1) | int(2).q(5)).q(4, 10).merge)
  }

  ////////////////////////////

  /*test("[;] trace w/ state") {
    val results = int(2, 8, 15, 20) ==> int.q(4).to("x").plus(1).-<(
      (int.is(int.gt(10)) --> int.mult(10)) `;`
        (int --> int.id().plus(int.from("x")).to("y").id())).>-

    println(results.toStrm.values.map(x=>x.trace))
    assertResult(6)(results.toStrm.values.length)
    results.toStrm.values.foreach {
      case x if x == int(5) =>
        assertResult(5)(x.trace.length)
        assertResult(int(2))(Obj.fetch(x, "x"))
        assertResult(int(5))(Obj.fetch(x, "y"))
      case x if x == int(17) =>
        assertResult(5)(x.trace.length)
        assertResult(int(8))(Obj.fetch(x, "x"))
        assertResult(int(17))(Obj.fetch(x, "y"))
      case x if x == int(160) =>
        assertResult(3)(x.trace.length)
        assertResult(int(15))(Obj.fetch(x, "x"))
        assertThrows[LanguageException] {
          Obj.fetch(x, "y")
        }
      case x if x == int(31) =>
        assertResult(5)(x.trace.length)
        assertResult(int(15))(Obj.fetch(x, "x"))
        assertResult(int(31))(Obj.fetch(x, "y"))
      case x if x == int(210) =>
        assertResult(3)(x.trace.length)
        assertResult(int(20))(Obj.fetch(x, "x"))
        assertThrows[LanguageException] {
          Obj.fetch(x, "y")
        }
      case x if x == int(41) =>
        assertResult(5)(x.trace.length)
        assertResult(int(20))(Obj.fetch(x, "x"))
        assertResult(int(41))(Obj.fetch(x, "y"))
    }
  }*/

  test("[,] w/ values") {
    assertResult(int(4))(
      int(0).plus(1).-<(
        (int.is(int.gt(2)) --> int.mult(3)) `,`
          (int --> int.mult(4))) >-)

    assertResult(int(12, 16))(
      int(0).plus(4).-<(
        (int.is(int.gt(2)) --> int.mult(3)) `,`
          (int --> int.mult(4))) >-)

    assertResult(int(42, 43, 44))(
      int(0) ==> int.plus(int(39)).-<(
        (int.is(int.gt(40)) --> int.plus(1)) `,`
          (int.is(int.gt(30)) --> int.plus(2)) `,`
          (int.is(int.gt(20)) --> int.plus(3)) `,`
          (int.is(int.gt(10)) --> int.plus(4))).>-.plus(1))

    assertResult(int(33, 34))(
      int(0) ==> int.plus(29).-<(
        (int.is(int.gt(40)) --> int.plus(1)) `,`
          (int.is(int.gt(30)) --> int.plus(2)) `,`
          (int.is(int.gt(20)) --> int.plus(3)) `,`
          (int.is(int.gt(10)) --> int.plus(4))).>-.plus(1))

    assertResult(int(33, 34))(
      int(0) ==> int.plus(29).-<(
        (int.is(__.gt(40)) --> int.plus(1)) `,`
          (int.is(__.gt(30)) --> int.plus(2)) `,`
          (int.is(__.gt(20)) --> int.plus(3)) `,`
          (int.is(__.gt(10)) --> int.plus(4))).>-.plus(int(1)))

    assertResult(int(32, 33))(
      int(0) ==> int.plus(29).-<(
        (int.is(int.gt(40)) --> __.plus(1)) `,`
          (int.is(int.gt(30)) --> __.plus(2)) `,`
          (int.is(int.gt(20)) --> __.plus(3)) `,`
          (int.is(int.gt(10)) --> __.plus(4))).>-)
  }

  test("[,] w/ state") {
    assertResult(real(2.0, 3.0, 3.0))(
      real(0.0, 1.0, 1.0) ==> real.q(3).to("x").plus(1.0).to("y").-<(
        (__.is(__.eqs(1.0)) --> __.from("y")) `,`
          (__.is(__.eqs(2.0)) --> real.from("x"))
      ).>-.plus(real.from("y")))
  }

  ////////////////////////////

  test("[||] w/ values") {
    assertResult(int(4))(
      int(0).plus(1).-<(
        int.is(int.gt(2)) --> int.mult(3) |
          int --> int.mult(4)) >-)

    assertResult(int(12))(
      int(0).plus(4).-<(
        int.is(int.gt(2)) --> int.mult(3) |
          int --> int.mult(4)) >-)

    assertResult(int(42))(
      int(0) ==> int.plus(int(39)).-<(
        int.is(int.gt(40)) --> int.plus(1) |
          int.is(int.gt(30)) --> int.plus(2) |
          int.is(int.gt(20)) --> int.plus(3) |
          int.is(int.gt(10)) --> int.plus(4)).>-.plus(1))

    assertResult(int(33))(
      int(0) ==> int.plus(29).-<(
        int.is(int.gt(40)) --> int.plus(1) |
          int.is(int.gt(30)) --> int.plus(2) |
          int.is(int.gt(20)) --> int.plus(3) |
          int.is(int.gt(10)) --> int.plus(4)).>-.plus(1))

    assertResult(int(33))(
      int(0) ==> int.plus(29).-<(
        int.is(__.gt(40)) --> int.plus(1) |
          int.is(__.gt(30)) --> int.plus(2) |
          int.is(__.gt(20)) --> int.plus(3) |
          int.is(__.gt(10)) --> int.plus(4)).>-.plus(int(1)))

    assertResult(int(32))(
      int(0) ==> int.plus(29).-<(
        int.is(int.gt(40)) --> __.plus(1) |
          int.is(int.gt(30)) --> __.plus(2) |
          int.is(int.gt(20)) --> __.plus(3) |
          int.is(int.gt(10)) --> __.plus(4)).>-)
  }

  test("[||] w/ state") {
    assertResult(real(2.0, 3.0, 3.0))(
      real(0.0, 1.0, 1.0) ==> real.q(3).to("x").plus(1.0).to("y").-<(
        __.is(__.eqs(1.0)) --> __.from("y") |
          __.is(__.eqs(2.0)) --> real.from("x")
      ).>-.plus(real.from("y")))
  }
}
