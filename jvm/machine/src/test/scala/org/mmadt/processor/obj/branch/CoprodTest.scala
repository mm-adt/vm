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

package org.mmadt.processor.obj.branch

import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.branch.Coprod
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Int, Obj, Str}
import org.mmadt.storage.StorageFactory.{coprod, int, str, vlst}
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}

class CoprodTest extends FunSuite with TableDrivenPropertyChecks {

  test("product [zero][one]") {
    //assertResult(List.empty[Obj])(prod(str("a"), str("b")).zero().value)
    //    assertResult(List.empty[Obj])(prod(str("a"), str("b")).one().value)
    //assertResult(prod())(prod(str("a"), str("b")).zero())
  }

  test("coproduct [tail][head] values") {
    val starts: TableFor2[Coprod[Obj], List[Value[Obj]]] =
      new TableFor2[Coprod[Obj], List[Value[Obj]]](("prod", "projections"),
        (coprod(), List.empty),
        (coprod("a"), List(str("a"))),
        (coprod("a", "b"), List(str("a"), str("b"))),
        (coprod("a", "b", "c"), List(str("a"), str("b"), str("c"))),
        (coprod("a", coprod[Str]("b", "d"), "c"), List(str("a"), coprod[Str]("b", "d"), str("c"))),
      )
    forEvery(starts) { (alst, blist) => {
      assertResult(alst.value)(blist)
      assertResult(alst)(vlst[Value[Obj]](value = blist))
      if (blist.nonEmpty) {
        assertResult(alst.head())(blist.head)
        assertResult(alst.value.head)(blist.head)
        assertResult(alst.tail().value)(blist.tail)
        assertResult(alst.value.tail)(blist.tail)
      }
    }
    }
  }

  test("coproduct [get] values") {
    assertResult(str("a"))(coprod[Str]("a").get(0))
    assertResult(str("b"))(coprod[Str]("a", "b").get(1))
    assertResult(str("b"))(coprod[Str]("a", "b", "c").get(1))
    assertResult(coprod[Str]("b", "d"))(coprod[Obj]("a", coprod[Str]("b", "d"), "c").get(1))
    // assertResult(prod[Str]("b", "d"))(prod[Obj]("a", prod[Str]("b", "d"), "c").get(1,prod()).get(0))
  }

  test("coproduct [get] types") {
    assertResult(str)(coprod[Str](str.plus("a"), str).get(0, str).range)
  }

  test("coproduct structure") {
    val coproduct = int.mult(8).split(coprod[Obj](__.id(), __.plus(2), 3))
    assertResult("[int[id]|int[plus,2]|3]<=int[mult,8]-<[int[id]|int[plus,2]|3]")(coproduct.toString)
    assertResult(int.id())(coproduct.value(0))
    assertResult(int.plus(2))(coproduct.value(1))
    assertResult(int(3))(coproduct.value(2))
    assertResult(int)(coproduct.value(0).via._1)
    assertResult(int)(coproduct.value(1).via._1)
    assert(coproduct.value(2).root)
    assertResult(coprod[Int](int.id(), int.plus(2), int(3)))(coproduct.range)
  }

  test("coproduct quantifier") {
    val coproduct = int.q(2).mult(8).split(coprod[Obj](__.id(), __.plus(2), 3))
    assertResult("[int{2}[id]|int{2}[plus,2]|3]{2}<=int{2}[mult,8]-<[int{2}[id]|int{2}[plus,2]|3]")(coproduct.toString)
    assertResult(int.q(2).id())(coproduct.value(0))
    assertResult(int.q(2).plus(2))(coproduct.value(1))
    assertResult(int(3))(coproduct.value(2))
    assertResult(int.q(2))(coproduct.value(0).via._1)
    assertResult(int.q(2))(coproduct.value(1).via._1)
    assert(coproduct.value(2).root)
    assertResult(coprod[Int](int.q(2).id(), int.q(2).plus(2), int(3)).q(2))(coproduct.range)
  }

  test("coproduct [split] quantification") {
    assertResult(int.q(0, 3))(int.mult(8).split(coprod(__.id(), __.plus(8).mult(2), int(56))).merge[Int]().id().isolate)
    assertResult(int.q(0, 23))(int.mult(8).split(coprod(__.id().q(10, 20), __.plus(8).mult(2).q(2), int(56))).merge[Int]().id().isolate)
    assertResult(int.q(0, 45))(int.q(2).mult(8).q(1).split(coprod(__.id().q(10, 20), __.plus(8).mult(2).q(2), int(56))).merge[Int]().id().isolate)
    // assertResult(int.q(0))(int.q(2).mult(8).q(0).split(prod(__.id().q(10, 20), __.plus(8).mult(2).q(2), int(56))).merge().id().isolate)
  }
}