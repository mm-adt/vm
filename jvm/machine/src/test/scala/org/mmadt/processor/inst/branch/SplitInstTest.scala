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

package org.mmadt.processor.inst.branch

import org.mmadt.language.obj.Int
import org.mmadt.language.obj.Obj.{symbolToToken, intToInt, tupleToRecYES}
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.PathOp.VERTICES
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil._
import org.mmadt.storage.StorageFactory.{real, _}

class SplitInstTest extends BaseInstTest(
  testSet("[split] table test",
    testing(lst, is(lst.eqs(lst.zero)), lst, "lst => [is,lst[eq,lst[zero]]]"),
    testing(lst, eqs(lst.zero), btrue, "lst => [eq,lst[zero]]"),
    testing(1, map(lst).eqs(lst.zero), btrue, "lst => [map,lst][eq,lst[zero]]"), // why 1 not work?
    testing(lst, map(lst).eqs(lst.zero), btrue, "lst => [map,lst][eq,lst[zero]]"),
    testing(1, -<(int `,` int), int(1) `,` int(1), "1-<(int,int)"),
    testing(1, -<(int `,` int.plus(2)), int(1) `,` int(3), "1-<(int,int+2)"),
    testing(1, -<(int `,` int.plus(2).q(10)), int(1) `,` int(3).q(10), "1-<(int,int+{10}2)"),
    testing(1.q(5), -<(int `,` int.plus(2).q(10)), (1 `,` 3.q(10)).q(5), "1{5}-<(int,int+{10}2)"),
    testing(1.q(5), -<(int `,` int.plus(2).q(10)) >-, int(1.q(5), 3.q(50))),
    testing(int(1, 100), -<(int | int) >-, int(int(1), int(100)), "[1,100]-<(int|int)>-"),
    testing(int(1, 100), -<(int.q(?) | int) >-, int(int(1), int(100)), "[1,100]-<(int{?}|int)>-"),
    testing(int(1, 100), -<(int `,` int) >-, int(1, 1, 100, 100), "[1,100]-<(int,int)>-"),
    testing(int(1, 100), -<(int `,` int) >-, int(int(1).q(2), int(100).q(2)), "[1,100]-<(int,int)>-"),
    testing(int(1.q(5), 100), -<(int `,` int.plus(2).q(10)) >-, int(1.q(5), 3.q(50), 100, 102.q(10))),
    testing(int(1.q(5), 100), -<(int | int.plus(2).q(10)) >-, int(int(1).q(5), 100)),
    testing(int(1, 2), -<(int.q(?) | (int -< (int.q(?) | int))), __(int(1) `|`, int(2) `|`), "[1,2] => -<(int{?}|int-<(int{?}|int))"),
    testing(int(1, 2), -<(int | (int -< (int | int))), __(int(1) `|`, int(2) `|`), "[1,2]-<(int|int-<(int|int))"),
    testing(int(1, 2), -<(int `,` (int -< (int | int))), __(int(1) `,` (int(1) |), 2 `,` (int(2) |))),
    testing(1, -<(str.q(?) | int), zeroObj | 1, "1-<(str{?}|int)"),
    testing(1, int.-<(int `;` int), 1 `;` 1, "1=>int-<(int;int)"),
    testing(int(1, 2, 3), int.q(3).-<(int.q(3) `;` int.q(3)), strm(List(1 `;` 1, 2 `;` 2, 3 `;` 3)), "(1,2,3) => lst>--<(int{3};int{3})"),
    testing(2, -<(int.q(?) | str), int(2) | __.q(qZero), "2-<(int{?}|str)"),
    testing(4.q(2), int.q(2).-<(int | int.is(gt(10))), (4 | zeroObj).q(2), "4{2} => int{2}-<(int|int[is>10])"),
    testing(2.q(2), int.q(2).-<(int `;` int.is(gt(10))), (2 `;` zeroObj).q(2), "2{2} => int{2}-<(int;int[is>10])"),
    testing(2, int.-<(int `;` int.is(gt(10))), 2 `;` zeroObj, "2 => int-<(int;int[is>10])"),
    testing(2, int.-<((int | int.is(gt(11))) | int.is(gt(10))), (2 | zeroObj | zeroObj), "2 => int-<(int|int[is>11])"),
    comment("[split] |-rec table test"),
    testing(0, plus(1).-<(
      int.is(int.gt(2)) -> int.mult(3) |
        int -> int.mult(4)) >-, 4, "0[plus,1]-<(int[is>2] -> int[mult,3] | int -> int[mult,4])>-"),
    IGNORING("eval-6")(0, int.plus(39).-<(
      int.is(int.gt(40)) -> int.plus(1) |
        int.is(int.gt(30)) -> int.plus(2) |
        int.is(int.gt(20)) -> int.plus(3) |
        int.is(int.gt(10)) -> int.plus(4)).>-.plus(1), 42, "0 => int[plus,39]-<(int[is>40] -> [plus,1] | int[is>30] -> [plus,2] | int[is>20] -> [plus,3] | int[is>10] -> [plus,4])>-[plus,1]"),
    IGNORING("eval-6")(0, int.plus(29).-<(
      int.is(int.gt(40)) -> int.plus(1) |
        int.is(int.gt(30)) -> int.plus(2) |
        int.is(int.gt(20)) -> int.plus(3) |
        int.is(int.gt(10)) -> int.plus(4)).>-.plus(1), 33, "0 => int[plus,29]-<(int[is>40] -> [plus,1] | int[is>30] -> [plus,2] | int[is>20] -> [plus,3] | int[is>10] -> [plus,4])>-[plus,1]"),
    IGNORING("eval-6")(0, int.plus(29).-<(
      int.is(gt(40)) -> int.plus(1) |
        int.is(gt(30)) -> int.plus(2) |
        int.is(gt(20)) -> int.plus(3) |
        int.is(gt(10)) -> int.plus(4)).>-.plus(1), 33, "0 => int[plus,29]-<(int[is>40] -> [plus,1] | int[is>30] -> [plus,2] | int[is>20] -> [plus,3] | int[is>10] -> [plus,4])>-[plus,1]"),
    IGNORING("eval-5", "query-2")(29, int.-<(
      int.is(int.gt(40)) -> plus(1) `_|`
        int.is(int.gt(30)) -> plus(2) `_|`
        int.is(int.gt(20)) -> plus(3) `_|`
        int.is(int.gt(10)) -> plus(4)).>-, 32, "29-<(int[is>40] -> [plus,1] | int[is>30] -> [plus,2] | int[is>20] -> [plus,3] | int[is>10] -> [plus,4])>-"),
    testing(real(0.0, 1.0, 1.0),
      real.q(3).to('x).plus(1.0).to('y).-<(
        (is(eqs(1.0)) -> real.from('y)) |
          (is(eqs(2.0)) -> real.from('x))).>-.plus(real.from('y)), real(2.0, 3.0, 3.0)),
    comment("[split] ,-rec table test"),
    testing(0, plus(1).-<(int.is(int.gt(2)) -> int.mult(3) `_,` int -> int.mult(4)) >-, 4),
    testing(0, plus(4).-<(
      (int.is(int.gt(2)) -> int.mult(3)) `,`
        (int -> int.mult(4))) >-, int(12, 16)),
    testing(0, int.plus(39).-<(
      (int.is(int.gt(40)) -> int.plus(1)) `,`
        (int.is(int.gt(30)) -> int.plus(2)) `,`
        (int.is(int.gt(20)) -> int.plus(3)) `,`
        (int.is(int.gt(10)) -> int.plus(4))).>-.plus(1), int(42, 43, 44), "0 => int+39-<(int[is>40] -> [plus,1] , int[is>30] -> [plus,2] , int[is>20] -> [plus,3] , int[is>10] -> [plus,4])>-+1"),
    testing(0, int.plus(29).-<(
      (int.is(int.gt(40)) -> int.plus(1)) `,`
        (int.is(int.gt(30)) -> int.plus(2)) `,`
        (int.is(int.gt(20)) -> int.plus(3)) `,`
        (int.is(int.gt(10)) -> int.plus(4))).>-.plus(1), int(33, 34), "0 => int+29-<(int[is>40] -> [plus,1] , int[is>30] -> [plus,2] , int[is>20] -> [plus,3] , int[is>10] -> [plus,4])>-+1"),
    testing(0, int.plus(29).-<(
      (int.is(gt(40)) -> int.plus(1)) `,`
        (int.is(gt(30)) -> int.plus(2)) `,`
        (int.is(gt(20)) -> int.plus(3)) `,`
        (int.is(gt(10)) -> int.plus(4))).>-, int(32, 33), "0 => int+29-<(int[is>40] -> [plus,1] , int[is>30] -> [plus,2] , int[is>20] -> [plus,3] , int[is>10] -> [plus,4])>-"),
    testing(0, int.plus(-29).-<(
      (int.is(int.gt(40)) -> plus(1)) `,`
        (int.is(int.gt(30)) -> plus(2)) `,`
        (int.is(int.gt(20)) -> plus(3)) `,`
        (int.is(int.gt(10)) -> plus(4))).>-, zeroObj, "0 => int+-29-<(int[is>40] -> [plus,1] , int[is>30] -> [plus,2] , int[is>20] -> [plus,3] , int[is>10] -> [plus,4])>-"),
    testing(real(0.0, 1.0, 1.0), real.q(3).to('x).plus(1.0).to('y).-<(
      (is(eqs(1.0)) -> real.from('y)) `_,`
        (is(eqs(2.0)) -> real.from('x))
    ).>-.plus(real.from('y)), real(2.0, 3.0, 3.0), "[0.0,1.0,1.0]=>real{3}<x>[plus,1.0]<y>-<([is==1.0]-><.y>,[is==2.0]-><.x>)>-[plus,<.y>]"),
  )) {

  test("lineage preservation (products)") {
    assertResult(int(321))(int(1) ==> int.plus(100).plus(200).split(int `,` bool).merge[Int].plus(20))
    assertResult(int.plus(100).plus(200).split(int | bool).merge[Int].plus(20))(int ==> int.plus(100).plus(200).split(int | bool).merge[Int].plus(20))
    assertResult(int(1) `;` 101 `;` 301 `;` 301 `;` 321)((int(1) ==> int.plus(100).plus(200).split(int `,` bool).merge[Int].plus(20)).path(VERTICES))
  }
  test("lineage preservation (coproducts)") {
    assertResult(int(321, 323))(int(1) ==> int.plus(100).plus(200).split(int `,` int.plus(2)).merge[Int].plus(20))
    assertResult(int.plus(100).plus(200).split(int `,` int.plus(2)).merge[Int].plus(20))(int ==> int.plus(100).plus(200).split(int `,` int.plus(2)).merge[Int].plus(20))
    assertResult(strm(List(
      int(1) `;` 101 `;` 301 `;` 301 `;` 321,
      int(1) `;` 101 `;` 301 `;` 301 `;` 303 `;` 323)))(int(1) ==> int.plus(100).plus(200).split(int `,` int.plus(2)).merge[Int].plus(20).path(VERTICES))
  }
  test("quantifiers") {
    assertResult(int(1))((int(1) | 2).merge)
    assertResult(int(1).q(10))((int(1) | 2).q(10).merge)
    assertResult(int(1).q(10))((int(1) | int(2).q(5)).q(10).merge)
    assertResult(int(1).q(4, 10))((int(1) | int(2).q(5)).q(4, 10).merge)
  }
}
