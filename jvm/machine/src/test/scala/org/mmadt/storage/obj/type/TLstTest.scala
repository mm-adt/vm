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

package org.mmadt.storage.obj.`type`

import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.value.{IntValue, LstValue, StrValue, Value}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VLst
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}

class TLstTest extends FunSuite with TableDrivenPropertyChecks {

  test("...") {
    println((Nil ++ "a" ++ "b" ++ "c").tail)
    println(lst[StrValue].append("a").append("b").append("c").tail())
  }


  test("lst encode/decode") {
    val starts: TableFor2[LstValue[Value[Obj]], List[Value[Obj]]] =
      new TableFor2[LstValue[Value[Obj]], List[Value[Obj]]](("lst", "list"),
        (new VLst[Value[Obj]](), List.empty),
        (new VLst[Value[Obj]]().append("a"), List(str("a"))),
        (new VLst[Value[Obj]]().append("a").append("a"), List(str("a"), str("a"))),
        (new VLst[Value[Obj]]().append("a").append("b").append("c"), List(str("a"), str("b"), str("c"))),
        (new VLst[Value[Obj]]().append("a").append(new VLst[Value[Obj]]().append("b").append("d")).append("c"), List[Value[Obj]](str("a"), new VLst[Value[Obj]]().append(str("b")).append(str("d")), str("c"))),
      )
    forEvery(starts) { (alst, blist) => {
      assertResult(alst.value)(blist)
      assertResult(alst)(new VLst[Value[Obj]](blist))
      if (blist.nonEmpty) {
        assertResult(alst.head())(blist.head)
        assertResult(alst.value.head)(blist.head)
        assertResult(alst.tail().value)(blist.tail)
        assertResult(alst.value.tail)(blist.tail)
      }
    }
    }
  }

  test("lst head/tail toString") {
    println(List(1, 2, 3).head)
    println(List(1, 2, 3).tail)
    println(lst[IntValue].append(1).append(2).append(3).tail())
  }

}