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

import org.mmadt.language.obj.Obj.{booleanToBool, intToInt}
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp._
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.storage.StorageFactory.{int, lst, rec, str, _}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class EmptyInstTest extends BaseInstTest(
  testSet("[empty] table test", List(MM, MMX),
    comment("lst"),
    testing(lst.q(20), lst.q(20).empty.q(5), lst.q(20).empty.q(5), "lst{20} => lst{20}[empty]{5}"),
    testing(lst(), empty, true, "()[empty]"),
    testing(lst(), lst.empty, true, "() => lst[empty]"),
    testing(lst().q(2), empty.q(5), true.q(10), "(){2}[empty]{5}"),
    testing(lst().q(2), lst.q(2).empty.q(5), true.q(10), "(){2} => lst{2}[empty]{5}"),
    testing(lst().q(20), lst.q(20).empty.q(5), true.q(100), "(){20} => lst{20}[empty]{5}"),
    testing(lst(), lst.empty.q(5), true.q(5), "() => lst[empty]{5}"),
    testing(1 `,`, empty, false, "(1)[empty]"),
    testing(1 `,`, lst.empty, false, "(1) => lst[empty]"),
    testing(1.q(10) `,`, empty.q(5), false.q(5), "(1{10})[empty]{5}"),
    testing(1.q(10) `,`, lst.empty.q(5), false.q(5), "(1{10}) => lst[empty]{5}"),
    testing(lst(1.q(10)).q(20), lst.q(20).empty.q(5), false.q(100), "(1{10}){20} => lst{20}[empty]{5}"),
    comment("rec"),
    testing(rec(), empty, true, "(->)[empty]"),
    testing(rec(), rec.empty, true, "(->) => rec[empty]"),
    testing(rec().q(2), empty.q(5), true.q(10), "(->){2}[empty]{5}"),
    testing(rec().q(2), rec.q(2).empty.q(5), true.q(10), "(->){2} => rec{2}[empty]{5}"),
    testing(rec(), rec.empty.q(5), true.q(5), "(->) => rec[empty]{5}"),
    testing(str("a") -> int(1), empty, false, "('a'->1)[empty]"),
    testing(str("a") -> int(1), rec.empty, false, "('a'->1) => rec[empty]"),
    testing(str("a") -> 1.q(10), empty.q(5), false.q(5), "('a'->1{10})[empty]{5}"),
    testing(str("a") -> 1.q(10), rec.empty.q(5), false.q(5), "('a'->1{10}) => rec[empty]{5}"),
    testing((str("a") -> 1.q(10)).q(20), rec.q(20).empty.q(5), false.q(100), "('a'->1{10}){20} => rec{20}[empty]{5}"),
  ))