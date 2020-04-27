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

package org.mmadt.storage.obj.value

import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.value.{LstValue, Value}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2, TableFor3}

class VLstTest extends FunSuite with TableDrivenPropertyChecks {

  test("lst value toString") {
    assertResult("[ ]")(vlst.toString)
  }

  test("lst [tail][head] values") {
    val starts: TableFor2[LstValue[Value[Obj]], List[Value[Obj]]] =
      new TableFor2[LstValue[Value[Obj]], List[Value[Obj]]](("lst", "list"),
        (vlst(), List.empty),
        (vlst[Value[Obj]]().append("a"), List(str("a"))),
        (vlst[Value[Obj]]().append("a").append("a"), List(str("a"), str("a"))),
        (vlst[Value[Obj]]().append("a").append("b").append("c"), List(str("a"), str("b"), str("c"))),
        (vlst[Value[Obj]]().append("a").append(vlst[Value[Obj]]().append("b").append("d")).append("c"), List[Value[Obj]](str("a"), vlst().append(str("b")).append(str("d")), str("c"))),
      )
    forEvery(starts) { (alst, blist) => {
      assertResult(alst.value)(blist)
      assertResult(alst)(vlst[Value[Obj]](value = blist))
      if (blist.nonEmpty) {
        assertResult(alst.head())(blist.head)
        assertResult(alst.value.head)(blist.head)
        //assertResult(alst.tail().value)(blist.tail)
        assertResult(alst.value.tail)(blist.tail)
      }
    }
    }
  }

  test("lst [plus] values") {
    val starts: TableFor3[LstValue[Value[Obj]], LstValue[Value[Obj]], List[Value[Obj]]] =
      new TableFor3[LstValue[Value[Obj]], LstValue[Value[Obj]], List[Value[Obj]]](("lstA", "lstB", "list"),
        (vlst(), vlst(), List.empty),
        (vlst(), vlst().zero(), List.empty),
        (vlst().zero(), vlst().zero(), List.empty),
        (vlst(str("a")), vlst(str("b")), List(str("a"), str("b"))),
        (vlst(), vlst(str("b")), List(str("b"))),
        (vlst(str("a")), vlst(), List(str("a"))),
        (vlst(str("a")), vlst(), List(str("a"))),
        (vlst(str("a"), str("b")), vlst(str("c")), List(str("a"), str("b"), str("c"))),
      )
    forEvery(starts) { (alst, blst, list) => {
      assertResult(list)(alst.plus(blst).value)
    }
    }
  }
}
