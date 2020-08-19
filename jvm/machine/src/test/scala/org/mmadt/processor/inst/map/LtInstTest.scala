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
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.map.LtOp
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.storage.StorageFactory.{bfalse, bool, btrue, int, real}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class LtInstTest extends BaseInstTest(

  testSet("[lt] table testing",
    comment("int"),
    testing(int(2), lt(1), bfalse, "2 < 1"),
    testing(int(2).q(10), lt(1), bfalse.q(10), "2{10} < 1"),
    testing(int(2), lt(int(1).q(10)), bfalse, "2 < 1{10}"),
    testing(int(2), lt(int), bfalse, "2 < int"),
    testing(int(2), lt(mult(int)), btrue, "2 < *int"),
    testing(int, lt(int(2)), int.lt(int(2)), "int < 2"),
    testing(int.q(10), lt(int(2)), int.q(10).lt(int(2)), "int{10} < 2"),
    testing(int, lt(int), int.lt(int), "int < int"),
    comment("int strm"),
    testing(int(1, 2, 3), lt(2), bool(true, false, false), "[1,2,3] => [lt,2]"),
    testing(int(1, 2, 3), lt(int(2).q(10)), bool(true, false, false), "[1,2,3] => [lt,2{10}]"),
    testing(int(1, 2, 3), lt(int(2)).q(10), bool(btrue.q(10), bfalse.q(10), bfalse.q(10)), "[1,2,3][lt,2]{10}"),
    testing(int(1, 2, 3), lt(int), bool(false, false, false), "[1,2,3][lt,int]"),
    testing(int(1, 2, 3), lt(mult(int)), bool(false, true, true), "[1,2,3][lt,[mult,int]]"),
    comment("real"),
    testing(real(2.0), lt(1.0), bfalse, "2.0[lt,1.0]"),
    testing(real(2.0), lt(real), bfalse, "2.0 => [lt,real]"),
    testing(real(2.0), lt(mult(real)), true, "2.0 => <*real"),
    testing(real, lt(real(2.0)), real.lt(2.0), "real[lt,2.0]"),
    testing(real, lt(real), real.lt(real), "real[lt,real]"),
    comment("real strm"),
    testing(real(1.0, 2.0, 3.0), lt(2.0), bool(true, false, false), "[1.0,2.0,3.0][lt,2.0]"),
    testing(real(1.0, 2.0, 3.0), lt(real), bool(false, false, false), "[1.0,2.0,3.0] < real"),
    testing(real(1.0, 2.0, 3.0), lt(mult(real)), bool(false, true, true), "[1.0,2.0,3.0][lt,[mult,real]]"),
    comment("exception"),
    testing(bfalse, lt(btrue), LanguageException.unsupportedInstType(bfalse, LtOp(btrue)), "false < true")
  )) {
}
