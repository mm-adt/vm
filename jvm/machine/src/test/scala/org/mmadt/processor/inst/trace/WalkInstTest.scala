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
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.language.obj.{Lst, Obj}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{IGNORING, comment, testSet, testing}
import org.mmadt.processor.inst.trace.WalkInstTest._
import org.mmadt.storage.StorageFactory.{int, lst, strm}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object WalkInstTest {

  private val natType: __ = __("nat") <= int.is(int.gt(0))
  private val dateType: Lst[__] = ((__("nat") <= __("nat").is(lte(12))) `;` (__("nat") <= __("nat").is(lte(31))) `;` __("nat")).named("date")
  private val noYearDateType: __ = __("date") <= ((__("nat") <= __("nat").is(lte(12))) `;` (__("nat") <= __("nat").is(lte(31)))).put(2, 2009)
  //private val monthDayType: Lst[Obj] = __("moday") <= (int `;` int).named("moday")
  private val modayType: Obj = (int -< (int `;` int)).named("moday")
  private val MODEL: Model = ModelOp.EMPTY.defining(natType).defining(dateType).defining(noYearDateType).defining(modayType)
  private val PARSE_MODEL: Model = org.mmadt.storage.model("social")
}
class WalkInstTest extends BaseInstTest(
  testSet("[walk] table test", MODEL,
    comment("int=>nat"),
    testing(int, int.walk(__("nat")), lst <= int.walk(__("nat")), "int => int[walk,nat]"),
    testing(int, walk(__("nat")), lst <= int.walk(__("nat")), "int => [walk,nat]"),
    testing(5, int.walk(__("nat")), ((int `;` natType) `,`) <= 5.walk(__.named("nat")), "5 => int[walk,nat]"),
    testing(-5, int.walk(__("nat")), ((int `;` natType) `,`) <= (-5).walk(__.named("nat")), "-5 => int[walk,nat]"),
    comment("int=>date"),
    testing(int, int.walk(__("moday")), lst <= int.walk(__("moday")), "int => int[walk,moday]"),
    testing(5, int.walk(__("moday")),
      ((int `;` natType `;` modayType) `,`
        (int `;` modayType)) <= 5.walk(__("moday")), "5 => int[walk,moday]"),
    testing(5, int.walk(__("date")),
      ((int `;` natType `;` modayType `;` noYearDateType) `,`
        (int `;` modayType `;` noYearDateType)) <= 5.walk(__("date")), "5 => int[walk,date]"),
    comment("int-<[walk]"),
    IGNORING("eval-5")(50, int.split(walk(__("nat")).head).merge, 50, "50 => int[split,[walk,nat][head]][merge][merge]"),
    IGNORING("eval-5")(50, split(walk(__("nat")).head).merge, 50, "50 => [split,[walk,nat][head]][merge][merge]"), // TODO: use exec() in parser to compose monoid
    IGNORING("eval-5")(50, int.split(walk(__("moday")).head).merge, (50 `;` 50).named("moday"), "50 => int[split,[walk,moday][head]][merge]"),
    IGNORING("eval-5")(int(50, 100), int.q(2).split(walk(__("moday")).head).merge[Obj], strm((100 `;` 100).named("moday"), (50 `;` 50).named("moday"))), //"[50,100] => int{2}[split,[walk,moday][head]][merge]"
  )) {

  test("test model test") {
    assertResult(MODEL)(PARSE_MODEL)
    //println(MODEL)
    //println(PARSE_MODEL)
    //println(BaseInstTest.engine.eval("int => int[walk,nat]", BaseInstTest.bindings(MODEL)))
  }
}

