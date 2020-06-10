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

import org.mmadt.language.LanguageException
import org.mmadt.language.obj._
import org.mmadt.language.obj.op.map.{MultOp, PlusOp}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TIntTest extends FunSuite {
  test("canonical int") {
    assert(int.root)
    assertResult(base)(int.via)
  }

  test("derived int") {
    assert(!int.plus(2).root)
    assertResult(int)(int.plus(2).via._1)
    assertResult(PlusOp(2))(int.plus(2).via._2)
    //
    assert(!int.plus(2).mult(5).root)
    assertResult(int.plus(2))(int.plus(2).mult(5).via._1)
    assertResult(MultOp(5))(int.plus(2).mult(5).via._2)
  }

  test("int type") {
    assertResult("int")(int.name)
    assertResult(int.plus(int))(int + int)
    assertResult(int.plus(int(-4)))(int + -4)
    assertResult(int.mult(int(10)))(int * 10)
    assertResult(int.plus(int.plus(int)))(int + (int + int))
    assertResult(int.plus(int.plus(int.mult(int))))(int + (int + (int * int)))
  }

  test("canonical int type") {
    /*assertThrows[LanguageException] {
      bool <= int
    }*/
    assert(int.q(?).test((int.q(?) <= int.is(int.gt(5))).range))
    assert(!int.test(bool))
    assert(!bool.test(int))
  }

  test("type/type patterns on atomic objs") {
    assert(int.test(int))
    assert(bool.test(bool))
    assert(str.test(str))
    //
    assert(!int.plus(2).test(int))
    assert(!bool.test(bool.is(btrue)))
    assert(!str.test(str.plus("a")))
    //
    assert(int.plus(2).test(int.plus(2)))
    assert(str.plus("a").test(str.plus("a")))
    //
    assert(!int.named("nat").test(int))
    assert(!int.named("nat").test(int.is(int.gt(0))))
    assert(!int.named("nat").test(int.named("nat").is(int.gt(0))))
  }

  test("value/type patterns on atomic objs") {
    assert(str("m").test(str("m")))
    assert(!str("m").test(int(2)))
    assert(str("m").test(str))
    assert(!int.test(int(3)))
    assert(int(3).test(int))
  }

  test("value/type patterns on refinement types") {
    assert(int(6).test(int))
    assert(!int(6).test(int.q(0)))
    assert(int.plus(2).test(int.plus(2)))
    assert(!int.plus(2).test(int.plus(3)))
  }
}