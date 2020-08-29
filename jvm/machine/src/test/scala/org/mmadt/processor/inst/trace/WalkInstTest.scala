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

import org.mmadt.language.obj.Lst
import org.mmadt.language.obj.Obj.intToInt
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.processor.inst.trace.WalkInstTest.{MODEL, PARSE_MODEL}
import org.mmadt.storage.{KV, model}
import org.mmadt.storage.StorageFactory.{int, lst}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object WalkInstTest {

  private val natType: __ = __("nat") <= int.is(int.gt(0))
  private val dateType: Lst[__] = ((__("nat") <= __("nat").is(lte(12))) `;` (__("nat") <= __("nat").is(lte(31))) `;` __("nat")).named("date")
  private val noYearDateType: __ = __("date") <= ((__("nat") <= __("nat").is(lte(12))) `;` (__("nat") <= __("nat").is(lte(31)))).put(2, 2009)
  private val monthDayTuple: __ = __("moday") <= int -< (__ `,` __)
  private val MODEL: Model = ModelOp.EMPTY.defining(natType).defining(dateType).defining(noYearDateType).defining(monthDayTuple)
  private val PARSE_MODEL:Model = org.mmadt.storage.model("social")
}
class WalkInstTest extends BaseInstTest(
  testSet("[walk] table test", PARSE_MODEL,
    comment("int=>nat"),
    testing(int, int.walk(__("nat")), lst <= int.walk(__("nat")), "int => int[walk,nat]"),
    testing(int, walk(__("nat")), lst <= int.walk(__("nat")), "int => [walk,nat]"),
    testing(5, int.walk(__("nat")), ((int `;` (__("nat") <= int.is(int.gt(0)))) `,`) <= int.walk(__("nat")), "5 => int[walk,nat]"),
    testing(-5, int.walk(__("nat")), lst <= int.walk(__("nat"))),
    comment("int=>date"),
    testing(int, int.walk(__("moday")), lst <= int.walk(__("moday")), "int => int[walk,moday]"),
    testing(5, int.walk(__("moday")), ((int `;` __("moday") <= int -< (__ `,` __).:=(__ `;`)) `,`) <= int.walk(__("moday")), "5 => int[walk,moday]"),
    //testing(5, int.walk(__("moday")).get(0), (int `;` __("moday") <= int -< (__ `,` __)) <= int.walk(__("moday").get(0)), "5 => int[walk,moday].0"),
    testing(-5, int.walk(__("nat")), lst <= int.walk(__("nat"))),
    //testing(5, int.walk(__("big")), 15.named("big"), "5 => int[walk,big]"),
    //testing(-5, int.walk(__("big")), 15.named("big"), "-5 => int[walk,big]"),
  )) {
  test("") {
    println(MODEL)
    println(PARSE_MODEL)

  }
}

