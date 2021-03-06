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

import org.mmadt.language.obj.Obj.{intToInt, symbolToToken}
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp.MM
import org.mmadt.language.obj.{Lst, Obj}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.BaseInstTest.{bindings, engine}
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.storage
import org.mmadt.storage.StorageFactory.{int, lst}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object WalkInstTest {

  private val natType:__ = 'nat <= int.is(int.gt(0))
  private val dateType:Lst[__] = 'date(('nat <= 'nat.is(lte(12))) `;`('nat <= 'nat.is(lte(31))) `;` 'nat)
  private val noYearDateType:__ = 'date <= 'moday(('nat <= 'nat.is(lte(12))) `;`('nat <= 'nat.is(lte(31)))).put(2, 2009)
  private val modayType:Obj = 'moday <= (int -< (int `;` int))
  /**
   *
   * /-------------\
   * z-->y-->x-->w-->v
   * \------/
   */
  /*private val CHAIN_MODEL:Model = ModelOp.MM
    .defining('ztype <= int)
    .defining('ytype <= 'ztype.id)
    .defining('xtype <= 'ytype.id)
    .defining('wtype <= 'xtype.id)
    .defining('wtype <= 'ytype.id)
    .defining('vtype <= 'wtype.id)
    .defining('vtype <= 'ztype.id)
  private val CAT3_MODEL:Model = ModelOp.NONE
    .defining('A <= int)
    .defining('B <= __("A"))
    .defining('C <= __("B"))
    .defining('A <= __("C"))*/
}

class WalkInstTest extends BaseInstTest(
  testSet("[walk] table test", List(MM).map(m => m.defining('nat <= int.is(gt(0)))),
    //comment("int~>int"),
    //testing(int, int.walk(int), lst(lst(int)) <= int.walk(int), "int => int[walk,int]"),
    //testing(2, int.walk(int), lst(lst(int)) <= 2.walk(int), "2 => int[walk,int]"),
    //comment("int~>nat"),
    //testing(int, int.walk('nat), lst(int`;`'nat) <= int.walk('nat), "int => int[walk,nat]"),
    /*testing(int, int.walk('nat), lst <= int.walk('nat), "int => int[walk,nat]"),
    testing(int, walk('nat), lst <= int.walk('nat), "int => [walk,nat]"),
    testing(5, int.walk('nat), ((int `;` 'nat) `,`) <= 5.walk('nat), "5 => int[walk,nat]"),
    testing(-5, int.walk('nat), ((int `;` 'nat) `,`) <= (-5).walk('nat), "-5 => int[walk,nat]"),
    comment("int~>date"),
    testing(int, int.walk('moday), lst <= int.walk('moday), "int => int[walk,moday]"),
    testing(5, int.walk('moday), lst(int `;` 'moday) <= 5.walk('moday), "5 => int[walk,moday]"),
    testing(5, int.walk('date), lst((int `;` 'moday `;` 'date)) <= 5.walk('date), "5 ~> date"),
    comment("int-<walk>-"),
    IGNORING("eval-5")(50, int.split(walk('nat).head).merge, 'nat(50), "50 => int[split,[walk,nat][head]][merge]"),
    IGNORING("eval-5")(50, int.split(walk('nat).head).merge, 'nat(50), "50 => int-<:[walk,nat][head]:>-"),
    IGNORING("eval-5")(50, int.split(walk('nat).head).merge, 'nat(50), "50 => int[split,[walk,nat][head]][merge]"),
    IGNORING("eval-5")(50, split(walk('nat).head).merge, 'nat(50), "50 => [split,[walk,nat][head]][merge]"),
    IGNORING("eval-5")(50, int.split(walk('moday).head).merge, 'moday(50 `;` 50), "50 => int[split,[walk,moday][head]][merge]"),
    IGNORING("eval-5")(int(50, 100), int.q(2).split(walk('moday).head).merge[Obj], strm('moday(100 `;` 100), 'moday(50 `;` 50))),
    comment("int=>date"),
    testing(6, __.juxta[Obj]('date), 'date(6 `;` 6 `;` 2009), "6 => date"), // TODO: create a "lazy juxta operator"
  ), testSet("[walk] table test w/ chain model", CHAIN_MODEL,
    comment("linear chains"),
    testing(int, int.walk('ztype), lst <= int.walk('ztype), "int ~> ztype"),
    testing(int, int.walk(int), lst <= int.walk(int), "int ~> int"),
    testing(5, int.walk(int), lst(int `;`) <= 5.walk(int), "5 ~> int"),
    testing(5, int.walk('ztype), lst(int `;` 'ztype) <= 5.walk('ztype), "5 ~> ztype"),
    testing(5, int.walk('ytype), lst(int `;` 'ztype `;` 'ytype) <= 5.walk('ytype), "5 ~> ytype"),
    testing(5, int.walk('xtype), lst(int `;` 'ztype `;` 'ytype `;` 'xtype) <= 5.walk('xtype), "5 ~> xtype"),
    testing(5.q(3), int.q(3).walk('xtype), lst(int `;` 'ztype `;` 'ytype `;` 'xtype).q(3) <= 5.q(3).walk('xtype), "5{3} ~> xtype"),
    comment("branching chains"),
    testing(5, int.walk('wtype),
      ((int `;` 'ztype `;` 'ytype `;` 'xtype `;` 'wtype) `,`
        (int `;` 'ztype `;` 'ytype `;` 'wtype)) <= 5.walk('wtype), "5 ~> wtype"),
    testing(5, int.walk('wtype),
      ((int `;` 'ztype `;` 'ytype `;` 'wtype) `,`
        (int `;` 'ztype `;` 'ytype `;` 'xtype `;` 'wtype)) <= 5.walk('wtype), "5 ~> wtype"),
    testing(5, int.walk('vtype),
      ((int `;` 'ztype `;` 'ytype `;` 'xtype `;` 'wtype `;` 'vtype) `,`
        (int `;` 'ztype `;` 'ytype `;` 'wtype `;` 'vtype) `,`
        (int `;` 'ztype `;` 'vtype)) <= 5.walk('vtype), "5 ~> vtype"),
  ), testSet("[walk] table test w/ cat3 model", CAT3_MODEL,
    testing(5, walk(int), lst(int `;`) <= 5.walk(int), "5[walk,int]"),
    testing(5, walk('A), lst(int `;` 'A) <= 5.walk('A), "5[walk,A]"),
    testing(5, walk('B), lst(int `;` 'A `;` 'B) <= 5.walk('B), "5[walk,B]"),
    testing(5, walk('C), lst(int `;` 'A `;` 'B `;` 'C) <= 5.walk('C), "5[walk,C]"),
    // testing(int(5,6,7), int.q(3).walk('C), lst(int `;` 'A `;` 'B `;` 'C).q(3) <= int.q(3).walk('C), "[5,6,7] => int{3}[walk,C]"),
    testing(5, walk(str), lst(), "5[walk,str]"),
    IGNORING("eval-5")(5, /*int.split(walk(int).head).merge*/ 5, 5, "5-<:[walk,int][head]:>-"),
    IGNORING("eval-5")(5, int.split(walk('A).head).merge, 'A(5), "5-<:[walk,A][head]:>-"),
    IGNORING("eval-5")(5, int.split(walk('B).head).merge, 'B(5), "5-<:[walk,B][head]:>-"),
    IGNORING("eval-5")(5, int.split(walk('C).head).merge, 'C(5), "5-<:[walk,C][head]:>-"),
    testing(int, __, int, "int"),
    testing('A, __, 'A, "A"),
    testing('B, __, 'B, "B"),
    testing('C, __, 'C, "C"),*/
  )) {
  test("walk play") {
    println(engine.eval("int => [walk,nat]", bindings(storage.model('digraph))))
    //println(int.update(storage.model('digraph)).model.graph.paths(asType(int).rangeObj.hardQ(qOne),'nat))
  }
}


