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

package org.mmadt.storage.obj.value.dvalue

import org.mmadt.language.obj.value.IntValue
import org.mmadt.language.obj.{Lst, Obj, Str}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}

class ALstTest extends FunSuite with TableDrivenPropertyChecks {

  /*test("...") {
    println((Nil ++ "a" ++ "b" ++ "c").tail)
    println(lst[Str].append("a").append("b").append("c").tail())
  }*/

  test("lst encode/decode") {
    val starts: TableFor2[Lst[_ <: Obj], List[_ <: Obj]] =
      new TableFor2(("lst", "list"),
        (lst, List.empty),
        (lst[Str].append("a"), List(str("a"))),
        (lst[Str].append("a").append("a"), List(str("a"), str("a"))),
        (lst[Str].append("c").append("b").append("a"), List(str("a"), str("b"), str("c"))),
        (lst[Obj].append("c").append(lst[Str].append("b").append("d")).append("a"), List(str("a"), Lst.encode(List(str("d"), str("b"))), str("c"))),
      )
    forEvery(starts) { (alst, blist) => {
      assertResult(Lst.decode(alst))(blist)
      assertResult(alst)(Lst.encode(blist))
      if (blist.nonEmpty) {
        assertResult(alst.head())(blist.head)
        assertResult(Lst.decode(alst).head)(blist.head)
        assertResult(Lst.decode(alst.tail()))(blist.tail)
        assertResult(Lst.decode(alst).tail)(blist.tail)
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