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

import org.mmadt.language.LanguageException
import org.mmadt.language.mmlang.mmlangScriptEngineFactory
import org.mmadt.language.obj.Obj.intToInt
import org.mmadt.language.obj.`type`.__.{a, as, eqs, is}
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.trace.ModelOp
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.language.obj.{Bool, Int}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.processor.inst.trace.DefineInstTest.MODEL
import org.mmadt.storage.StorageFactory._

object DefineInstTest {
  private val natType: Type[Int] = int.named("nat") <= int.is(int.gt(0))
  private val abcType: Type[__] = __("mylist") <= __.-<(is(eqs(1)) `|` (1 `;` __("mylist"))) >-
  private val MODEL: Model = ModelOp.EMPTY.defining(natType).defining(abcType)
  //     testing   ((int(1) `,` (int(1) `,` (int(2) `,` 3))).define(__("abc") <= (__.branch(__.is(__.a(int)) | (int `,` __("abc"))))).a(__("abc")), btrue),
  //    testing   ((int(1) `,` (int(1) `,` (int(2) `,` 3))).define(__("abc") <= (__.branch(__.is(__.lt(2)) | (int `,` __("abc"))))).a(__("abc")), bfalse),

}
class DefineInstTest extends BaseInstTest(
  testSet("[define] table test w/ nat model", MODEL,
    comment("nat"),
    testing(2, a(__("nat")), true),
    testing(-2, a(__("nat")), false),
    testing(-2, int.a(__("nat").plus(100)), false),
    testing(2, as(__("nat")).plus(0), 2.named("nat")),
    // testing(2, as(__("nat")).plus(-10), LanguageException.typingError(-8.named("nat"), __("nat") <= int.is(int.gt(0))), "2[as,nat][plus,-10]"),
    comment("mylist"),
    testing(1 `;` (1 `;` 1), lst.a(__("mylist")), true, "(1;(1;1)) => lst[a,mylist]"),
    testing(1 `,` (1 `,` 2), a(__("mylist")), false),
    testing(1 `,` (2 `,` 1), a(__("mylist")), false),
    testing(1 `,` (1 `,` 2), a(__("mylist")), false),
  )
) {

  test("[define] play tests") {
    println(int.define(int.is(int.gt(0))).a(__("nat")))
    println(int(-10).define(__("nat") <= int.is(int.gt(0))).a(__("nat").plus(100)))
    println(__("nat").plus(100).domain)
    println(int(-10).compute(int.define(__("nat") <= int.is(int.gt(0))).a(__("nat")).asInstanceOf[Type[Bool]]))
    println(int.define(int.plus(10).mult(20)).plus(2) -< (__("x").plus(100) `,` __("x")) >-)
    println(new mmlangScriptEngineFactory().getScriptEngine.eval("1[a,[real|str]]"))
    println(str.a(__.-<(real `|` int) >-)) // TODO
  }
}
