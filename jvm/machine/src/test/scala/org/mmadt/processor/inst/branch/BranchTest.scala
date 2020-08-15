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
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.map.PlusOp
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{test, testSet}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class BranchTest extends BaseInstTest(
  testSet("[branch] ,-lst",
    test(int.q(10), plus(0).branch(plus(1) `,` plus(2)).is(gt(10)), int.q(0, 20) <= int.q(10).plus(0).branch(plus(1) `,` plus(2)).is(gt(10))),
    test(int(1), int.plus(0).branch(plus(1) `,` plus(2)), int(2, 3)),
    test(int(1), int.plus(0).branch(plus(1) `,` plus(2) `,` int.plus(3)), int(2, 3, 4)),
    test(int(1), int.plus(0).branch(plus(1).q(2) `,` plus(2).q(3) `,` int.plus(3).q(4)), int(int(2).q(2), int(3).q(3), int(4).q(4))),
    test(int(1), int.plus(0).branch(plus(1).plus(1) `,` plus(2)), int(3).q(2)),
    test(int(1, 2), int.q(2).plus(0).branch(plus(1).plus(1) `,` plus(2)), int(int(3).q(2), int(4).q(2))),
    test(int(1), int.plus(0).branch(plus(1) `,` plus(2)).path(), strm(lst(g = (";", List[Obj](int(1), PlusOp(0), 1, PlusOp(1), 2))), lst(g = (";", List[Obj](int(1), PlusOp(0), 1, PlusOp(2), 3)))))),
  testSet("[branch] ;-lst",
    test(int.q(10), plus(0).branch(plus(1) `;` plus(2)).is(gt(10)), int.q(0, 10) <= int.q(10).plus(0).branch(plus(1) `;` plus(2)).is(gt(10))),
    test(int(1), int.plus(0).branch(plus(1) `;` plus(2)), int(4)),
    test(int(1), int.plus(0).branch(plus(1) `;` plus(2) `;` int.plus(3)), int(7)),
    test(int(1), int.plus(0).branch(plus(1).q(2) `;` plus(2).q(3) `;` int.plus(3).q(4)), int(7).q(24)),
    test(int(1), int.plus(0).branch(plus(1).plus(1) `;` plus(2)), int(5)),
    test(int(1, 2), int.q(2).plus(0).branch(plus(1).plus(1) `;` plus(2)), int(5, 6)),
    test(int(1, 2), int.q(2).plus(0).branch(plus(1) `;` plus(2)).path(), strm(
      lst(g = (";", List[Obj](int(1), PlusOp(0), 1, PlusOp(1), 2, PlusOp(2), 4))),
      lst(g = (";", List[Obj](int(2), PlusOp(0), 2, PlusOp(1), 3, PlusOp(2), 5)))))),
  testSet("[branch] |-lst",
    test(int.q(10), plus(0).branch(plus(1) | plus(2)).is(gt(10)), int.q(0, 10) <= int.q(10).plus(0).branch(plus(1) | plus(2)).is(gt(10))),
    test(int(1), int.plus(0).branch(plus(1) | plus(2)), int(2)),
    test(int(1), int.plus(0).branch(plus(1).q(0) | plus(2) | int.plus(3)), int(3)),
    test(int(1), int.plus(0).branch(plus(1).q(0) | plus(2).q(0) | int.plus(3)), int(4)),
    test(int(1), int.plus(0).branch(plus(1).plus(1) | plus(3)), int(3)),
    test(int(1), int.plus(0).branch(plus(1).q(0).plus(1) | plus(3)), int(4)),
    test(int(1), int.plus(0).branch(plus(1).plus(1).q(0) | plus(3)), int(4)),
    test(int(1), int.plus(0).branch(plus(1).plus(1).q(0) | plus(3).q(0)), zeroObj, compile = false),
    test(int(1, 2), int.q(2).plus(0).branch(plus(1).plus(1) | plus(2)), int(3, 4)),
    test(int(1, 2), int.q(2).plus(0).branch(plus(1) | plus(2)).path(), strm(
      lst(g = (";", List[Obj](int(1), PlusOp(0), 1, PlusOp(1), 2))),
      lst(g = (";", List[Obj](int(2), PlusOp(0), 2, PlusOp(1), 3)))))),
  testSet("[branch] ,-rec",
    test(int(0), plus(1).branch((is(gt(1)) -> plus(10)) `_,` (is(gt(2)) -> plus(20)) `_,` (__ -> int.plus(30))), int(31)),
    test(int(1, 2, 3), plus(0).branch((is(gt(1)) -> plus(10)) `_,` (is(gt(2)) -> plus(20)) `_,` (__ -> int.plus(30))), int(31, 12, 13, 32, 23, 33))),
  testSet("[branch] |-rec",
    test(int.q(10), plus(0).branch(int + 0 -> plus(1) `_|` int -> plus(2)).is(gt(10)), int.q(0, 10) <= int.q(10).plus(0).branch(int + 0 -> plus(1) `_|` int -> plus(2)).is(gt(10))),
    test(int(1), int.plus(0).branch((int + 0 -> int.plus(1)) `_|` (int -> int.plus(2))), int(2)),
    test(int(1), int.plus(0).branch(int.q(0) -> plus(1) `_|` int.q(0) -> plus(2) `_|` int + 0 -> int.plus(3)), int(4)),
    test(int(1), int.plus(0).branch(int + 0 -> plus(1).plus(1) `_|` int -> plus(3)), int(3)),
    test(int(1), int.plus(0).branch(int.q(0) -> plus(1).plus(1) `_|` int -> plus(3).q(0)), zeroObj, compile = false),
    test(int(1), int.plus(0).branch(int.q(0) -> plus(1).plus(1) `_|` int.plus(1).q(0) -> plus(3)), zeroObj, compile = false),
    test(int(1, 2), int.q(2).plus(0).branch(int + 0 -> plus(1).plus(1) `_|` int -> plus(2)), int(3, 4)),
    test(int(1, 2), int.q(2).plus(0).branch(int + 0 -> plus(1) `_|` int -> plus(2)).path(), strm(
      lst(g = (";", List[Obj](int(1), PlusOp(0), 1, PlusOp(1), 2))),
      lst(g = (";", List[Obj](int(2), PlusOp(0), 2, PlusOp(1), 3))))))) {

  test("[branch] path testing") {
    assertResult("(5;[plus,0];5;[plus,1];6;[plus,3];9)")(int(5).plus(0).branch(int.plus(1) `,` int.plus(2)).plus(3).path().toStrm.values(0).toString)
    assertResult("(5;[plus,0];5;[plus,2];7;[plus,3];10)")(int(5).plus(0).branch(int.plus(1) `,` int.plus(2)).plus(3).path().toStrm.values(1).toString)
    //
    assertResult("(5;[plus,1];6;[plus,11];17;[plus,3];20)")(int(5).branch(int.plus(1).plus(11) `,` int.plus(2)).plus(3).path().toStrm.values(0).toString)
    assertResult("(5;[plus,2];7;[plus,3];10)")(int(5).branch(int.plus(1).plus(11) `,` int.plus(2)).plus(3).path().toStrm.values(1).toString)
    //
    assertResult("(5;[plus,0];5;[plus,1];6;[plus,11];17;[plus,3];20)")(int(5).plus(0).branch(int.plus(1).plus(11) `,` int.plus(2)).plus(3).path().toStrm.values(0).toString)
    assertResult("(5;[plus,0];5;[plus,2];7;[plus,3];10)")(int(5).plus(0).branch(int.plus(1).plus(11) `,` int.plus(2)).plus(3).path().toStrm.values(1).toString)
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
