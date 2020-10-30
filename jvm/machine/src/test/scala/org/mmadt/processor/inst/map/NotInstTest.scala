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
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp.{MM, MMX, NONE}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{testSet, testing}
import org.mmadt.storage.StorageFactory._

class NotInstTest extends BaseInstTest(
  testSet("[not] table test", List(MM, MMX),
    testing(true, not(__), false, "true[not,_]"),
    testing(true, not(bool), false, "true[not,bool]"),
    testing(true.q(19), not(__), false.q(19), "true{19} => [not,_]"),
    testing(false, not(__), true, "false[not,_]"),
    testing(5, int.gt(10).not(bool), true, "5=>int>10[not,bool]"),
    testing(5, int.gt(10).q(10).not(__), true.q(10), "5 => int[gt,10]{10}[not,_]"),
    testing(int, gt(10).not(__), int.gt(10).not(bool), "int[gt,10][not,_]"),
    testing(int, gt(10).q(0).not(__), bool.q(qZero), "int[gt,10]{0}[not,_]"),
    testing(13.q(2), and(int.gt(10), int.lt(15)).q(10).not(__), false.q(20)),
  ))


