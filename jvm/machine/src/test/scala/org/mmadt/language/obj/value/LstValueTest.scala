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

package org.mmadt.language.obj.value

import org.mmadt.language.obj.Obj._
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.{Lst, Obj, Str}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.storage.StorageFactory._
import org.scalatest.prop.{TableFor2, TableFor4}

class LstValueTest extends BaseInstTest() {

  test("lst test") {
    assert(("a" | "b").q(0).test(str.q(0)))
    //
    assert(("a" | "b").test("a" | "b"))
    assert(("a" | "b").test("a" |))
    assert(("a" |).test("a" | "b"))
    //
    assert(("a" | ("b" | "c")).test("a" | ("b" | "c")))
    assert(("a" | ("b" | "c")).test("a" | ("b" |)))
    assert(("a" | ("b" |)).test("a" | ("b" | "c")))
    // TODO: think on the semantics of this --- assert(("z" | ("b" |)).test("a" | ("b" | "c")))
    //
    assertResult(btrue)(lst.zero.eqs(lst))
    assertResult(lst)(lst ==> lst.is(lst.eqs(lst.zero)))
  }

  test("parallel [tail][head][last] values") {
    val starts:TableFor2[Lst[Str], List[Obj]] =
      new TableFor2[Lst[Str], List[Obj]](("parallel", "projections"),
        (lst, List.empty),
        ("a" |, List(str("a"))),
        ("a" `;` "b", List(str("a"), str("b"))),
        ("a" `;` "b" `;` "c", List(str("a"), str("b"), str("c"))),
        ("a" `;`("b" `;` "d") `;` "c", List("a", "b" `;` "d", "c")),
      )
    forEvery(starts) { (alst, blist) => {
      assertResult(alst.glist)(blist)
      if (blist.nonEmpty) {
        assertResult(alst.last)(blist.last)
        assertResult(alst.head)(blist.head)
        assertResult(alst.g._2.head)(blist.head)
        assertResult(alst.g._2.last)(blist.last)
        assertResult(alst.tail.g._2)(blist.tail)
        assertResult(alst.g._2.tail)(blist.tail)
      }
    }
    }
  }


  test("serial value/type checking") {
    val starts:TableFor2[Lst[_ <: Obj], Boolean] =
      new TableFor2[Lst[_ <: Obj], Boolean](("serial", "isValue"),
        (lst, false),
        ("a" `;` "b", true),
        ("a" `;` "b" `;` "c" `;` "d", true),
        (str `;` "b", false),
      )
    forEvery(starts) { (serial, bool) => {
      assertResult(bool)(serial.isInstanceOf[Value[Obj]])
    }
    }
  }

  test("serial [put]") {
    val starts:TableFor4[Lst[StrValue], Int, StrValue, Lst[StrValue]] =
      new TableFor4[Lst[StrValue], Int, StrValue, Lst[StrValue]](("serial", "key", "value", "newProd"),
        // (lst, 0, "a", "a" `;`),
        (str("b") `;`, 0, "a", "a" `;` "b"),
        ("a" `;` "c", 1, "b", "a" `;` "b" `;` "c"),

        ("a" `;` "b", 2, "c", "a" `;` "b" `;` "c"),
        //(str("a")/"b", 2, str("c")/ "d", str("a")/ "b"/ (str("c")/ "d")),
        //
        //(`/x`, 0, str, (str /).via(/, PutOp[Int, Str](0, str))),
        //(/, int.is(int.gt(0)), "a", /[Obj].via(/, PutOp[Int, Str](int.is(int.gt(0)), "a"))),
      )
    forEvery(starts) { (serial, key, value, newProduct) => {
      assertResult(newProduct)(serial.put(key, value))
      assertResult(newProduct)(PutOp[Obj, Obj](key, value).exec(serial))
    }
    }
  }

  test("parallel [get] values") {
    assertResult(str("a"))((str("a") |).get(0))
    assertResult(str("b"))((str("a") `;` "b").get(1))
    assertResult(str("b"))((str("a") `;` "b" `;` "c").get(1))
    assertResult("b" `;` "d")(("a" `;`("b" `;` "d") `;` "c").get(1))
  }

}
