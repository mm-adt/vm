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
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.{Int, Lst, Obj}
import org.mmadt.storage.StorageFactory
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2, TableFor3, TableFor4}

class LstValueTest extends FunSuite with TableDrivenPropertyChecks {

  test("lst test") {
    assert(("a" | "b").q(0).test(str.q(0)))
    //
    assert(("a" | "b").test("a" | "b"))
    assert(!("a" | "b").test("a" |))
    assert(!("a" |).test("a" | "b"))
    //
    assert(("a" | ("b" | "c")).test("a" | ("b" | "c")))
    //assert(("a" | ("b" | "c")).test("a" | ("b" |)))
    assert(!("a" | ("b" |)).test("a" | ("b" | "c")))
  }

  test("basic poly") {
    assertResult(str("a"))(("a" | "b" | "c").head())
    assertResult("b" | "c")(("a" | "b" | "c").tail())

    assertResult(str("a"))(("a" `;` "b" `;` "c").head())
    assertResult("b" `;` "c")(("a" `;` "b" `;` "c").tail())
  }

  test("parallel expressions") {
    val starts: TableFor3[Obj, Obj, Obj] =
      new TableFor3[Obj, Obj, Obj](("lhs", "rhs", "result"),
        (int(1), __ -< (int `,` int), int(1) `,` int(1)),
        (int(1), __ -< (int `,` int.plus(2)), int(1) `,` int(3)),
        (int(1), __ -< (int `,` int.plus(2).q(10)), int(1) `,` int(3).q(10)),
        (int(1).q(5), __ -< (int `,` int.plus(2).q(10)), (int(1) `,` int(3).q(10)).q(5)),
        (int(1).q(5), __ -< (int `,` int.plus(2).q(10)) >-, int(int(1).q(5), int(3).q(50))),
        (int(1, 100), __ -< (int | int) >-, int(int(1), int(100))),
        (int(1, 100), __ -< (int `,` int) >-, int(1, 1, 100, 100)),
        (int(1, 100), __ -< (int `,` int) >-, int(int(1).q(2), int(100).q(2))),
        (int(int(1).q(5), 100), __ -< (int `,` int.plus(2).q(10)) >-, int(int(1).q(5), int(3).q(50), int(100), int(102).q(10))),
        (int(int(1).q(5), 100), __ -< (int | int.plus(2).q(10)) >-, int(int(1).q(5), int(100))),
        (int(1, 2), __ -< (int | (int -< (int | int))), StorageFactory.strm[Obj](List[Obj](int(1) `|`, int(2) `|`))),
        (int(1, 2), __ -< (int `,` (int -< (int | int))), StorageFactory.strm[Obj](List(int(1) `,` (int(1) |), int(2) `,` (int(2) |)))),
        (int(1), __ -< (str | int), zeroObj | int(1)),
        //(strm(List(int(1), str("a"))).-<(str | int), strm(List(zeroObj | int(1), str("a") | zeroObj))),
      )
    forEvery(starts) { (lhs, rhs, result) => {
      // assertResult(result)(new mmlangScriptEngineFactory().getScriptEngine.eval(s"[start,${lhs}] ${rhs}"))
      assertResult(result)(lhs `=>` rhs)
    }
    }
  }


  test("parallel [tail][head][last] values") {
    val starts: TableFor2[Lst[Obj], List[Obj]] =
      new TableFor2[Lst[Obj], List[Obj]](("parallel", "projections"),
        (lst, List.empty),
        ("a" |, List(str("a"))),
        ("a" | "b", List(str("a"), str("b"))),
        ("a" | "b" | "c", List(str("a"), str("b"), str("c"))),
        ("a" | ("b" | "d") | "c", List(str("a"), "b" | "d", str("c"))),
      )
    forEvery(starts) { (alst, blist) => {
      assertResult(alst.glist)(blist)
      if (blist.nonEmpty) {
        assertResult(alst.last())(blist.last)
        assertResult(alst.head())(blist.head)
        assertResult(alst.g._2.head)(blist.head)
        assertResult(alst.g._2.last)(blist.last)
        assertResult(alst.tail().g._2)(blist.tail)
        assertResult(alst.g._2.tail)(blist.tail)
      }
    }
    }
  }

  test("scala type constructor") {
    assertResult("('a'|'b')")(("a" | "b").toString)
  }

  test("parallel [get] values") {
    assertResult(str("a"))((str("a") |).get(0))
    assertResult(str("b"))((str("a") | "b").get(1))
    assertResult(str("b"))((str("a") | "b" | "c").get(1))
    assertResult("b" | "d")(("a" | ("b" | "d") | "c").get(1))
  }

  test("serial value/type checking") {
    val starts: TableFor2[Lst[Obj], Boolean] =
      new TableFor2[Lst[Obj], Boolean](("serial", "isValue"),
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
    val starts: TableFor4[Lst[Obj], Int, Obj, Lst[Obj]] =
      new TableFor4[Lst[Obj], Int, Obj, Lst[Obj]](("serial", "key", "value", "newProd"),
        // (lst, 0, "a", "a" `;`),
        ("b" `;`, 0, "a", "a" `;` "b"),
        ("a" `;` "c", 1, "b", "a" `;` "b" `;` "c"),
        ("a" `;` "b", 2, "c", "a" `;` "b" `;` "c"),
        //(str("a")/"b", 2, str("c")/ "d", str("a")/ "b"/ (str("c")/ "d")),
        //
        //(`/x`, 0, str, (str /).via(/, PutOp[Int, Str](0, str))),
        //(/, int.is(int.gt(0)), "a", /[Obj].via(/, PutOp[Int, Str](int.is(int.gt(0)), "a"))),
      )
    forEvery(starts) { (serial, key, value, newProduct) => {
      assertResult(newProduct)(serial.put(key, value))
      assertResult(newProduct)(PutOp(key, value).exec(serial))
    }
    }
  }

}
