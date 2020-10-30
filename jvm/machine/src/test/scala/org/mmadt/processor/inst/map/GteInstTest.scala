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

import org.mmadt.language.LanguageException
import org.mmadt.language.obj.Obj.{booleanToBool, intToInt}
import org.mmadt.language.obj.`type`.__.{gte, mult}
import org.mmadt.language.obj.op.map.GteOp
import org.mmadt.language.obj.op.trace.ModelOp.{MM, MMX, NONE}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, excepting, testSet, testing}
import org.mmadt.storage.StorageFactory.{bool, int}

class GteInstTest extends BaseInstTest(
  testSet("[gte] table test", List( MM, MMX),
    comment("int"),
    testing(2, gte(1), true, "2>=1"),
    testing(2.q(10), int.q(10).gte(1), true.q(10), "2{10} => int{10}[gte,1]"),
    testing(2.q(10), gte(1).q(20), true.q(200), "2{10}[gte,1]{20}"),
    testing(2, gte(1.q(10)), true, "2 >= 1{10}"),
    testing(2, gte(int), true, "2[gte,int]"),
    testing(2, int.gte(mult(int)), false, "2 => int[gte,[mult,int]]"),
    testing(int, int.gte(2), int.gte(2), "int => int>=2"),
    testing(int.q(10), gte(2), int.q(10).gte(2), "int{10} => [gte,2]"),
    testing(int, gte(int), int.gte(int), "int => >=int"),
    testing(int, int.gte(int), int.gte(int), "int => int>=int"),

    testing(int(1, 2, 3), int.q(3).gte(2), bool(false, true, true), "[1,2,3] => int{3}>=2"),
    testing(int(1, 2, 3), gte(2.q(10)), bool(false, true, true), "[1,2,3][gte,2{10}]"),
    testing(int(1, 2, 3), gte(2).q(10), bool(false.q(10), true.q(10), true.q(10)), "[1,2,3][gte,2]{10}"),
    testing(int(1, 2, 3), int.q(3).gte(2).q(10).id, bool(false.q(10), true.q(10), true.q(10)), "[1,2,3] => int{3}[gte,2]{10}[id]"),
    testing(int(1, 2, 3), gte(2).q(10).id.q(5), bool(false.q(50), true.q(50), true.q(50)), "[1,2,3][gte,2]{10}[id]{5}"),
    testing(int(1, 2, 3), gte(int), bool(true, true, true), "[1,2,3][gte,int][id]"),
    testing(int(1, 2, 3), int.q(3).gte(mult(int)), bool(true, false, false), "[1,2,3] => int{3}[gte,*int]"),
    comment("real"),
    testing(2.0, gte(1.0), true, "2.0 >= 1.0"),
    /*testing(real(2.0).gte(real), btrue, "value"),
    testing(real(2.0).gte(__.mult(real)), bfalse, "value"),
    testing(real.gte(real(2.0)), real.gte(2.0), "type"),
    testing(real.gte(real), real.gte(real), "type"),
    testing(real(1.0, 2.0, 3.0).gte(2.0), bool(false, true, true), "strm"),
    testing(real(1.0, 2.0, 3.0).gte(real), bool(true, true, true), "strm"),
    testing(real(1.0, 2.0, 3.0).gte(__.mult(real)), bool(true, false, false), "strm"),*/
    comment("exceptions"),
    excepting(false, gte(true), LanguageException.unsupportedInstType(false, GteOp(true)), "false >= true")
  ))