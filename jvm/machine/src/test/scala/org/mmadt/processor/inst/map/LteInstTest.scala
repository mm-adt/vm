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
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp._
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, excepting, testSet, testing}
import org.mmadt.storage.StorageFactory.{bool, int, real}

class LteInstTest extends BaseInstTest(
  testSet("[lte] table testing", List(NONE, MM, MMX),
    comment("int"),
    testing(2, lte(1), false, "2=<1"),
    testing(2.q(10), lte(1), false.q(10), "2{10}[lte,1]"),
    testing(2.q(10), lte(1).q(20), false.q(200), "2{10}=<{20}1"),
    testing(2, lte(1.q(10)), false, "2=<1{10}"),
    testing(2, lte(int), true, "2 => [lte,int]"),
    testing(2, lte(mult(int)), true, "2[lte,*int]"),
    testing(int, lte(2), int.lte(2), "int[lte,2]"),
    testing(int.q(10), lte(2), int.q(10).lte(2), "int{10}[lte,2]"),
    testing(int, lte(int), int.lte(int), "int[lte,int]"),
    testing(int(1, 2, 3), lte(2), bool(true, true, false), "[1,2,3][lte,2]"),
    testing(int(1, 2, 3), lte(2.q(10)), bool(true, true, false), "[1,2,3][lte,2{10}]"),
    testing(int(1, 2, 3), int.q(3).lte(2).q(10), bool(true.q(10), true.q(10), false.q(10)), "[1,2,3]=>int{3}[lte,2]{10}"),
    testing(int(1, 2, 3), lte(int), bool(true, true, true), "[1,2,3][lte,int]"),
    testing(int(1, 2, 3), lte(mult(int)), bool(true, true, true), "[1,2,3][lte,[mult,int]]"),
    comment("real"),
    testing(2.0, lte(1.0), false, "2.0=<1.0"),
    testing(2.0, lte(real), true, "2.0=<real"),
    testing(2.0, lte(mult(real)), true, "2.0 => [lte * real]"),
    testing(real, lte(2.0), real.lte(2.0), "real[lte,2.0]"),
    testing(real, lte(real), real.lte(real), "real[lte,real]"),
    testing(real(1.0, 2.0, 3.0), real.q(3).lte(2.0), bool(true, true, false), "[1.0,2.0,3.0] => real{3}[lte,2.0]"),
    testing(real(1.0, 2.0, 3.0), real.q(3).lte(real), bool(true, true, true), "[1.0,2.0,3.0] => real{3}=<real"),
    testing(real(1.0, 2.0, 3.0), real.q(3).lte(mult(real)), bool(true, true, true), "[1.0,2.0,3.0]=>real{3}[lte * real]"),
    comment("exceptions"),
    excepting(false, lte(true), LanguageException.unsupportedInstType(false, lte(true).inst))
  ))
