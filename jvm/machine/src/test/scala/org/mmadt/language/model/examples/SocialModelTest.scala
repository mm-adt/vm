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

  val mmsocial  :Model     = Model.simple()
  val socialmm  :Model     = Model.simple()
  val msCompiler:Processor = Processor.compiler(mmsocial)
  val smCompiler:Processor = Processor.compiler(socialmm)
  val msIterator:Processor = Processor.iterator(mmsocial)
  val smIterator:Processor = Processor.iterator(socialmm)


  val nat   :IntType          = mmsocial.define(int.named("nat") <= int.is(int.gt(0)))
  val person:RecType[Str,Obj] = mmsocial.define(trec[Str,Obj](str("name") -> str,str("age") -> nat).named("person") <= trec[Str,Obj](str("name") -> str,str("age") -> int))
  println("mm=>social\n" + mmsocial)

  socialmm.define(int <= nat.id())
  socialmm.define(trec(str("name") -> str,str("age") -> int) <= person)
  println("social=>mm\n" + socialmm)

  test("model atomic types"){
    assertResult("nat")(nat.name)
    assertResult(mmsocial(int(34)))(mmsocial(int(34)))
    assertResult("nat")(mmsocial(int(34)).name)
    assertResult("int")(socialmm(int(34)).name)
    assertResult("int")(socialmm(mmsocial(int(34))).name)
    assertResult(34)(socialmm(int(34)).value)
    assertThrows[AssertionError]{mmsocial(int(-34))}
    assertResult("nat[plus,nat]")(nat.plus(nat).toString)
  }

  test("model mapping and inverse mapping"){
    val result = (1 to 100).foldRight(int(40))((_,b) => {
      val result = socialmm(mmsocial(b))
      assertResult("int")(result.name)
      assertResult(40)(result.value)
      assertResult(qOne)(result.q)
      result
    })
    assertResult("int")(result.name)
    assertResult(40)(result.value)
    assertResult(qOne)(result.q)
    //
    assertResult("nat")(mmsocial(result).name)
    assertResult(40)(mmsocial(result).value)
    assertResult(qOne)(mmsocial(result).q)
  }

  test("model composite types"){
    // map nat to nat
    val marko:RecValue[StrValue,Value[Obj]] = mmsocial(vrec(str("name") -> str("marko"),str("age") -> int(29)))
    assertResult("person")(marko.name)
    assertResult("nat")(marko.get(str("age")).name)
    assertResult(29L)(marko.get(str("age")).value)
    assertResult("int")(socialmm(marko.get(str("age"))).name)
    assertResult(29L)(socialmm(marko.get(str("age"))).value)
    // map int to nat
    val ryan:RecValue[StrValue,Value[Obj]] = mmsocial(vrec(str("name") -> str("ryan"),str("age") -> int(20)))
    assertResult("person")(ryan.name)
    assertResult("nat")(ryan.get(str("age")).name)
    assertResult(20L)(ryan.get(str("age")).value)
    assertResult("int")(socialmm(ryan.get(str("age"))).name)
    assertResult(20L)(socialmm(ryan.get(str("age"))).value)
  }

  test("model compilation and evaluation"){
    val toSocial = msCompiler(person.get(str("age"),nat).plus(nat))
    assertResult("nat<=person[get,'age'][plus,nat]")(toSocial.toString)
    assertResult("nat")(toSocial.range.name)
    assertResult("person")(toSocial.domain().name)
    assertResult("int<=rec['name':str,'age':int][get,'age'][plus,int]")(smCompiler(toSocial).toString)
    assertResult(int(40))(smIterator(vrec(str("name") -> str("ryan"),str("age") -> int(20)),smCompiler(toSocial)))
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
