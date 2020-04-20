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

package org.mmadt.storage.obj.branch

import org.mmadt.language.obj._
import org.mmadt.language.obj.branch._
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}

class OProductTest extends FunSuite with TableDrivenPropertyChecks {

  test("product [zero]") {
    assertResult(List.empty[Obj])(prod(str("a"), str("b")).zero().value)
    assertResult(prod())(prod(str("a"), str("b")).zero())
  }

  test("product [tail][head] values") {
    val starts: TableFor2[Product[Obj], List[Value[Obj]]] =
      new TableFor2[Product[Obj], List[Value[Obj]]](("lst", "list"),
        (prod(), List.empty),
        (prod("a"), List(str("a"))),
        (prod("a", "b"), List(str("a"), str("b"))),
        (prod("a", "b", "c"), List(str("a"), str("b"), str("c"))),
        (prod("a", prod[Str]("b", "d"), "c"), List(str("a"), prod[Str]("b", "d"), str("c"))),
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

  test("product play") {
    println(int(1) ===> int.split(prod(int(2), int.plus(int), int(3))))
    println(int.split(prod(int(2), int.plus(int), int(3))))
    println(prod(int(2), int.plus(int), int(3)) <= int.plus(2).map(prod(int(2), int.plus(int), int(3))))
    println(coprod(str, int.plus(int), int(3)))
    println(int.split(prod[Obj](int(3), int.plus(2), int)).id().merge().is(int.gt(0)))
    println(int.split(coprod(real, str, int)).id().merge())
  }

}
