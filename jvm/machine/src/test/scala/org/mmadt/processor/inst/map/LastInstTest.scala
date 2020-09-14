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
import org.mmadt.language.obj.`type`.__.last
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.storage.StorageFactory.{int, lst, rec, str}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class LastInstTest extends BaseInstTest(
  testSet("[last] table test",
    comment(";-lst"),
    testing(1 `;` 2 `;` 3, lst.last, 3, "(1;2;3) => lst[last]"),
    testing(1 `;` 2 `;` 3, last, 3, "(1;2;3)[last]"),
    testing(1 `;` (2 `;` 3), last, (2 `;` 3), "(1;(2;3))[last]"),
    testing(1 `;` 2.q(4) `;` 3.q(5), lst.last, 3.q(5), "(1;2{4};3{5}) => lst[last]"),
    testing(1.q(5) `;` 2.q(4) `;` 3.q(5), lst[Int].last.q(2), 3.q(10), "(1{5};2{4};3{5})[last]{2}"),
    testing((1 `;` 2.q(4) `;` 3.q(5)).q(6), lst[Int].q(6).last.q(2), 3.q(60), "(1;2{4};3{5}){6}[last]{2}"),
    comment("|-lst"),
    testing((1.q(5) `|` 2.q(4) `|` 3).q(6), lst[Int].q(6).last.q(2), 3.q(12), "(1{5}|2{4}|3){6}[last]{2}"),
    testing((1.q(0) `|` 2.q(4) `|` 3.q(4)).q(6), lst[Int].q(6).last.q(2), 3.q(48), "(1{0}|2{4}|3{4}){6}[last]{2}"),
    comment(";-rec"),
    testing(str("a") -> int(1) `_;` str("b") -> int(2) `_;` str("c") -> int(3), rec.last, 3, "('a'->1;'b'->2;'c'->3) => rec[last]"),
    testing(str("a") -> int(1) `_;` str("b") -> int(2) `_;` str("c") -> int(3), last, 3, "('a'->1;'b'->2;'c'->3)[last]"),
    testing(str("a") -> int(1) `_;` str("b") -> (str("c") -> int(2) `_;` str("d") -> int(3)), last, (str("c") -> int(2) `_;` str("d") -> int(3)), "('a'->1;'b'->('c'->2;'d'->3))[last]"),
  ))