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
import org.mmadt.language.obj.{Obj, Str}
import org.mmadt.processor.Processor
import org.mmadt.storage.StorageFactory.{str, _}
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class SocialModelTest extends FunSuite {

  val mmsocial: Model = Model.simple()
  val socialmm: Model = Model.simple()
  val msCompiler: Processor = Processor.compiler(mmsocial)
  val smCompiler: Processor = Processor.compiler(socialmm)
  val msIterator: Processor = Processor.iterator(mmsocial)
  val smIterator: Processor = Processor.iterator(socialmm)

  mmsocial.put(int <= int.is(int.gt(0)), int.named("nat"))
  val nat: IntType = mmsocial("nat")
  mmsocial.put(trec[Str, Obj](str("name") -> str, str("age") -> int), trec[Str, Obj](str("name") -> str, str("age") -> nat).named("person"))
  val person: RecType[Str, Obj] = mmsocial("person")
  println("mm=>social\n" + mmsocial)

  socialmm.put(nat, int)
  socialmm.put(person, trec(str("name") -> str, str("age") -> int))
  println("social=>mm\n" + socialmm)

  test("model atomic types") {
    assertResult(int.getClass)(mmsocial.get(tobj("nat")).get.getClass)
    assertResult("nat")(nat.name)
    assertResult(nat(34))(nat(34))
    assertResult("nat")(nat(int(34)).name)
    assertResult("int")(socialmm(nat(34)).name)
    assertResult("int")(socialmm(mmsocial(int(34))).name)
    assertResult(34)(mmsocial(int(34)).g)
    // assertThrows[AssertionError]{mmsocial(int(-34))}
    assertResult("nat[plus,nat]")(nat.plus(nat).toString)
  }

  test("model mapping and inverse mapping") {
    val result = (1 to 100).foldRight(int(40))((_, b) => {
      val result = socialmm(mmsocial(b))
      assertResult("int")(result.name)
      assertResult(40)(result.g)
      assertResult(qOne)(result.asInstanceOf[Obj].q)
      result
    })
    assertResult("int")(result.name)
    assertResult(40)(result.g)
    assertResult(qOne)(result.asInstanceOf[Obj].q)
    //
    assertResult("nat")(mmsocial(result).name)
    assertResult(40)(mmsocial(result).g)
    assertResult(qOne)(mmsocial(result).asInstanceOf[Obj].q)
  }

  /*test("model composite types"){
    // map nat to nat
    val marko:RecValue[StrValue,Value[Obj]] = mmsocial(vrec[StrValue,Value[Obj]](str("name") -> str("marko"),str("age") -> int(29)))
    assertResult("person")(marko.name)
    assertResult("nat")(marko.get(str("age")).name)
    assertResult(29L)(marko.get(str("age")).value)
    assertResult("int")(socialmm(marko.get(str("age"))).name)
    assertResult(29L)(socialmm(marko.get(str("age"))).value)
    // map int to nat
    val ryan:RecValue[StrValue,Value[Obj]] = mmsocial(vrec[StrValue,Value[Obj]](str("name") -> str("ryan"),str("age") -> int(20)))
    assertResult("person")(ryan.name)
    assertResult("nat")(ryan.get(str("age")).name)
    assertResult(20L)(ryan.get(str("age")).value)
    assertResult("int")(socialmm(ryan.get(str("age"))).name)
    assertResult(20L)(socialmm(ryan.get(str("age"))).value)
  }*/

  test("bad model mappings") {
    //assertThrows[AssertionError]{mmsocial(vrec(str("name") -> int(34),str("age") -> int(24)))}
    //assertThrows[AssertionError]{mmsocial(vrec(str("name") -> str("marko")))}
    //    assertThrows[AssertionError]{mmsocial(vrec(str("name") -> str("marko"),str("age") -> int(-120)))}
    //assertThrows[AssertionError]{mmsocial(int(-130))}
  }

  test("model compilation and evaluation") {
    val toSocial = msCompiler(trec[Str, Obj](str("name") -> str, str("age") -> int).get(str("age"), int).plus(int))
    assertResult("nat<=person[get,'age'][plus,nat]")(toSocial.toString)
    assertResult("nat")(toSocial.range.name)
    assertResult("person")(toSocial.domain().name)
    assertResult("int<=rec:['name'->str;'age'->int][get,'age'][plus,int]")(smCompiler(toSocial).toString)
    assertResult(int(40))(smIterator(vrec(str("name") -> str("ryan"), str("age") -> int(20)), smCompiler(toSocial)))
  }

  test("model compilation already in model") {
    assertResult("nat<=person[get,'age'][plus,nat]")(msCompiler(person.get(str("age"), nat).plus(nat)).toString)
    assertResult("int<=rec:['name'->str;'age'->int][get,'age'][plus,int]")(smCompiler(trec[Str, Obj](str("name") -> str, str("age") -> int).get(str("age"), int).plus(int)).toString)
  }

  /* test("model composite strm"){
     val records:RecStrm[Value[Str],Value[Obj]] = vrec(
       rec(str("name") -> str("marko"),str("age") -> int(29)),
       rec(str("name") -> str("kuppitz"),str("age") -> int(28)),
       rec(str("name") -> str("ryan"),str("age") -> int(27)),
       rec(str("name") -> str("stephen"),str("age") -> int(26)))
     val people :RecStrm[Value[Str],Value[Obj]] = mmsocial(records)
     people.value.foreach(x => {
       assertResult("person")(x.name)
       assertResult("str")(x.get(str("name")).name)
       assertResult("nat")(x.get(str("age")).name)
     })
   }*/
}
