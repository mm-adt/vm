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

package org.mmadt.processor.inst.filter

import org.mmadt.language.obj.Obj.{booleanToBool, doubleToReal, intToInt}
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp.{MM, MMX, NONE}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{IGNORING, comment, testSet, testing}
import org.mmadt.storage.StorageFactory._

class IsInstTest extends BaseInstTest(
  testSet("[is] table test", List(NONE, MM, MMX),
    comment("int"),
    testing(2, is(true), 2, "2[is,true]"),
    testing(2.q(10), int.q(10).is(true), 2.q(10), "2{10} => int{10}[is,true]"),
    testing(2.q(10), is(true).q(20), 2.q(200), "2{10}[is,true]{20}"),
    testing(2, is(true.q(10)), 2, "2[is,true{10}]"),
    testing(2, is(bool), 2.is(bool), "2[is,bool]"),
    testing(2, int.is(gt(int)), zeroObj, "2 => int[is>int]"),
    testing(2, is(gte(int)), 2, "2[is>=int]"),
    testing(2.q(10), int.q(10).is(gte(int)), 2.q(10), "2{10} => int{10}[is>=int]"),
    IGNORING(MM)(2, int.is(gte(int)).q(10), 2.q(10), "2 => int[is>=int]{10}"),
    IGNORING(MM)(2.q(10), is(gte(int)).q(20), 2.q(200), "2{10}[is>=int]{20}"),
    testing(int, int.is(true), int.is(true), "int => int[is,true]"),
    testing(int.q(10), is(true), int.q(10).is(true), "int{10} => [is,true]"),
    testing(int(1, 2, 3), is(true), int(1, 2, 3), "[1,2,3][is,true]"),
    testing(int(1, 2, 3), is(false), zeroObj, "[1,2,3][is,false]"),
    IGNORING("eval-5")(int(1, 2, 3), int.q(3).is(int.gt(2.q(10))), 3, "[1,2,3] => int{3}[is,int[gt,2{10}]]"),
    IGNORING(List((NONE, "eval-5"), (MM, null)))(int(1, 2, 3), int.q(3).is(gte(2)).q(10), int(2.q(10), 3.q(10)), "[1,2,3] => int{3}[is,>=2]{10}"),
    testing(int(1, 2, 3), is(gt(int)), zeroObj, "[1,2,3][is>int]"),
    IGNORING("eval-5")(int(1, 2, 3), int.q(3).is(gte(mult(int))), 1, "[1,2,3] => int{3}[is>=*int]"),
    comment("real"),
    testing(2.0, is(true), 2.0, "2.0[is,true]"),
    testing(2.0, is(false), zeroObj, "2.0[is,false]"),
    testing(2.0, is(real.gt(real.mult(real))), zeroObj, "2.0[is,real[gt,real[mult,real]]]"),
    testing(2.0, real.is(gt(mult(real))), zeroObj, "2.0 => real[is>*real]"),
    testing(real, is(real.gt(2.0)), real.is(real.gt(2.0)), "real[is,real>2.0]"),
    testing(real, real.is(gt(2.0)), real.is(real.gt(2.0)), "real => real[is>2.0]"),
    IGNORING("eval-1", "eval-2", "eval-3", "eval-4", "eval-5")(real, is(bool), real.is(bool), "real[is,bool]"),
    IGNORING("eval-5")(real(1.0, 2.0, 3.0), is(real.gt(2.0)), 3.0, "[1.0,2.0,3.0][is,real>2.0]"),
    testing(real(1.0, 2.0, 3.0), real.q(3).is(real.gt(real)), zeroObj, "[1.0,2.0,3.0] => real{3}[is,real>real]"),
    testing(real(1.0, 2.0, 3.0), is(gt(mult(real))), zeroObj, "[1.0,2.0,3.0][is>*real]"),
    testing(real(1.0, 2.0, 3.0), is(lte(mult(real))), real(1.0, 2.0, 3.0), "[1.0,2.0,3.0][is,=<*real]"),
  ))
