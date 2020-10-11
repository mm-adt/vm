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

import org.mmadt.language.LanguageException
import org.mmadt.language.obj.Obj.intToInt
import org.mmadt.language.obj.op.trace.ModelOp.{MM, MMX, NONE}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class IntTypeTest extends BaseInstTest(
  testSet("int table test", List(NONE, MM, MMX),
    comment("canonical"),
    testing(int, __, int, "int"),
    testing(6, __, 6, "6"),
    testing(-2, __, -2, "-2"),
    testing(6, int, 6, "6"),
    testing(-2, int, -2, "-2"),
    testing(6.q(5), int.q(5), 6.q(5), "6{5}"),
    testing(6.q(5), int.q(2, 5), 6.q(5), "6{5}"),
    testing(-2.q(-3), int.q(-3), -2.q(-3), "-2{-3}"),
    comment("exceptions"),
    // testing(6.q(5), int.q(10), LanguageException.typingError(int.q(5), int.q(10)), "6{5} => int{10}"),
    // testing(6.q(2,4), int.q(6,10), LanguageException.typingError(int.q(2,4), int.q(6,10)), "6{2,4} => int{6,10}"),
  )) {

  test("int type test") {
    // type ~ value
    assert(!int.test(int(3)))
    assert(!int.plus(10).plus(-5).plus(-5).test(int(3).plus(10).plus(-5).plus(-5)))
    // type ~ type
    assert(int.test(int))
    assert(int.plus(2).test(int.plus(2)))
    assert(int.is(int.gt(2)).test(int.is(int.gt(2))))
    assert(!int.test(str))
    assert(int.test(int.plus(2)))
    assert(int.plus(2).test(int))
    assert(!int.test(str.map(int(2))))
    assert(!int.test(str.map(int)))
  }

  test("int infix operators") {
    assertResult("bool<=int[plus,2][gt,4]")((int + 2 > 4).toString)
    assertResult("int{?}<=int[plus,2][is,bool<=int[gt,4]]")((int + 2 is int.gt(4)).toString)
  }
  test("int: refinement types") {
    assertResult("int[is,bool<=int[gt,5]]")((int <= int.is(int.gt(5))).toString())
    assertResult(int(5))(int(5) ==> (int <= int.is(int.gt(4))))
    assertResult(int(5))(int(5) ==> (int.is(int.gt(4))))
    //intercept[LanguageException]{
    int(4) ==> (int <= int.is(int.gt(4)))
    //}
    //intercept[LanguageException] {
    int(6) ==> int.q(0).is(int.gt(5))
    //}
    intercept[LanguageException] {
      int(6) ==> int.q(2).is(int.gt(5))
    }
    intercept[LanguageException] {
      int(6) ==> int.q(15, 46).is(int.gt(5))
    }
  }
  test("int: deep nest") {
    assertResult(int(2))(int(1) ==> int.plus(1))
    assertResult(int(3))(int(1) ==> int.plus(int.plus(1)))
    assertResult(int(4))(int(1) ==> int.plus(int.plus(int.plus(1))))
    assertResult(int(5))(int(1) ==> int.plus(int.plus(int.plus(int.plus(1)))))
    assertResult(int(6))(int(1) ==> int.plus(int.plus(int.plus(int.plus(int.plus(1))))))
  }
}