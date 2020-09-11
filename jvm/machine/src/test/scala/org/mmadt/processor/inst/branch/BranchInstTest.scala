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

import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.Obj.{intToInt, tupleToRecYES}
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.branch.BranchOp
import org.mmadt.language.obj.op.map.PlusOp
import org.mmadt.language.obj.op.trace.ModelOp.MM
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{IGNORING, comment, testSet, testing}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.StorageFactory.int.⨁

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class BranchInstTest extends BaseInstTest(

  testSet("[branch] w/ mm", MM.defining((__.via(__, BranchOp(from('x))) `;`) <= '^(split(to("x")).merge `,`)), //  ([branch,x])<=([split,<x>][merge])
    testing(str, -<(str `;` str) >-, str, "str-<(str;str)>-"),
    testing(str, id.-<(plus("a") `,` plus("b")).id.id.>-, str.branch(str.plus("a") `,` str.plus("b")), "str => [id]-<(+'a',+'b')[id][id]>-"),
    testing("a", -<(plus("a") `,` plus("b")) >-, str(str("aa"), str("ab")), "'a' => str-<(+'a',+'b')>-"),
    testing("a", id.-<(plus("a") `,` plus("b")).id.id.>-, str(str("aa"), str("ab")), "'a' => [id]-<(+'a',+'b')[id][id]>-"),
  ),
  testSet("[branch] ,-lst",
    testing(str, branch(str `,` str `,` str), str.id.q(3), "str[str,str,str]"),
    testing(int, branch(is(gt(10)) `,` is(gt(5)) `,` is(gt(0))), int.q(0, 3) <= int.branch(int.is(gt(10)) `,` int.is(gt(5)) `,` int.is(gt(0))),
      "int[[is>10],[is>5],[is>0]]"),
    IGNORING("eval-5")(7, branch(is(gt(10)) `,` is(gt(5)) `,` is(gt(0))), 7.q(2),
      "7 => int[[is>10],[is>5],[is>0]]"),
    testing(int.q(10), plus(0).branch(plus(1) `,` plus(2)).is(gt(10)), int.q(0, 20) <= int.q(10).plus(0).branch(plus(1) `,` plus(2)).is(gt(10)),
      "int{10}[plus,0][+1,+2][is>10]"),
    testing(int.q(10), branch(plus(1) `,` plus(2)).is(gt(10)), int.q(0, 20) <= int.q(10).branch(plus(1) `,` plus(2)).is(gt(10)),
      "int{10}[+1,+2][is>10]"),
    testing(7.q(10), branch(plus(1).q(6) `,` plus(2)).is(gt(5)), int(8.q(60), 9.q(10)),
      "7{10}[+{6}1,+2][is>5]"),
    testing(1, int.branch(plus(1) `,` plus(2)), int(2, 3),
      "1 => int+0[+1,+2]"),
    testing(1, int.plus(0).branch(plus(1) `,` plus(2) `,` int.plus(3)), int(2, 3, 4),
      "1 => int+0[+1,+2,int+3]"),
    testing(1, int.plus(0).branch(plus(1).q(2) `,` plus(2).q(3) `,` int.plus(3).q(4)), int(2.q(2), 3.q(3), 4.q(4)),
      "1 => int+0[+{2}1,+{3}2,+{4}3]"),
    testing(1, int.plus(0).branch(plus(1).plus(1) `,` plus(2)), 3.q(2),
      "1 => int+0[+1+1,+2]"),
    testing(int(1, 2), int.q(2).plus(0).branch(plus(1).plus(1) `,` plus(2)), int(3.q(2), 4.q(2)),
      "[1,2] => int{2}[plus,0][+1+1,+2]"),
    testing(int(1, 2), plus(0).branch(plus(1).plus(1) `,` plus(2)), int(3.q(2), 4.q(2)),
      "[1,2][plus,0][+1+1,+2]"),
    testing(1, int.plus(0).branch(plus(1) `,` plus(2)).path, strm(
      (1 `;` plus(0).inst `;` 1 `;` plus(1).inst `;` 2),
      (1 `;` plus(0).inst `;` 1 `;` plus(2).inst `;` 3)),
      "1 => int+0[+1,+2][path]"),
  ),
  testSet("[branch] ;-lst",
    testing(int.q(10), plus(0).branch(plus(1) `;` plus(2)).is(gt(10)), int.q(0, 10) <= int.q(10).plus(0).branch(plus(1) `;` plus(2)).is(gt(10)),
      "int{10}[plus,0][+1;+2][is>10]"),
    testing(1, int.plus(0).branch(plus(1) `;` plus(2)), 4,
      "1 => int+0[+1;+2]"),
    testing(1, int.plus(0).branch(plus(1) `;` plus(2) `;` int.plus(3)), 7,
      "1 => int[plus,0][[plus,1];[plus,2];[plus,3]]"),
    testing(1, int.plus(0).branch(plus(1).q(2) `;` plus(2).q(3) `;` int.plus(3).q(4)), 7.q(24),
      "1 => int[plus,0][[plus,1]{2};[plus,2]{3};[plus,3]{4}]"),
    testing(1, int.plus(0).branch(plus(1).plus(1) `;` plus(2)), 5,
      "1 => int[plus,0][[plus,1][plus,1];[plus,2]]"),
    testing(int(1, 2), plus(0).branch(plus(1).plus(1) `;` plus(2)), int(5, 6),
      "[1,2]+0[branch,(+1+1;+2)]"),
    testing(int(1, 2), plus(0).branch(plus(1) `;` plus(2)).path, strm(
      (1 `;` plus(0).inst `;` 1 `;` plus(1).inst `;` 2 `;` plus(2).inst `;` 4),
      (2 `;` plus(0).inst `;` 2 `;` plus(1).inst `;` 3 `;` plus(2).inst `;` 5)),
      "[1,2]+0[+1;+2][path]")
  ),
  testSet("[branch] |-lst",
    testing("marko", branch(str.q(?) `|` int), "marko", "'marko'  => [str{?}|int]"),
    testing("marko", branch(real.q(?) `|` int), zeroObj, "'marko' => [real{?}|int]"),
    testing(str, branch(__ `|` __ `|` __), str, "str[_|_|_]"),
    testing(str, branch(str `|` str `|` str), str, "str[str|str|str]"),
    testing(str, branch(str.q(?) `|` int.q(?) `|` real), str, "str => [str{?}|int{?}|real]"),
    testing(str, branch(str.id.q(2) `|` str.id.q(5) `|` str.id.q(3, 7)), str.id.q(2), "str[str[id]{2}|str[id]{5}|str[id]{3,7}]"),
    testing(int.q(10), plus(0).branch(plus(1) | plus(2)).is(gt(10)), int.q(0, 10) <= int.q(10).plus(0).branch(plus(1) | plus(2)).is(gt(10)),
      "int{10}[plus,0][+1|+2][is>10]"),
    testing(int.q(10), int.q(10).plus(0).branch(plus(1) | plus(2)).is(gt(10)), int.q(0, 10) <= int.q(10).plus(0).branch(plus(1) | plus(2)).is(gt(10)),
      "int{10} => int{10}[plus,0][+1|+2][is>10]"),
    testing(1, int.plus(0).branch(plus(1) | plus(2)), 2,
      "1 => int+0[+1|+2]"),
    testing(1, int.plus(0).branch(plus(1).q(0) | plus(2) | int.plus(3)), 3,
      "1 => int+0[+{0}1|+2|+3]"),
    testing(1, int.plus(0).branch(plus(1).q(0) | plus(2).q(0) | int.plus(3)), 4,
      "1 => int+0[+{0}1|+{0}2|+3]"),
    //testing(1, int.plus(0).branch(plus(1.q(0)) | plus(2.q(0)) | int.plus(3)), 4,
    //  "1 => int+0[+1{0}|+2{0}|+3]"), // TODO: argument quantifier not considered -- should this throw an exception?
    testing(1, plus(0).branch(plus(1).plus(1) | plus(3)), 3,
      "1+0[+1+1|+3]"),
    testing(1, ⨁(0).branch(⨁(1).q(-1).⨁(1).q(0) | ⨁(3)), 4,
      "1+0[+{-1}1+{0}1|+3]"),
    testing(1, int.plus(0).branch(plus(1).q(0).plus(1) | plus(3)), 4,
      "1 => int+0[+{0}1+1 | +3]"),
    testing(1, int.plus(0).branch(plus(1).plus(1).q(0) | plus(3)), 4,
      "1 => int[plus,0][[plus,1][plus,1]{0} | [plus,3]]"),
    testing(int, int.plus(0).branch(plus(1).plus(1).q(0) | plus(3).q(0)), int.plus(0).branch(plus(1).plus(1).q(0) | plus(3).q(0)),
      "int => int[plus,0][[plus,1][plus,1]{0}|[plus,3]{0}]"),
    testing(1, int.plus(0).branch(plus(1).plus(1).q(0) | plus(3).q(0)), zeroObj,
      "1 => int[plus,0][[plus,1][plus,1]{0}|[plus,3]{0}]"),
    testing(1, plus(0).branch(plus(1).plus(1).q(0) | plus(3).q(0)), zeroObj,
      "1[plus,0][[plus,1][plus,1]{0}|[plus,3]{0}]"),
    testing(int(1, 2), plus(0).branch(plus(1).plus(1) | plus(2)), int(3, 4),
      "[1,2][plus,0][+1+1 | +2]"),
    testing(int(1, 2), int.q(2).plus(0).branch(plus(1) | plus(2)).path, strm(
      (1 `;` plus(0).inst `;` 1 `;` plus(1).inst `;` 2),
      (2 `;` plus(0).inst `;` 2 `;` plus(1).inst `;` 3)))),
  testSet("[branch] ,-rec",
    testing(int(0), plus(1).branch((is(gt(1)) -> plus(10)) `_,`(is(gt(2)) -> plus(20)) `_,`(__ -> int.plus(30))), int(31)),
    testing(int(1, 2, 3), plus(0).branch((is(gt(1)) -> plus(10)) `_,`(is(gt(2)) -> plus(20)) `_,`(__ -> int.plus(30))), int(31, 12, 13, 32, 23, 33)),
    testing(int(5), plus(0).branch(__ -> plus(1) `_,` __ -> plus(2) `_,` is(gt(10)) -> plus(6)), int(6, 7)),
    testing(int(11), plus(0).branch(__ -> plus(1) `_,` __ -> plus(2) `_,` is(gt(10)) -> plus(6)), int(12, 13, 17))),
  testSet("[branch] |-rec",
    testing(int.q(10), plus(0).branch(int + 0 -> plus(1) `_|` int -> plus(2)).is(gt(10)), int.q(0, 10) <= int.q(10).plus(0).branch(int + 0 -> plus(1) `_|` int -> plus(2)).is(gt(10))),
    testing(int(1), int.plus(0).branch((int + 0 -> int.plus(1)) `_|`(int -> int.plus(2))), int(2)),
    testing(int(1), int.plus(0).branch(int.q(0) -> plus(1) `_|` int.q(0) -> plus(2) `_|` int + 0 -> int.plus(3)), int(4)),
    testing(int(1), int.plus(0).branch(int + 0 -> plus(1).plus(1) `_|` int -> plus(3)), int(3)),
    testing(int(1), int.plus(0).branch(int.q(0) -> plus(1).plus(1) `_|` int -> plus(3).q(0)), zeroObj),
    testing(int(1), int.plus(0).branch(int.q(0) -> plus(1).plus(1) `_|` int.plus(1).q(0) -> plus(3)), zeroObj),
    testing(int(1, 2), int.q(2).plus(0).branch(int + 0 -> plus(1).plus(1) `_|` int -> plus(2)), int(3, 4)),
    testing(int(1, 2), int.q(2).plus(0).branch(int + 0 -> plus(1) `_|` int -> plus(2)).path, strm(
      lst(g = (";", List[Obj](int(1), PlusOp(0), 1, PlusOp(1), 2))),
      lst(g = (";", List[Obj](int(2), PlusOp(0), 2, PlusOp(1), 3)))))),
  testSet("[branch] lst stream ring theory", MM,
    comment("abelian group axioms"),
    testing(str, branch(branch(branch(branch(id `,`) `,`) `,`) `,`), str, "str[[[[[id]]]]]"),
    testing(str, branch(branch(id `,` id) `,` id), str.q(3) <= str.id.q(3), "str[[[id],[id]],[id]]"),
    testing(str, branch(id.q(2) `,` id.q(3)), str.q(5) <= str.id.q(5), "str[[id]{2},[id]{3}]"),
    testing(str, branch(id `,` q(0)), str, "str[[id],{0}]"),
    testing(str, branch(id `,` id.q(-1)), zeroObj, "str[[id],[id]{-1}]"),
    comment("monoid axioms"),
    testing(str, branch(id `;` id `;` id), str, "str[[id];[id];[id]]"),
    testing(str, branch(branch(id `;` id) `;` id), str, "str[[[id];[id]];[id]]"),
    testing(str, branch(id `;` branch(id `;` id)), str, "str[[id];[[id];[id]]]"),
    comment("ring axioms"),
    testing(str, branch(branch(id `,` id) `;` id), str.q(2) <= str.id.q(2), "str[[[id],[id]];[id]]"),
    testing(str, branch(branch(id `;` id) `,` branch(id `;` id)), str.q(2) <= str.id.q(2), "str[str[[id];[id]],[[id];[id]]]"),
    comment("ring theorems"),
    testing(str, branch(id.q(-1) `,` id.q(-1)), str.q(-2) <= str.id.q(-2), "str[[id]{-1},[id]{-1}]"),
    testing(str, branch(id `,` id).q(-1), str.q(-2) <= str.id.q(-2), "str[[id],[id]]{-1}"),
    testing(str, branch(id.q(-1) `,`).q(-1), str, "str[[id]{-1}]{-1}"),
    testing(str, branch(id `;` __.q(0)), zeroObj, "str[[id];{0}]"),
    testing(str, branch(__.q(0) `;` id), zeroObj, "str[{0};[id]]"),
    testing(str, branch(id `;` id.q(-1)), str.q(-1) <= str.id.q(-1), "str[[id];[id]{-1}]"),
    testing(str, branch(id.q(-1) `;` id), str.q(-1) <= str.id.q(-1), "str[[id]{-1};[id]]"),
    testing(str, branch(id.q(-1) `;` id.q(-1)), str, "str[[id]{-1};[id]{-1}]"),
    testing(str, branch(id `;` id), str, "str[[id];[id]]"),
    testing(str, branch(str.id `;` str.id).q(-1), str.q(-1) <= str.id.q(-1), "str[[id];[id]]{-1}"),
    comment("stream ring axioms"),
    testing(str, branch(id.q(2) `,` id.q(3)), str.id.q(5), "str[[id]{2},[id]{3}]"), // bulking
    testing(str.q(2), str.q(2).branch(id.q(3) `,`), str.q(6) <= str.q(2).id.q(3), "str{6}<=str{2}[[id]{3}]"), // applying
    testing(str.q(2), str.q(2).branch(id.q(3) `,` id.q(4)), str.q(14) <= str.q(2).id.q(7), "str{14}<=str{2}[[id]{3},[id]{4}]"), // splitting
    testing(str, branch(id.q(6) `,` id.q(8)), str.q(14) <= str.id.q(14), "str[[id]{6},[id]{8}]"), // splitting
    testing(str, branch(branch(id.q(2) `,`) `,` branch(id.q(3) `,`)), str.q(5) <= str.id.q(5), "str[[[id]{2}],[[id]{3}]]"), // merging
    testing(str, branch(id.q(2) `,` id.q(3)), str.q(5) <= str.id.q(5), "str[[id]{2},[id]{3}]"), // merging
    testing(str, branch(__.q(0) `,` id), str, "str[{0},[id]]"), // removing
    testing(str, branch(id.q(0) `,` id), str, "str[[id]{0},[id]]"), // removing
  ),
  testSet("[branch] rec stream ring theory", MM,
    comment("abelian group axioms"),
    testing(str, branch(branch(branch(branch(id -> id) `,`) `,`) `,`), str, "str[[[[[id]->[id]]]]]"),
    testing(str, branch(branch(id -> id `_,` id -> id) `,` branch(id -> id)), str.q(3) <= str.id.q(3), "str[[[id]->[id],[id]->[id]],[[id]->[id]]]"),
    testing(str, branch(str -> str.id.q(2) `_,` str -> str.id.q(3)), str.q(5) <= str.id.q(5), "str[str->[id]{2},str->[id]{3}]"),
    testing(str, branch(id -> id `_,` __.q(0) -> __.q(0)), str, "str[[id]->[id],{0}->{0}]"),
    testing(str, branch(id -> id `_,` id.q(-1) -> id.q(-1)), zeroObj, "str[str[id]->str[id],str[id]{-1}->str[id]{-1}]"),
    testing(str, branch(id -> id `_,` id.q(-1) -> id.q(-1)), zeroObj, "str[[id]->[id],[id]{-1}->[id]{-1}]"),
    comment("monoid axioms"),
    testing(str, branch(branch(id -> id `_;` id -> id) `;` id -> id), str, "str[[id]->[id];[id]->[id];[id]->[id]]"),
    testing(str, branch(id -> id `_;` __ -> branch(id -> id `_;` id -> id)), str, "str[[id]->[id];[id]->[[id]->[id];[id]->[id]]]"),
    comment("ring axioms"),
    testing(str, branch(branch(id -> id `_,` id -> id) `;` branch(str.id -> str.id)), str.q(2) <= str.id.q(2), "str[[[id]->[id],[id]->[id]];[[id]->[id]]]"),
    testing(str, branch(id -> branch(id -> id `_;` id -> id) `_,` id -> branch(id -> id `_;` id -> id)), str.q(2) <= str.id.q(2), "str[[id]->[[id]->[id];[id]->[id]],[id]->[[id]->[id];[id]->[id]]]"),
    comment("ring theorems"),
    testing(str, branch(id.q(1) -> id.q(-1) `_,` id.q(1) -> id.q(-1)), str.q(-2) <= str.id.q(-2), "str[[id]{-1}->[id]{-1},[id]{-1}->[id]{-1}]"),
    testing(str, branch(id -> id `_,` id -> id).q(-1), str.q(-2) <= str.id.q(-2), "str[[id]->[id],[id]->[id]]{-1}"),
    testing(str, branch(__ -> id.q(-1)).q(-1), str, "str[_->[id]{-1}]{-1}"),
    testing(str, branch(id -> id `_;` id.q(0) -> id.q(0)), zeroObj, "str[[id]->[id];{0}->{0}]"),
    testing(str, branch((str.q(0) -> str.q(0)) `_;`(str.id -> str.id)), zeroObj, "str[{0}->{0};[id]->[id]]"),
    testing(str, branch(str.id -> str.id `_;` str.id.q(-1) -> str.id.q(-1)), str.q(-1) <= str.id.q(-1), "str[[id]->[id];[id]{-1}->[id]{-1}]"),
    testing(str, branch(str.id.q(-1) -> str.id.q(-1) `_;` str.id -> str.id), str.q(-1) <= str.id.q(-1), "str[[id]{-1}->[id]{-1};[id]->[id]]"),
    testing(str, branch(str.id.q(-1) -> str.id.q(-1) `_;` str.id.q(-1) -> str.id.q(-1)), str, "str[[id]{-1}->[id]{-1};[id]{-1}->[id]{-1}]"),
    testing(str, branch(id -> id `_;` id -> id), str, "str[[id]->[id];[id]->[id]]"),
    testing(str, branch(str.id -> str.id `_;` str.id -> str.id).q(-1), str.q(-1) <= str.id.q(-1), "str[[id]->[id];[id]->[id]]{-1}"),
    comment("stream ring axioms"),
    testing(str, branch(__ -> str.id.q(2) `_,` __ -> str.id.q(3)), str.id.q(5), "str[_->[id]{2},_->[id]{3}]"), // bulking
    testing(str.q(2), str.q(2).branch(id.q(3) -> id.q(3)), str.q(6) <= str.q(2).id.q(3), "str{6}<=str{2}[[id]{3}->[id]{3}]"), // applying
    testing(str.q(2), str.q(2).branch(__ -> id.q(3) `_,` __ -> id.q(4)), str.q(14) <= str.q(2).id.q(7), "str{14}<=str{2}[_->[id]{3},_->[id]{4}]"), // splitting
    testing(str, branch(__ -> id.q(6) `_,` __ -> id.q(8)), str.q(14) <= str.id.q(14), "str[_->[id]{6},_->[id]{8}]"), // splitting
    testing(str, branch(str -> branch(id.q(2) -> id.q(2)) `_,` str -> branch(id.q(3) -> id.q(3))), str.q(5) <= str.id.q(5), "str[[id]->[_->[id]{2}],[id]->[_->[id]{3}]]"), // merging
    testing(str, branch(id -> id.q(2) `_,` id -> id.q(3)), str.q(5) <= str.id.q(5), "str[[id]->[id]{2},[id]->[id]{3}]"), // merging
    testing(str, branch(__.q(0) -> __.q(0) `_,` id -> id), str, "str[{0}->{0},[id]->[id]]"), // removing
    testing(str, branch(id.q(0) -> id.q(0) `_,` id -> id), str, "str[[id]{0}->[id]{0},[id]->[id]]"), // removing
  ),
) {

  test("[branch] path testing") {
    assertResult("(5;[plus,0];5;[plus,1];6;[plus,3];9)")(int(5).plus(0).branch(int.plus(1) `,` int.plus(2)).plus(3).path.toStrm.drain.head.toString)
    assertResult("(5;[plus,0];5;[plus,2];7;[plus,3];10)")(int(5).plus(0).branch(int.plus(1) `,` int.plus(2)).plus(3).path.toStrm.drain(1).toString)
    //
    assertResult("(5;[plus,1];6;[plus,11];17;[plus,3];20)")(int(5).branch(int.plus(1).plus(11) `,` int.plus(2)).plus(3).path.toStrm.drain.head.toString)
    assertResult("(5;[plus,2];7;[plus,3];10)")(int(5).branch(int.plus(1).plus(11) `,` int.plus(2)).plus(3).path.toStrm.drain(1).toString)
    //
    assertResult("(5;[plus,0];5;[plus,1];6;[plus,11];17;[plus,3];20)")(int(5).plus(0).branch(int.plus(1).plus(11) `,` int.plus(2)).plus(3).path.toStrm.drain.head.toString)
    assertResult("(5;[plus,0];5;[plus,2];7;[plus,3];10)")(int(5).plus(0).branch(int.plus(1).plus(11) `,` int.plus(2)).plus(3).path.toStrm.drain(1).toString)
  }

  /*(int(1, 2), int.q(2).plus(0).branch[Int](plus(1) `,` plus(2)).path(), strm(List(
  lst(g = (";", List[Obj](int(1), PlusOp(0), 1, PlusOp(1), 2))),
  lst(g = (";", List[Obj](int(1), PlusOp(0), 1, PlusOp(2), int(3).q(2)))), // TODO: <-- q(2)
  // lst(g = (";", List[Obj](int(2), PlusOp(0), 2, PlusOp(1), 3))), // TODO: WHEN USING PATH, UNIQUNESS BASED ON OBJ GRAPH PATH!
  lst(g = (";", List[Obj](int(2), PlusOp(0), 2, PlusOp(2), 4)))))),*/
  //(int(1), int.plus(0).branch[Int](plus(1) `;` plus(2)).path(), lst(g = (";", List[Obj](int(1), PlusOp(0), 1, PlusOp(1), 2, PlusOp(2),4)))), // TODO: TYPES IN A STRM
  // (int(1), int.plus(0).branch(plus(1).q(2) | plus(2).q(3) | int.plus(3).q(4)), int(2).q(2)), // TODO: VALUE QUANTIFIERS ARE NOT RANGED
  //(int(1), int.plus(0).branch[Int](plus(1) | plus(2)).path(), lst(g = (";", List[Obj](int(1), PlusOp(0), 1, PlusOp(1), 2)))), // TODO: TYPES IN A STRM
  //(int(1), int.plus(0).branch((int.q(0) -> int.plus(1)) `|` (int + 0 -> int.plus(2)) `|` (int -> int.plus(3))), int(3)),
  // (int(1), int.plus(0).branch(plus(1).q(2) | plus(2).q(3) | int.plus(3).q(4)), int(2).q(2)), // TODO: VALUE QUANTIFIERS ARE NOT RANGED
  // (int(1), int.plus(0).branch(int.q(0) -> plus(1).plus(1) `_|` int -> plus(3)), int(4)),
  // (int(1), int.plus(0).branch(int.q(0) -> plus(1).plus(1) `_|` int -> plus(3)), int(4)),
  //(int(1), int.plus(0).branch[Int](plus(1) | plus(2)).path(), lst(g = (";", List[Obj](int(1), PlusOp(0), 1, PlusOp(1), 2)))), // TODO: TYPES IN A STRM

}
