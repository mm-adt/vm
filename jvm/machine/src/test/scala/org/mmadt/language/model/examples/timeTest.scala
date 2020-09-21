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

package org.mmadt.language.model.examples

import org.mmadt.language.model.examples.timeTest.TIME
import org.mmadt.language.obj.Obj.{intToInt, symbolToToken}
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp.{MM, Model}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.storage.model

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object timeTest {
  val TIME:Model = MM
    .defining('date <= ('nat.is(lte(12)) `;` 'nat.is(lte(31)) `;` 'nat))
    .defining('date <= ('nat.is(lt(12)) `;` 'nat.is(lte(31))).put(2, 2020)).merging(model('num)).named("time")
}

class timeTest extends BaseInstTest(
  testSet("time model table test", List(TIME, model('time)),
    comment("date"),
    testing(8 `;` 26 `;` 2020, 'date, 'date('nat(8) `;` 'nat(26) `;` 'nat(2020)), "(8;26;2020) => date"),
    testing(8 `;` 26, 'date, 'date('nat(8) `;` 'nat(26) `;` 'nat(2020)), "(8;26) => date"),
  ))