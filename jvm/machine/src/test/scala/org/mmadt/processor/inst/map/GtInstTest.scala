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
import org.mmadt.language.obj.Obj.{booleanToBool, intToInt, stringToStr}
import org.mmadt.language.obj.`type`.__.{gt, id, mult}
import org.mmadt.language.obj.op.map.GtOp
import org.mmadt.language.obj.op.trace.ModelOp.{MM, MMX, NONE}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil._
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class GtInstTest extends BaseInstTest(
  testSet("[gt] table test", List(NONE, MM, MMX),
    comment("int"),
    testing(2, gt(1), true, "2 => [gt,1]"),
    testing(2.q(10), gt(1), true.q(10), "2{10}[gt,1]"),
    testing(2.q(10), gt(1).q(20), true.q(200), "2{10}[gt,1]{20}"),
    testing(2, gt(1.q(10)), true, "2[gt,1{10}]"),
    testing(2, gt(int), false, "2[gt,int]"),
    testing(2, gt(mult(int)), false, "2[gt,*int]"),
    testing(int, gt(2), int.gt(int(2)), "int[gt,2]"),
    testing(int.q(10), gt(2), int.q(10).gt(int(2)), "int{10}[gt,2]"),
    testing(int, gt(int), int.gt(int), "int > int"),
    testing(int(1, 2, 3), gt(2), bool(false, false, true), "[1,2,3]>2"),
    testing(int(1, 2, 3), gt(2.q(10)), bool(false, false, true), "[1,2,3]>2{10}"),
    testing(int(1, 2, 3), gt(2).q(10), bool(false.q(10), false.q(10), true.q(10)), "[1,2,3]>{10}2"),
    testing(int(1, 2, 3), gt(2).q(10), bool(false.q(20), true.q(10))),
    IGNORING(MM)(int(1, 2, 3), gt(2).q(10).id, bool(false.q(10), false.q(10), true.q(10)), "[1,2,3]>{10}2[id]"),
    IGNORING(MM)(int(1, 2, 3), gt(2).q(10).id.q(5), bool(false.q(50), false.q(50), true.q(50)), "[1,2,3][gt,2]{10}[id]{5}"),
    IGNORING(MM)(int(1, 2, 3), id.gt(2).q(10).id.q(5), bool(false.q(50), false.q(50), true.q(50)), "[1,2,3][id][gt,2]{10}[id]{5}"),
    IGNORING(MM)(int(1, 2, 3), gt(2).id.q(10).id.q(5), bool(false.q(50), false.q(50), true.q(50)), "[1,2,3][gt,2][id]{10}[id]{5}"),
    testing(int(1, 2, 3), gt(int), bool(false, false, false), "[1,2,3][gt,int]"),
    testing(int(1, 2, 3), gt(mult(int)), bool(false, false, false), "[1,2,3][gt,[mult,int]]"),
    comment("real"),
    testing(2.0, gt(1.0), true),
    testing(2.0, gt(real), false),
    testing(2.0, gt(mult(real)), false),
    testing(real, gt(real(2.0)), real.gt(2.0)),
    testing(real, gt(real), real.gt(real)),
    testing(real(1.0, 2.0, 3.0), gt(2.0).q(3), bool(false.q(6), true.q(3))),
    IGNORING(MM)(real(1.0, 2.0, 3.0), gt(2.0).id.q(3), bool(false.q(6), true.q(3)), "[1.0,2.0,3.0][gt,2.0][id]{3}"),
    testing(real(1.0, 2.0, 3.0), gt(2.0), bool(false, false, true)),
    testing(real(1.0, 2.0, 3.0), gt(real), bool(false, false, false)),
    testing(real(1.0, 2.0, 3.0), gt(mult(real)), bool(false, false, false)),
    comment("str"),
    testing("b", gt("a"), true),
    testing("b".q(10), gt("a"), true.q(10)),
    testing("b".q(10), gt("a").q(20), true.q(200)),
    testing("b", gt("a".q(10)), true),
    testing("b", gt(str), false),
    testing(str, gt("b"), str.gt("b")),
    testing(str.q(10), gt("b"), str.q(10).gt("b")),
    testing(str, gt(str), str.gt(str)),
    testing(str("a", "b", "c"), gt("b"), bool(false, false, true)),
    testing(str("a", "b", "c"), gt("b".q(10)), bool(false, false, true)),
    testing(str("a", "b", "c"), gt("b").q(10), bool(false.q(10), false.q(10), true.q(10))),
    testing(str("a", "b", "c"), gt(str), bool(false, false, false)),
    comment("exceptions"),
    excepting(false, gt(true), LanguageException.unsupportedInstType(false, GtOp(true)), "false > true"),
    excepting(false, gt("b"), LanguageException.unsupportedInstType(false, GtOp("b")), "false > 'b'"),
  ))

