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

import org.mmadt.TestUtil
import org.mmadt.language.obj.Obj.{intToInt, stringToStr}
import org.mmadt.language.obj.`type`.LstTypeTest._
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.language.obj.{Lst, Obj}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.storage.StorageFactory._
import org.scalatest.prop.TableFor3

object LstTypeTest {
  private val intArrayObj: Type[_] = __("tarr_i") <= lst.branch(lst.is(merge.count.eqs(0)) `|` lst.branch(is(head.a(int)) `;` is(tail.a(__("tarr_i")))))
  private val intArrayStr: String = "tarr_i<=lst[[is>-[count]==0]|[[is,[head][a,int]];[is,[tail][a,tarr_i]]]]"
  private val INT_ARRAY_MODEL: Model = ModelOp.EMPTY.defining(intArrayObj)
  ///////////
  private val intStrArrayObj: Type[_] = __("tarr_is") <= lst.branch(is(merge.count.eqs(0)) `|` branch(is(head.a(int)) `;` is(tail.head.a(str)) `;` is(tail.tail.a(__("tarr_is")))))
  private val intStrArrayStr: String = "tarr_is<=lst[[is>-[count]==0]|[[is,[head][a,int]];[is,[tail][head][a,str]];[is,[tail][tail][a,tarr_is]]]]"
  private val INT_STR_ARRAY_MODEL: Model = ModelOp.EMPTY.defining(intStrArrayObj)

}
class LstTypeTest extends BaseInstTest(
  testSet(";-lst int array type", INT_ARRAY_MODEL,
    comment("int array mmlang/mmscala"),
    testing(intArrayObj, __, intArrayObj, intArrayStr),
    comment("int array passing"),
    testing(lst(), a(__("tarr_i")), btrue, "( )[a,tarr_i]"),
    testing(1 `;`, a(__("tarr_i")), btrue, "(1)[a,tarr_i]"),
    testing(1 `;` 2, a(__("tarr_i")), btrue, "(1;2)[a,tarr_i]"),
    testing(1 `;` 2 `;` 3, a(__("tarr_i")), btrue, s"(1;2;3) => [a,tarr_i]"),
    testing(1 `;` 2 `;` 3 `;` 4, a(__("tarr_i")), btrue, s"(1;2;3;4) => [a,tarr_i]"),
    comment("int array failing"),
    testing("a", a(__("tarr_i")), bfalse, "'a'[a,tarr_i]"),
    testing("a" `;`, a(__("tarr_i")), bfalse, "('a')[a,tarr_i]"),
    testing(1 `;` "a", a(__("tarr_i")), bfalse, "(1;'a')[a,tarr_i]"),
    testing(1 `;` 2 `;` "a", a(__("tarr_i")), bfalse, "(1;2;'a') => [a,tarr_i]"),
    testing(1 `;` 2 `;` "a" `;` 4, a(__("tarr_i")), bfalse, "(1;2;'a';4) => [a,tarr_i]"),
  ), testSet(";-lst int/str array type", INT_STR_ARRAY_MODEL,
    comment("int/str array mmlang/mmscala"),
    testing(intStrArrayObj, __, intStrArrayObj, intStrArrayStr),
    comment("int/str array passing"),
    testing(lst(), a(__("tarr_is")), btrue, "( )[a,tarr_is]"),
    testing(1 `;` "a", a(__("tarr_is")), btrue, "(1;'a')[a,tarr_is]"),
    testing(1 `;` "a", a(__("tarr_is")), btrue, "(1;'a')[a,tarr_is]"),
    testing(1 `;` "a" `;` 2 `;` "b", a(__("tarr_is")), btrue, "(1;'a';2;'b')[a,tarr_is]"),
    testing(1 `;` "a" `;` 2 `;` "b" `;` 3 `;` "c", a(__("tarr_is")), btrue, "(1;'a';2;'b';3;'c')[a,tarr_is]"),
    comment("int/str array failing"),
    testing("a", a(__("tarr_is")), bfalse, "'a'[a,tarr_is]"),
    testing("a" `;` "b", a(__("tarr_is")), bfalse, "('a';'b')[a,tarr_is]"),
    testing(1 `;` 2, a(__("tarr_is")), bfalse, "(1;2)[a,tarr_is]"),
    testing(1 `;` "a" `;` 2 `;` 2, a(__("tarr_is")), bfalse, "(1;'a';2;2)[a,tarr_is]"),
    testing(1 `;` "a" `;` 2 `;` "b" `;` "c" `;` "c", a(__("tarr_is")), bfalse, "(1;'a';2;'b';'c';'c')[a,tarr_is]"),


  )) {

  test("mmlang and mmscala strings") {
    println(intArrayObj)
    println(BaseInstTest.engine.eval(intArrayStr))
    println(INT_ARRAY_MODEL)
    assertResult(intArrayObj)(BaseInstTest.engine.eval(intArrayStr))
    assertResult(BaseInstTest.engine.eval(intArrayStr))(intArrayObj)
    assertResult(intArrayObj.toString)(BaseInstTest.engine.eval(intArrayStr).toString)
    //////////////////////
    println("----\n\n")
    println(intStrArrayObj)
    println(BaseInstTest.engine.eval(intStrArrayStr))
    println(INT_STR_ARRAY_MODEL)
    assertResult(intStrArrayObj)(BaseInstTest.engine.eval(intStrArrayStr))
    assertResult(BaseInstTest.engine.eval(intStrArrayStr))(intStrArrayObj)
    assertResult(intStrArrayObj.toString)(BaseInstTest.engine.eval(intStrArrayStr).toString)
  }

  ///////// MOVE BELOW INTO TABLE TEST RIG

  test("lst type token") {
    assertResult("lst")(lst.toString)
    assert(lst.isInstanceOf[LstType[_]])
    assert(lst.test(lst))
    assert(!lst.test(rec))
    assert(!lst.test(int))
    assert((str("a") `,` "b").test(lst))
    assert((str("a") `|` "b").test(lst))
    assert((str("a") `;` "b").test(lst))
    assert(!(str("a") `;` "b").test(lst.q(20)))
    assert((str("a") `;` "b").test(lst.q(0, 20)))
  }

  test("lst type basics") {
    assert((int(1) `;` 2).test(int `;` int))
    //assert((int(1) `,` 2).test(int.q(2)))
  }


  test("parallel expressions") {
    val starts: TableFor3[Obj, Lst[Obj], Obj] =
      new TableFor3[Obj, Lst[Obj], Obj](("lhs", "rhs", "result"),
        (int(1), int `,` int, int(1).q(2)),
        (int(1), int `,` int.plus(2), int(1, 3)),
        (int(1), int `,` int.plus(2).q(10), int(1, int(3).q(10))),
        (int(1).q(5), int `,` int.plus(2).q(10), int(int(1).q(5), int(3).q(50))),
        (int(1, 100), int | int, int(int(1), int(100))),
        (int(int(1), int(100)), int `,` int, int(1, 1, 100, 100)),
        (int(int(1), int(100)), int `,` int, int(int(1).q(2), int(100).q(2))),
        (int(int(1).q(5), int(100)), int `,` int.plus(2).q(10), int(int(1).q(5), int(3).q(50), int(100), int(102).q(10))),
        (int(int(1).q(5), int(100)), int | int.plus(2).q(10), int(int(1).q(5), int(100))),
        (int(1, 2), int | (int | int), int(1, 2)),
        (int(1, 2), (int | int) | int, int(1, 2)),
        (int(1, 2), (int | int) | obj, int(1, 2)),
        (int(1, 2), (str | str) | str, zeroObj),
        ((1 `;` 2), ((int `;` int) | str), (1 `;` 2)),
        (1, int | str, 1),
        // (1, str | int, 1),
      )
    forEvery(starts) { (lhs, rhs, result) => TestUtil.evaluate(lhs, split(rhs).merge[Obj], result, compile = false) }
  }

  test("parallel [split] quantification") {
    assertResult(int)(int.mult(8).split(id | plus(8).mult(2) | int(56)).merge.id.rangeObj)
    assertResult(int.q(10, 20))(int.mult(8).split(id.q(10, 20) | plus(8).mult(2).q(2) | int(56)).merge.id.rangeObj)
    assertResult(int.q(20, 40))(int.q(2).mult(8).q(1).split(id.q(10, 20) | plus(8).mult(2).q(2) | int(56)).merge.id.rangeObj)
    assertResult(zeroObj)(int.q(2).mult(8).q(0).split(id.q(10, 20) | plus(8).mult(2).q(2) | int(56)).merge.id.rangeObj)
  }
}
