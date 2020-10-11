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

package org.mmadt.processor.inst.map


import org.mmadt.language.obj.Obj.{intToInt, tupleToRecYES}
import org.mmadt.language.obj.`type`.__.tail
import org.mmadt.language.obj.op.trace.ModelOp._
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.storage.StorageFactory._

class TailInstTest extends BaseInstTest(
  testSet("[tail] table test", List(NONE, MM, MMX),
    comment(";-lst"),
    testing(1 `;`, tail, lst(), "(1)[tail]"),
    testing(1 `;`, lst.tail, lst(), "(1) => lst[tail]"),
    testing(1 `;` 2, lst.tail, 2 `;`, "(1;2) => lst[tail]"),
    testing(1 `;` 2 `;` 3, lst.tail, 2 `;` 3, "(1;2;3) => lst[tail]"),
    testing(1 `;` 2 `;` 3, tail, 2 `;` 3, "(1;2;3)[tail]"),
    testing((1 `;` 2 `;` 3).q(10), tail, (2 `;` 3).q(10), "(1;2;3){10}[tail]"),
    testing((1 `;` 2 `;` 3).q(10), tail.q(2), (2 `;` 3).q(20), "(1;2;3){10}[tail]{2}"),
    comment("|-lst"),
    testing(int(1) `|` 2 `|` 3, tail, int(2) `|` 3, "(1|2|3)[tail]"),
    testing(int(1) `|` 2 `|` 3.q(0), tail, lst(int(2)), "(1|2|3{0})[tail]"),
    comment(";-rec"),
    testing(str("a") -> int(1) `_;` str("b") -> int(2) `_;` str("c") -> int(3), tail, (str("b") -> int(2) `_;` str("c") -> int(3)), "('a'->1;'b'->2;'c'->3)[tail]"),
    testing((str("a") -> int(1) `_;` str("b") -> int(2) `_;` str("c") -> int(3)).q(5), tail.q(10), (str("b") -> int(2) `_;` str("c") -> int(3)).q(50), "('a'->1;'b'->2;'c'->3){5}[tail]{10}"),
  ))
