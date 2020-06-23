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

package org.mmadt.language.obj.`type`

import org.mmadt.language.obj.`type`.{LstType, __}
import org.mmadt.language.obj.{Int, Lst, Obj, Poly}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

class LstTypeTest extends FunSuite with TableDrivenPropertyChecks {
  test("parallel expressions") {
    val starts: TableFor3[Obj, Lst[Obj], Obj] =
      new TableFor3[Obj, Lst[Obj], Obj](("lhs", "rhs", "result"),
        (int(1), int `,` int, int(1).q(2)),
        (int(1), int `,` int.plus(2), int(1, 3)),
        (int(1), int `,` int.plus(2).q(10), int(1, int(3).q(10))),
        (int(1).q(5), int `,` int.plus(2).q(10), int(int(1).q(5), int(3).q(50))),
        (int(int(1), int(100)), int | int, int(int(1), int(100))),
        (int(int(1), int(100)), int `,` int, int(1, 1, 100, 100)),
        (int(int(1), int(100)), int `,` int, int(int(1).q(2), int(100).q(2))),
        (int(int(1).q(5), int(100)), int `,` int.plus(2).q(10), int(int(1).q(5), int(3).q(50), int(100), int(102).q(10))),
        (int(int(1).q(5), int(100)), int | int.plus(2).q(10), int(int(1).q(5), int(100))),
        (int(1, 2), int | (int | int), int(1, 2)), // TODO: this is not computing the lst as a type
        (int(1, 2), (int | int) | int, int(1, 2)), // TODO: this is not computing the lst as a type
        //(int(1, 2), (int | int) | (int | int), int(1, 2)),
        //(int(int(1), int(2)).-<(int `,` (int -< (int | int))), strm[Obj](List(int(1), int(1) |, int(2), int(2) |))),
        (int(1), str | int, int(1)),
        //(strm(List(int(1), str("a"))).-<(str | int), strm(List(zeroObj | int(1), str("a") | zeroObj))),
      )
    forEvery(starts) { (lhs, rhs, result) => {
      // assertResult(result)(new mmlangScriptEngineFactory().getScriptEngine.eval(s"[start ${lhs}]${rhs}"))
      assertResult(result)(rhs.asInstanceOf[LstType[Obj]].exec(lhs))
      assertResult(result)(lhs.compute(__.via(__, rhs.asInstanceOf[LstType[Obj]])))
    }
    }
  }

  test("parallel [get] types") {
    assertResult(str)((str.plus("a") | str).get(0, str).range)
  }

  test("parallel structure") {
    val poly: Poly[Obj] = int.mult(8).split(__.id() | __.plus(2) | 3)
    assertResult("(int[id]|int[plus,2]|3)<=int[mult,8]-<(int[id]|int[plus,2]|3)")(poly.toString)
    assertResult(int.id())(poly.glist.head)
    assertResult(int.plus(2))(poly.glist(1))
    assertResult(int(3))(poly.glist(2))
    assertResult(int)(poly.glist.head.via._1)
    assertResult(int)(poly.glist(1).via._1)
    assert(poly.glist(2).root)
    assertResult(int.id() | int.plus(2) | int(3))(poly.range)
  }

  test("parallel quantifier") {
    val poly: Poly[Obj] = int.q(2).mult(8).split(__.id() | __.plus(2) | 3)
    assertResult("(int{2}[id]|int{2}[plus,2]|3)<=int{2}[mult,8]-<(int{2}[id]|int{2}[plus,2]|3)")(poly.toString)
    assertResult(int.q(2).id())(poly.glist.head)
    assertResult(int.q(2).plus(2))(poly.glist(1))
    assertResult(int(3))(poly.glist(2))
    assertResult(int.q(2))(poly.glist.head.via._1)
    assertResult(int.q(2))(poly.glist(1).via._1)
    assert(poly.glist(2).root)
    assertResult(int.q(2).id() | int.q(2).plus(2) | int(3))(poly.range)
  }

  test("parallel [split] quantification") {
    assertResult(int)(int.mult(8).split(__.id() | __.plus(8).mult(2) | int(56)).merge[Int].id().isolate)
    assertResult(int.q(1, 20))(int.mult(8).split(__.id().q(10, 20) | __.plus(8).mult(2).q(2) | int(56)).merge[Int].id().isolate)
    assertResult(int.q(1, 40))(int.q(2).mult(8).q(1).split(__.id().q(10, 20) | __.plus(8).mult(2).q(2) | int(56)).merge[Int].id().isolate)
    assertResult(int(56))(int.q(2).mult(8).q(0).split(__.id().q(10, 20) | __.plus(8).mult(2).q(2) | int(56)).merge[Obj].id().isolate)
  }
}
