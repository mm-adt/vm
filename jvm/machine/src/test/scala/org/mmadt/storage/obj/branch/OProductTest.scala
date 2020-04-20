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
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

class OProductTest extends FunSuite {

  test("product [zero]") {
    assertResult(List.empty[Obj])(prod(str("a"),str("b")).zero().value)
    assertResult(prod())(prod(str("a"),str("b")).zero())
  }

  test("product values") {

    println(int(1) ===> int.split(prod(int(2), int.plus(int), int(3))))
    println(int.split(prod(int(2), int.plus(int), int(3))))
    println(prod(int(2), int.plus(int), int(3)) <= int.plus(2).map(prod(int(2), int.plus(int), int(3))))

    println(coprod(str, int.plus(int), int(3)))

    println(int.split(prod[Obj](int(3), int.plus(2), int)).id().merge().is(int.gt(0)))
    println(int.split(coprod(real, str, int)).id().merge())
  }

}
