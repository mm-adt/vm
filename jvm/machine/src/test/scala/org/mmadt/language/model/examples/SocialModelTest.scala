/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 *  mm-ADT is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU Affero General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 *  License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 *  You can be released from the requirements of the license by purchasing a
 *  commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language.model.examples

import org.mmadt.language.model.Model
import org.mmadt.language.obj.`type`.{IntType, RecType}
import org.mmadt.language.obj.value.{RecValue, StrValue, Value}
import org.mmadt.language.obj.{Obj, Str}
import org.mmadt.processor.Processor
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class SocialModelTest extends FunSuite {

  val social   :Model     = Model.simple()
  val functor  :Model     = Model.simple()
  val scompiler:Processor = Processor.compiler(social)
  val fcompiler:Processor = Processor.compiler(functor)

  // define model types
  val nat   :IntType          = social.define("nat")(int <= int.is(int.gt(0)))
  val person:RecType[Str,Obj] = social.define("person")(trec[Str,Obj](str("name") -> str,str("age") -> nat).id())
  social.define("int")(int <= nat.id())
  println(social + "\n---")

  functor.define("nat")(int <= nat.id())
  functor.define("person")(trec(str("name") -> str,str("age") -> int) <= person.id())
  println(functor)

  test("model types"){
    assertResult(social(nat)(34))(social(nat)(34))
    assertResult("nat")(social(nat)(34).name)
    assertResult("int")(social(int)(social(nat)(34)).name)
    assertResult(34)(social(nat)(34).value)
    assertThrows[AssertionError]{social(nat)(-34)}
    assertResult("nat[plus,nat]")(nat.plus(nat).toString)

    // map nat to nat
    val marko:RecValue[StrValue,Value[Obj]] = social(person)(Map(str("name") -> str("marko"),str("age") -> social(nat)(29)))
    assertResult("person")(marko.name)
    assertResult("nat")(marko.get(str("age")).name)
    assertResult(29L)(marko.get(str("age")).value)
    assertResult("int")(social(int)(marko.get(str("age"))).name)
    assertResult(29L)(social(int)(marko.get(str("age"))).value)
    // map int to nat
    val ryan:RecValue[StrValue,Value[Obj]] = social(person)(Map(str("name") -> str("ryan"),str("age") -> int(20)))
    assertResult("person")(ryan.name)
    assertResult("nat")(ryan.get(str("age")).name)
    assertResult(20L)(ryan.get(str("age")).value)
    assertResult("int")(social(int)(ryan.get(str("age"))).name)
    assertResult(20L)(social(int)(ryan.get(str("age"))).value)
  }

  test("model values"){
    val endo = scompiler(person.get(str("age"),int).plus(int))
    assertResult("nat<=person[get,'age'][plus,nat]")(endo.toString)
    assertResult("nat")(endo.range.name)
    assertResult("person")(endo.domain().name)
    assertResult(social(nat)(40))(Processor.iterator(social).apply(vrec(str("name") -> str("ryan"),str("age") -> int(20)),endo))
    assertResult("int<=rec['name':str,'age':nat][get,'age'][plus,int]")(fcompiler(endo).toString)
  }

  test("rec stream w/ rewrites"){
    val ppl = vrec(
      rec(str("name") -> str("marko")),
      rec(str("name") -> str("kuppitz")),
      rec(str("name") -> str("ryan")),
      rec(str("name") -> str("stephen")))
    println(ppl)
  }

}
