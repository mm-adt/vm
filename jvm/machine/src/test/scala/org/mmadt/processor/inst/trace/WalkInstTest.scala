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
import org.mmadt.storage.StorageFactory.{int, lst, str, strm}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object WalkInstTest {

  private val natType: __ = 'nat <= int.is(int.gt(0))
  private val dateType: Lst[__] = 'date(('nat <= 'nat.is(lte(12))) `;` ('nat <= 'nat.is(lte(31))) `;` 'nat)
  private val noYearDateType: __ = 'date <= (('nat <= 'nat.is(lte(12))) `;` ('nat <= 'nat.is(lte(31)))).put(2, 2009)
  private val modayType: Obj = 'moday <= (int -< (int `;` int))
  private val sdateType: Obj = 'sdate <= 'moday(int `;` int).:=(str `;` str)
  private val MODEL: Model = ModelOp.MM
    .defining(natType)
    .defining(dateType)
    .defining(noYearDateType)
    .defining(modayType) //.defining(sdateType)
  private val PARSE_MODEL: Model = org.mmadt.storage.model("social")
}
class WalkInstTest extends BaseInstTest(
  testSet("[walk] table test", PARSE_MODEL,
    comment("int=>nat"),
    testing(int, int.walk('nat), lst <= int.walk('nat), "int => int[walk,nat]"),
    testing(int, walk('nat), lst <= int.walk('nat), "int => [walk,nat]"),
    testing(5, int.walk('nat), ((int `;` 'nat) `,`) <= 5.walk('nat), "5 => int[walk,nat]"),
    testing(-5, int.walk('nat), ((int `;` 'nat) `,`) <= (-5).walk('nat), "-5 => int[walk,nat]"),
    comment("int=>date"),
    testing(int, int.walk('moday), lst <= int.walk('moday), "int => int[walk,moday]"),
    testing(5, int.walk('moday),
      ((int `;` 'nat `;` int `;` 'moday) `,`
        (int `;` 'moday)) <= 5.walk('moday), "5 => int[walk,moday]"),
    testing(5, int.walk('date),
      ((int `;` 'nat `;` 'int `;` 'moday `;` 'date) `,`
        (int `;` 'moday `;` 'date)) <= 5.walk('date), "5 ~> date"),
    comment("int-<[walk]"),
    IGNORING("eval-5")(50, int.split(walk('nat).head).merge, 'nat(50), "50 => int[split,[walk,nat][head]][merge][merge]"),
    IGNORING("eval-5")(50, int.split(int.walk('nat).head).merge, 'nat(50), "50 => int[split,[walk,nat][head]][merge][merge]"),
    IGNORING("eval-5")(50, split(walk('nat).head).merge, 'nat(50), "50 => [split,[walk,nat][head]][merge][merge]"), // TODO: use exec() in parser to compose monoid
    IGNORING("eval-5")(50, int.split(walk('moday).head).merge, 'moday(50 `;` 50), "50 => int[split,[walk,moday][head]][merge]"),
    IGNORING("eval-5")(int(50, 100), int.q(2).split(walk('moday).head).merge[Obj], strm('moday(100 `;` 100), 'moday(50 `;` 50))),
    comment("int => far"),
    IGNORING("eval-4", "eval-5", "query-2")(6, __.juxta('date), 'date('nat(6) `;` 'nat(6) `;` 2009), "6 => date"), // TODO: create a "lazy juxta operator"
  )) {

  test("test model test") {
    // println(engine.eval("5 => date.2 => person", bindings(PARSE_MODEL)))
    // println(engine.eval("5 ~> date", bindings(MODEL)))
    //  assertResult(MODEL)(PARSE_MODEL)
    //  println(MODEL)
    println(PARSE_MODEL.domainObj.asInstanceOf[Model].definitions)
    //println(BaseInstTest.engine.eval("int => int[walk,nat]", BaseInstTest.bindings(MODEL)))
  }
}

