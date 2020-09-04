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
import org.mmadt.language.obj.Obj.{intToInt, stringToStr}
import org.mmadt.language.obj.`type`.__.{symbolToToken, symbolToRichToken,_}
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.trace.ModelOp
import org.mmadt.language.obj.op.trace.ModelOp.{MM, Model}
import org.mmadt.language.obj.{Bool, Int}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, excepting, testSet, testing}
import org.mmadt.processor.inst.trace.DefineInstTest.{MODEL, myListType, natType}
import org.mmadt.storage.StorageFactory._

object DefineInstTest {
  private val natType: Type[Int] = int.named("nat") <= int.is(int.gt(0))
  private val myListType: Type[__] = 'mylist <= __.-<(is(eqs(1)) `|` (1 `;` 'mylist)) >-
  private val iListType: Type[__] = 'ilist <= lst.branch(is(empty) `|` branch(is(head.a(int)) `;` is(tail.a('ilist))))
  private val siListType: Type[__] = 'silist <= lst.branch(is(empty) `|` branch(is(head.a(str)) `;` is(tail.head.a(int)) `;` is(tail.tail.a('silist))))
  private val vecType: Type[__] = 'vec <= __.split(__ `;` lst.combine(__ `,`).merge.count)
  private val MODEL: Model = ModelOp.MM.defining(natType).defining(myListType).defining(iListType).defining(siListType).defining(vecType)
  //     testing   ((int(1) `,` (int(1) `,` (int(2) `,` 3))).define(__("abc") <= (__.branch(__.is(__.a(int)) | (int `,` __("abc"))))).a(__("abc")), btrue),
  //    testing   ((int(1) `,` (int(1) `,` (int(2) `,` 3))).define(__("abc") <= (__.branch(__.is(__.lt(2)) | (int `,` __("abc"))))).a(__("abc")), bfalse),

}
class DefineInstTest extends BaseInstTest(
  testSet("[define] table test w/ nat model", MODEL,
    comment("nat"),
    testing(2, a('nat), true),
    testing(-2, a('nat), false),
    testing(-2, int.a('nat.plus(100)), false),
    testing(2, as('nat).plus(0), 2.named("nat")),
    excepting(2, as('nat).plus(-10), LanguageException.typingError(-8.named("nat"), natType), "2[as,nat][plus,-10]"),
    excepting(2, as('nat).plus(-10).plus(10), LanguageException.typingError(-8.named("nat"), natType), "2[as,nat][plus,-10][plus,10]"),
    comment("mylist"),
    testing(1 `;` (1 `;` 1), lst.a('mylist), true, "(1;(1;1)) => lst[a,mylist]"),
    testing(1 `,` (1 `,` 2), a('mylist), false),
    testing(1 `,` (2 `,` 1), a('mylist), false),
    testing(1 `,` (1 `,` 2), a('mylist), false),
    excepting(1 `;` (1 `;` 1), as('mylist).put(0, 34), LanguageException.typingError('mylist(34 `;` 1 `;` (1 `;` 1)), myListType), "(1;(1;1))[as,mylist][put,0,34]"),
    comment("ilist"),
    testing(lst(), a('ilist), true, "()[a,ilist]"),
    testing(1 `;`, a('ilist), true, "(1)[a,ilist]"),
    testing(1 `;` 2 `;` 3, a('ilist), true, "(1;2;3)[a,ilist]"),
    testing(1 `;` "a" `;` 1, lst.a('ilist), false, "(1;'a';1) => lst[a,ilist]"),
    comment("silist"),
    testing(lst(), a('silist), true, "()[a,silist]"),
    testing("a" `;` 1, a('silist), true, "('a';1)[a,silist]"),
    testing("a" `;` 1 `;` "b" `;` 2, a('silist), true, "('a';1;'b';2)[a,silist]"),
    testing(1 `;` "a" `;` 1, lst.a('silist), false, "(1;'a';1) => lst[a,silist]"),
    testing("a" `;` 1 `;` 2, lst.a('silist), false, "('a';1;2) => lst[a,silist]"),
    comment("vec"),
    testing(lst(), a('vec), true, "()[a,vec]"),
    testing(lst(), as('vec), (lst() `;` 0).named("vec"), "()[as,vec]"),
    testing(1 `;` 2, as('vec), (((1 `;` 2) `;`) `;` 2).named("vec"), "(1;2)[as,vec]"),
  ), testSet("[define] table test w/ mm", MM,
    comment("midway-define]"),
    testing(2, define('x <= int.plus(1)), 2, "2[define,x<=int+1]"),
    testing(2, define('x <= int.plus(1)).plus('x), 5, "2[define,x<=int+1][plus,x]"),
    testing(2, define('x <= int.plus(1)).plus('x), 5, "2[define,x<=int+1][plus,x]"),
    // testing(2, define('x <= int.plus(1)).as('x), 'x(3), "2[define,x<=int+1][as,x]"),
  )
) {
  println('silist <= lst.branch(is(empty) `|` branch(is(head.a(str)) `;` is(tail.head.a(int)) `;` is(tail.tail.a('silist)))))
  test("[define] play tests") {
    println(int.define(int.is(int.gt(0))).a('nat))
    println(int(-10).define('nat <= int.is(int.gt(0))).a('nat.plus(100)))
    println('nat.plus(100).domain)
    println(int(-10).compute(int.define('nat <= int.is(int.gt(0))).a('nat).asInstanceOf[Type[Bool]]))
    println(int.define(int.plus(10).mult(20)).plus(2) -< ('x.plus(100) `,` 'x) >-)
    println(new mmlangScriptEngineFactory().getScriptEngine.eval("1[a,[real|str]]"))
    println(str.a(__.-<(real `|` int) >-)) // TODO
  }
}
