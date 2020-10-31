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

import org.mmadt.language.obj.Obj._
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__.mult
import org.mmadt.language.obj.op.trace.ModelOp.{MM, MMX, NONE}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class MultInstTest extends BaseInstTest(
  testSet("[mult] table test", List(MM, MMX),
    comment("int"),
    testing(2, mult(2), 4),
    testing(2.q(10), int.q(10).mult(2), 4.q(10)),
    testing(2.q(10), int.q(10).mult(2).q(20), 4.q(200)),
    testing(2, mult(2).q(10), 4.q(10)),
    testing(2, mult(int), 4),
    testing(2, int.mult(mult(int)), 8, "2 => int**int"),
    testing(int, mult(2), int.mult(2)),
    testing(int.q(10), int.q(10).mult(2), int.q(10).mult(2)),
    testing(int, mult(int), int.mult(int), "int[mult,int]"),
    testing(int, mult(int), int.mult(__), "int[mult,_]"),
    testing(int(1, 2, 3), mult(2), int(2, 4, 6)),
    testing(int(1, 2, 3), mult(2), int(2, 4, 6)),
    testing(int(1, 2, 3), mult(2).q(10), int(2.q(10), 4.q(10), 6.q(10))),
    testing(int(1, 2, 3), mult(int), int(1, 4, 9)),
    testing(int(1, 2, 3), mult(mult(int)), int(1, 8, 27)),
    comment("real"),
    testing(2.0, mult(2.0), 4.0),
    testing(2.0, real.mult(real), 4.0, "2.0 => real[mult,real]"),
    testing(2.0, mult(mult(real)), 8.0, "2.0[mult[mult,real]]"),
    testing(real, mult(2.0), real.mult(2.0), "real*2.0"),
    testing(real, real.mult(real), real.mult(real), "real*real"),
    testing(real.q(5), real.q(5).mult(real.q(6)), real.q(5) <= real.q(5).mult(real.q(6)), "real{5}*real{6}"),
    testing(real(1.0, 2.0, 3.0), mult(2.0), real(2.0, 4.0, 6.0), "[1.0,2.0,3.0][mult,2.0]"),
    testing(real(1.0, 2.0, 3.0), mult(real), real(1.0, 4.0, 9.0), "[1.0,2.0,3.0] => [mult,real]"),
    testing(real(1.0, 2.0, 3.0), mult(mult(real)), real(1.0, 8.0, 27.0), "[1.0,2.0,3.0]**real"),
  ))
 