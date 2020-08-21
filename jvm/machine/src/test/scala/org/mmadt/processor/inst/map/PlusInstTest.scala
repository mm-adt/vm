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

import org.mmadt.language.obj.Obj.intToInt
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.storage.StorageFactory._

class PlusInstTest extends BaseInstTest(
  testSet("[plus] table test",
    comment("int"),
    testing(2, plus(2), 4, "2[plus,2]"),
    testing(2, int.plus(2), 4, "2 => int[plus,2]"),
    testing(2.q(10), int.q(10).plus(2), 4.q(10), "2{10} => int{10}[plus,2]"),
    testing(2.q(10), plus(2).q(20), 4.q(200), "2{10}[plus,2]{20}"),
    testing(2, plus(int(2).q(10)), int(4), "2[plus,2{10}]"),
    testing(2, plus(int), int(4), "2[plus,int]"),
    testing(2, plus(plus(int)), int(6), "2 => [plus,[plus,int]]"),
    testing(int, plus(2), int.plus(2), "int[plus,2]"),
    testing(int.q(10), int.q(10).plus(2), int.q(10).plus(int(2)), "int{10} => int{10}[plus,2]"),
    testing(int, plus(int), int.plus(int), "int => [plus,int]"),
    testing(int(1, 2, 3), plus(2), int(3, 4, 5), "[1,2,3][plus,2]"),
    testing(int(1, 2, 3), plus(2.q(10)), int(3, 4, 5), "[1,2,3][plus,2{10}]"),
    testing(int(1, 2, 3), int.q(3).plus(2).q(10), int(3.q(10), 4.q(10), 5.q(10)), "[1,2,3] => int{3}[plus,2]{10}"),
    testing(int(1, 2, 3), plus(2).q(10), int(3.q(10), 4.q(10), 5.q(10)), "[1,2,3][plus,2]{10}"),
    testing(int(1, 2, 3), plus(int), int(2, 4, 6), "[1,2,3][plus,int]"),
    testing(int(1, 2, 3), plus(plus(int)), int(3, 6, 9), "[1,2,3][plus,[plus,int]]"),
    comment("real"),
    testing(2.0, plus(2.0), 4.0, "2.0[plus,2.0]"),
    testing(2.0, plus(real), 4.0),
    testing(2.0, plus(plus(real)), 6.0, "2.0[plus,[plus,real]]"),
    testing(real, plus(2.0), real.plus(2.0), "real => [plus,2.0]"),
    testing(real, plus(real), real.plus(real), "real => [plus,real]"),
    testing(real(1.0, 2.0, 3.0), plus(2.0), real(3.0, 4.0, 5.0), "[1.0,2.0,3.0][plus,2.0]"),
    testing(real(1.0, 2.0, 3.0), real.q(3).plus(real), real(2.0, 4.0, 6.0), "[1.0,2.0,3.0] => real{3}[plus,real]"),
    testing(real(1.0, 2.0, 3.0), real.q(3).plus(plus(__)), real(3.0, 6.0, 9.0), "[1.0,2.0,3.0] => real{3}[plus,[plus,_]]"),
  ), testSet("[plus] table test w/ mm", ModelOp.MM,
    comment("int"),
    testing(int, plus(0), int, "int => [plus,0]"),
    testing(__, int.plus(0), int, "int[plus,0]"),
    testing(int, plus(plus(plus(0))), int.plus(plus(int)), "int => [plus,[plus,[plus,0]]]"),
    testing(int, mult(1), int, "int => [mult,1]"),
    testing(__, int.mult(1), int, "int[mult,1]"),
    testing(int, plus(plus(plus(mult(0)))), int.plus(plus(int)), "int => [plus,[plus,[plus,[mult,0]]]]"),
  ))