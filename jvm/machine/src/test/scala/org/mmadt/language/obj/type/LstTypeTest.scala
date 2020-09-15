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
import org.mmadt.language.obj.Obj.{intToInt, stringToStr, symbolToToken}
import org.mmadt.language.obj.`type`.LstTypeTest._
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp
import org.mmadt.language.obj.op.trace.ModelOp.{MM, Model}
import org.mmadt.language.obj.{Lst, Obj}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.storage.StorageFactory._
import org.scalatest.prop.TableFor3

object LstTypeTest {
  private val intArrayObj:Type[_] = 'tarr_i <= lst.branch(lst.is(merge.count.eqs(0)) `|` lst.branch(is(head.a(int)) `;` is(tail.a('tarr_i))))
  private val intArrayStr:String = "tarr_i<=lst[[is>-[count]==0]|[[is,[head][a,int]];[is,[tail][a,tarr_i]]]]"
  private val INT_ARRAY_MODEL:Model = ModelOp.MM.defining(intArrayObj)
  ///////////
  private val intStrArrayObj:Type[_] = 'tarr_is <= lst.branch(is(merge.count.eqs(0)) `|` branch(is(head.a(int)) `;` is(tail.head.a(str)) `;` is(tail.tail.a('tarr_is))))
  private val intStrArrayStr:String = "tarr_is<=lst[[is>-[count]==0]|[[is,[head][a,int]];[is,[tail][head][a,str]];[is,[tail][tail][a,tarr_is]]]]"
  private val INT_STR_ARRAY_MODEL:Model = ModelOp.MM.defining(intStrArrayObj)

}
class LstTypeTest extends BaseInstTest(
  testSet("lst ctype basics",
    testing(lst(), a(lst), true, "()[a,lst]"),
    testing(lst(), a(rec), false, "()[a,rec]"),
    testing((str("a") `,` "b"), a(lst), true, "('a','b')[a,lst]"),
    testing((str("a") `|` "b"), a(lst), true, "('a'|'b')[a,lst]"),
    testing((str("a") `;` "b"), a(lst), true, "('a';'b')[a,lst]"),
    testing((str("a") `,` "b"), a(lst.q(10)), false, "('a','b')[a,lst{10}]"),
    testing((str("a") `,` "b"), a(lst.q(0, 10)), true, "('a','b')[a,lst{0,10}]"),
    testing((str("a") `;` "b"), a(str `;` str), true, "('a';'b')[a,(str;str)]"),
    testing((str("a") `,` "b"), a(str `,` str), true, "('a','b')[a,(str,str)]"),
  ), testSet(",-lst basics", MM,
    comment(",-lst vs. ,-lst"),
    testing(1 `,` 2, a(int `,` int), true, "(1,2)[a,(int,int)]"),
    testing(1 `,` 2, a(2 `,` int), true, "(1,2)[a,(2,int)]"),
    testing(1 `,` 2, a(lst(int.q(2))), true, "(1,2)[a,(int{2})]"),
    testing(1 `,` 2 `,` 3, a(int `,` int), false, "(1,2,3)[a,(int,int)]"),
    testing(1 `,` 2 `,` 3, a(int `,` int.q(2)), true, "(1,2,3)[a,(int,int{2})]"),
    testing(1 `,` 2 `,` 3, a(int `,` 2 `,` int), true, "(1,2,3)[a,(int,2,int)]"),
    testing(1 `,` 2 `,` 3, a(2 `,` int), false, "(1,2,3)[a,(2,int)]"),
    testing(1 `,` 2 `,` 3 `,` 4.q(-1), a(2 `,` int.q(2) `,` int.q(-1)), true, "(1,2,3,4{-1})[a,(2,int{2},int{-1})]"),
    testing(1 `,` 2 `,` 3 `,` 4.q(-1), a(2 `,` int), true, "(1,2,3,4{-1})[a,(2,int)]"),
    testing(1 `,` 2 `,` 3 `,` 4, a(1 `,` -2 `,` 3 `,` 4), false, "(1,2,3,4)[a,(1,-2,3,4)]"),
    // testing(1 `,` 2 `,` 3 `,` 4.q(-1), a(-2 `,` int), false, "(1,2,3,4{-1})[a,(-2,int)]"),
    // testing(1 `,` 2 `,` 3, as(int `,` int.q(2)), 1 `,` int(2,3), "(1,2,3)[as,(int,int{2})]"),
    comment(",-lst vs. ;-lst"),
    testing(1 `,` 2, a(int `;` int), true, "(1,2)[a,(int;int)]"),
  ), testSet("|-lst basics", MM,
    testing(lst(int(1)), a(int `|` str), true, "(1)[a,(int|str)]"),
    testing(int(1).q(2) `|` 2, a(str.q(2) `|` int.q(2)), true, "(1{2}|2)[a,(str{2}|int{2})]"),
    testing(int(1) `|` 2, a(str.q(0, 5) `|` int.q(0, 100)), true, "(1|2)[a,(str{0,5}|int{0,100})]"),
    testing(int(1) `|` 2, a(str.q(0, 2) `|` int.q(2)), false, "(1|2)[a,(str{0,2}|int{2})]"),
    testing(int(1).q(2) `,`, a(int | int.q(0, 100)), true, "(1{2})[a,(int|int{0,100})]"),
    // TODO: testing(int(1).q(2) `,` int(2).q(2), a(str.q(0, 2) `|` int.q(0, 2)), true, "(1{2},2{2})[a,(str{+}|int{+})]"),
    testing(1 `,` 2 `,` 3, a(int `,` int), false, "(1,2,3)[a,(int,int)]"),
  ), testSet(";-lst int array type", INT_ARRAY_MODEL,
    comment("int array mmlang/mmscala"),
    // testing(intArrayObj, __, intArrayObj, intArrayStr),
    comment("int array passing"),
    testing(lst(), a('tarr_i), true, "( )[a,tarr_i]"),
    testing(1 `;`, a('tarr_i), true, "(1)[a,tarr_i]"),
    testing(1 `;` 2, a('tarr_i), true, "(1;2)[a,tarr_i]"),
    testing(1 `;` 2 `;` 3, a('tarr_i), true, s"(1;2;3) => [a,tarr_i]"),
    testing(1 `;` 2 `;` 3 `;` 4, a('tarr_i), true, s"(1;2;3;4) => [a,tarr_i]"),
    comment("int array failing"),
    testing("a", a('tarr_i), false, "'a'[a,tarr_i]"),
    testing("a" `;`, a('tarr_i), false, "('a')[a,tarr_i]"),
    testing(1 `;` "a", a('tarr_i), false, "(1;'a')[a,tarr_i]"),
    testing(1 `;` 2 `;` "a", a('tarr_i), false, "(1;2;'a') => [a,tarr_i]"),
    testing(1 `;` 2 `;` "a" `;` 4, a('tarr_i), false, "(1;2;'a';4) => [a,tarr_i]"),
  ), testSet(";-lst int/str array type", INT_STR_ARRAY_MODEL,
    comment("int/str array mmlang/mmscala"),
    // testing(intStrArrayObj, __, intStrArrayObj, intStrArrayStr),
    comment("int/str array passing"),
    testing(lst(), a('tarr_is), true, "( )[a,tarr_is]"),
    testing(1 `;` "a", a('tarr_is), true, "(1;'a')[a,tarr_is]"),
    testing(1 `;` "a", a('tarr_is), true, "(1;'a')[a,tarr_is]"),
    testing(1 `;` "a" `;` 2 `;` "b", a('tarr_is), true, "(1;'a';2;'b')[a,tarr_is]"),
    testing(1 `;` "a" `;` 2 `;` "b" `;` 3 `;` "c", a('tarr_is), true, "(1;'a';2;'b';3;'c')[a,tarr_is]"),
    comment("int/str array failing"),
    testing("a", a('tarr_is), false, "'a'[a,tarr_is]"),
    testing("a" `;` "b", a('tarr_is), false, "('a';'b')[a,tarr_is]"),
    testing(1 `;` 2, a('tarr_is), false, "(1;2)[a,tarr_is]"),
    testing(1 `;` "a" `;` 2 `;` 2, a('tarr_is), false, "(1;'a';2;2)[a,tarr_is]"),
    testing(1 `;` "a" `;` 2 `;` "b" `;` "c" `;` "c", a('tarr_is), false, "(1;'a';2;'b';'c';'c')[a,tarr_is]"),
  )) {

  ///////// MOVE BELOW INTO TABLE TEST RIG
  test("parallel expressions") {
    val starts:TableFor3[Obj, Lst[Obj], Obj] =
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
        (int(1, 2), (int | int) | __, int(1, 2)),
        (int(1, 2), (str | str) | str, zeroObj),
        ((1 `;` 2), ((int `;` int) | str), (1 `;` 2)),
        (1, int | str, 1),
        // (1, str | int, 1),
      )
    forEvery(starts) { (lhs, rhs, result) => TestUtil.evaluate(lhs, split(rhs).merge[Obj], result, compile = false) }
  }

  test("parallel [split] quantification") {
    assertResult(int)(int.mult(8).split(id | plus(8).mult(2) | int(56)).merge.id.rangeObj)
    assertResult(int.q(1, 20))(int.mult(8).split(id.q(10, 20) | plus(8).mult(2).q(2) | int(56)).merge.id.rangeObj)
    assertResult(int.q(2, 40))(int.q(2).mult(8).q(1).split(id.q(10, 20) | plus(8).mult(2).q(2) | int(56)).merge.id.rangeObj)
    assertResult(zeroObj)(int.q(2).mult(8).q(0).split(id.q(10, 20) | plus(8).mult(2).q(2) | int(56)).merge.id.rangeObj)
  }
}
