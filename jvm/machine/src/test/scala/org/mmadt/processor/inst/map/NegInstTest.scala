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
import org.mmadt.language.obj.Obj.{doubleToReal, intToInt}
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.map.NegOp
import org.mmadt.language.obj.op.trace.ModelOp.{MM, MMX}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil._
import org.mmadt.storage.StorageFactory.{int, real}

class NegInstTest extends BaseInstTest(
  testSet("[neg] int", List(MM), // MMX),
    testing(2, int.neg, -2, "2 => int[neg]"),
    testing(2.q(2), int.q(2).neg, -2.q(2), "2{2} => int{2}[neg]"),
    testing(-2, neg, 2, "-2[neg]"),
    testing(-2, neg.q(4).neg.q(2), -2.q(8), "-2[neg]{4}[neg]{2}"),
    IGNORING(MM)(int.neg, int.neg, int.neg.neg, "int[neg] => int[neg]"),
    IGNORING(MM)(int, int.neg.neg, int.neg.neg, "int => int[neg][neg]"),
    testing(int(-1, -2, -3), int.q(3).neg, int(1, 2, 3), "[-1,-2,-3] => int{3}[neg]"),
    comment("exceptions"),
    excepting("a", neg, LanguageException.unsupportedInstType("a", NegOp()), "'a'[neg]")
  ),
  testSet("[neg] real", List(MM), // MMX),
    testing(2.0, real.neg, -2.0, "2.0 => real[neg]"),
    testing(-2.0, neg, 2.0, "-2.0[neg]"),
    testing(real, real.neg, real.neg, "real => real[neg]"),
    testing(real(-1.0, -2.0, -3.0), real.q(3).neg, real(1.0, 2.0, 3.0), "[-1.0,-2.0,-3.0] => real{3}[neg]"),
    testing(real(-1.0, -2.0, -3.0), neg.q(10), real(1.0.q(10), 2.0.q(10), 3.0.q(10)), "[-1.0,-2.0,-3.0][neg]{10}"),
    comment("exceptions"),
    excepting("a", neg, LanguageException.unsupportedInstType("a", NegOp()), "'a'[neg]")
  ),
  testSet("[neg] int w/ mm and mmx", List(MM, MMX),
    comment("int"),
    testing(int, neg, int.neg, "int[neg]"),
    testing(int, neg.neg, int, "int[neg][neg]"),
    testing(int, neg.neg.neg, int.neg, "int[neg][neg][neg]"),
    testing(int, neg.neg.neg.neg, int, "int[neg][neg][neg][neg]"),
  ))