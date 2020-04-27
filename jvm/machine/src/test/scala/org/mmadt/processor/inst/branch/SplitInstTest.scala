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

package org.mmadt.processor.inst.branch

import org.mmadt.language.obj.{Int, Obj}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.TableDrivenPropertyChecks

class SplitInstTest extends FunSuite with TableDrivenPropertyChecks {

  test("lineage preservation (products)") {
    assertResult(int(321))(int(1) ===> int.plus(100).plus(200).split(prod(int, bool)).merge[Int]().plus(20))
    assertResult(int.plus(100).plus(200).split(coprod(int, bool)).merge[Int]().plus(20))(int ===> int.plus(100).plus(200).split(coprod(int, bool)).merge[Int]().plus(20))
    assertResult(prod[Obj](1, 101, 301, 321))((int(1) ===> int.plus(100).plus(200).split(prod(int, bool)).merge[Int]().plus(20)).path())
  }

  test("lineage preservation (coproducts)") {
    println(int.plus(100).plus(200).split(coprod(int, int.plus(2))).merge[Int]().plus(20))
    assertResult(int(321, 323))(int(1) ===> int.plus(100).plus(200).split(coprod(int, int.plus(2))).merge[Int]().plus(20))
    assertResult(int.plus(100).plus(200).split(coprod(int, int.plus(2))).merge[Int]().plus(20))(int ===> int.plus(100).plus(200).split(coprod(int, int.plus(2))).merge[Int]().plus(20))
    assertResult(strm(List(
      prod[Obj](1, 101, 301, 321),
      prod[Obj](1, 101, 301, 303, 323))))(int(1) ===> int.plus(100).plus(200).split(coprod(int, int.plus(2))).merge[Int]().plus(20).path())
  }

  test("quantifiers") {
    assertResult(int(int(1), int(2)))(coprod[Int](1, 2).merge())
    assertResult(int(int(1).q(10), int(2).q(10)))(coprod[Int](1, 2).q(10).merge())
    assertResult(int(int(1).q(10), int(2).q(50)))(coprod[Int](1, int(2).q(5)).q(10).merge())
    assertResult(int(int(1).q(4, 10), int(2).q(20, 50)))(coprod[Int](1, int(2).q(5)).q(4, 10).merge())
  }

}
