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
import org.mmadt.language.mmlang.mmlangScriptEngineFactory
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.{Bool, Obj}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}

class DefineInstTest extends FunSuite with TableDrivenPropertyChecks {

  test("[define] value, type, strm, anon combinations") {
    val starts: TableFor2[Obj, Obj] =
      new TableFor2[Obj, Obj](("query", "result"),
        (int(2).define(__("nat") <= int.is(int.gt(0))).a(__("nat")), btrue),
        (int(-2).define(__("nat") <= int.is(int.gt(0))).a(__("nat")), bfalse),
        (int(-2).define(__("nat") <= int.is(int.gt(0))).a(__("nat").plus(100)), bfalse),

        (int(2).define(__("abc") <= int.is(int.gt(0))).a(__("abc")), btrue),
        (int(-2).define(__("abc") <= int.is(int.gt(0))).a(__("abc")), bfalse),
      /*  ((int(1) `,` (int(1) `,` 1)).define(__("abc") <= (__.-<(__.is(__.eqs(1)) | (int(1) `,` __("abc"))) >-)).a(__("abc")), btrue),
        ((int(1) `,` (int(1) `,` 2)).define(__("abc") <= (__.-<(__.is(__.eqs(1)) | (int(1) `,` __("abc"))) >-)).a(__("abc")), bfalse),
        ((int(1) `,` (int(2) `,` 1)).define(__("abc") <= (__.-<(__.is(__.eqs(1)) | (int(1) `,` __("abc"))) >-)).a(__("abc")), bfalse),
        ((int(1) `,` (int(1) `,` 2)).define(__("abc") <= (__.-<(__.is(__.a(int)) | (int(1) `,` __("abc"))) >-)).a(__("abc")), btrue),
        ((int(1) `,` (int(1) `,` 2)).define(__("abc") <= (__.`[`(__.is(__.a(int)) | (int `,` __("abc"))) `]`)).a(__("abc")), btrue),
        ((int(1) `,` (int(1) `,` (int(2) `,` 3))).define(__("abc") <= (__.`[`(__.is(__.a(int)) | (int `,` __("abc"))) `]`)).a(__("abc")), btrue),
        ((int(1) `,` (int(1) `,` (int(2) `,` 3))).define(__("abc") <= (__.`[`(__.is(__.lt(2)) | (int `,` __("abc"))) `]`)).a(__("abc")), bfalse),
        ((int(1) `,` (int(1) `,` (int(2) `,` 3))).define(__("abc") <= (__.`[`(__.is(__.lt(5)) | (int `,` __("abc"))) `]`)).a(__("abc")), btrue)*/
      )
    forEvery(starts) { (query, result) => {
      assertResult(result)(new mmlangScriptEngineFactory().getScriptEngine.eval(s"${query}"))
      assertResult(result)(query)
    }
    }
  }

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
