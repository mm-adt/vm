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

import org.mmadt.language.obj.Int
import org.mmadt.language.obj.Obj.{intToInt, tupleToRecYES}
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp.{MM, MMX, NONE}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{IGNORING, comment, testSet, testing}
import org.mmadt.storage.StorageFactory._

class HeadInstTest extends BaseInstTest(
  testSet("[head] table test", List(NONE, MM, MMX),
    comment(";-lst"),
    testing(1 `;` 2 `;` 3, lst.head, 1, "(1;2;3) => lst[head]"),
    testing(1 `;` 2 `;` 3, head, 1, "(1;2;3)[head]"),
    testing((((1 `;` 2) `;`) `;` 3), head, (1 `;` 2), "((1;2);3)[head]"),
    testing(1.q(5) `;` 2.q(4) `;` 3, lst.head, 1.q(5), "(1{5};2{4};3) => lst[head]"),
    IGNORING(MM)(1.q(5) `;` 2.q(4) `;` 3, lst[Int].head.q(2), 1.q(10), "(1{5};2{4};3)[head]{2}"),
    IGNORING(MM)((1.q(5) `;` 2.q(4) `;` 3).q(6), lst[Int].q(6).head.q(2), 1.q(60), "(1{5};2{4};3){6}[head]{2}"),
    comment("|-lst"),
    IGNORING(MM)((1.q(5) `|` 2.q(4) `|` 3).q(6), lst[Int].q(6).head.q(2), 1.q(60), "(1{5}|2{4}|3){6}[head]{2}"),
    IGNORING(MM)((1.q(0) `|` 2.q(4) `|` 3).q(6), lst[Int].q(6).head.q(2), 2.q(48), "(1{0}|2{4}|3){6}[head]{2}"),
    comment(";-rec"),
    testing(str("a") -> int(1) `_;` str("b") -> int(2) `_;` str("c") -> int(3), rec.head, 1, "('a'->1;'b'->2;'c'->3) => rec[head]"),
    testing(str("a") -> int(1) `_;` str("b") -> int(2) `_;` str("c") -> int(3), head, 1, "('a'->1;'b'->2;'c'->3)[head]"),
    testing((str("a") -> (str("b") -> int(1) `_;` str("c") -> int(2))) `_;` str("c") -> int(3), head, (str("b") -> int(1) `_;` str("c") -> int(2)), "('a'->('b'->1;'c'->2);'d'->3)[head]"),
  ))
