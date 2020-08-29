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

package org.mmadt.processor.inst.trace

import org.mmadt.language.obj.Obj.intToInt
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.trace.ModelOp
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.processor.inst.trace.WalkInstTest.MODEL
import org.mmadt.storage.StorageFactory.int

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object WalkInstTest {
  private val natType: Type[__] = __("nat") <= int.is(int.gt(0))
  private val bigType: Type[__] = __("big") <= __("nat").plus(10)
  private val incrType: Type[__] = __("nat") <= int.plus(10)
  private val MODEL: Model = ModelOp.EMPTY.defining(natType).defining(bigType).defining(incrType)
}
class WalkInstTest extends BaseInstTest(
  testSet("[walk] table test", MODEL,
    comment("int"),
    testing(5, int.walk(__("nat")), 5.named("nat"), "5 => int[walk,nat]"),
    testing(int, int.walk(__("big")), __("big") <= int.walk(__("big")), "int => int[walk,big]"),
    testing(5, int.walk(__("big")), 15.named("big"), "5 => int[walk,big]"),
    testing(-5, int.walk(__("big")), 15.named("big"), "-5 => int[walk,big]"),

  ))

