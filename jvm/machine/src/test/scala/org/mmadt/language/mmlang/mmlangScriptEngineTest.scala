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

package org.mmadt.language.mmlang

import javax.script.ScriptContext
import org.mmadt.language.LanguageException.typingError
import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.obj.Obj.{booleanToBool, intToInt, stringToStr, tupleToRecYES}
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.op.map.{MultOp, PlusOp}
import org.mmadt.language.obj.op.sideeffect.LoadOp
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.{LanguageException, LanguageFactory, Tokens}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmlangScriptEngineTest extends FunSuite {

  lazy val engine:mmADTScriptEngine = LanguageFactory.getLanguage("mmlang").getEngine.get()

  test("play2") {
    println(engine.eval(":[model,mm]"))
  println(engine.eval("('plus',1)[as,inst]"))
    println(engine.eval(
      """:[model,mm]
        |[define,node:('name'->str)]
        |[define,edge:('dom'->node,'cod'->node)]
        |[define,edge<=(_;_)-<('dom'->.0,'cod'->.1)]""".stripMargin))
    println(engine.eval("(('name'->'marko');('name'->'jen')) => edge"))
    engine.eval(":{1}")
  }

  test("range<=domain") {
    println(engine.eval("lst"))
    println(lst)
    assertResult(LanguageException.typingError(int.q(5), int.q(3)))(intercept[LanguageException](engine.eval("[4{2},5{1},6{2}] => int{6}<=int{3}[[plus,1],[plus,1]]")))
    assertResult(int(5.q(4), 6.q(-2), 7.q(4)))(engine.eval("[4{2},5{-1},6{2}] => int{6}<=int{3}[[plus,1],[plus,1]]"))
    assertResult(13.q(8))(engine.eval("10 int[plus,1]{2}[plus,2]{4}"))
    assertResult(int.plus(2).plus(3))(engine.eval("1 => [plus,2][plus,3][type]"))
    assertResult(int(10).q(30))(engine.eval("5{2} => 10{15}"))
    assertResult("nat")(engine.eval("nat").toString)
    // assertResult(labelNotFound(int.is(bool <= int.gt(0)), "nat"))(intercept[LanguageException](engine.eval("nat<=int[is>0]")))
    assertResult("nat<=int[is,bool<=int[gt,0]]")(engine.eval("nat<=int[is>0]").toString)
    engine.eval(":[model,('type'->(nat->(nat<=int[is>0]))) <= mm]")
    engine.eval("5 => nat")
    assertResult(13.q(8))(engine.eval("10 => int[plus,1]{2}[plus,2]{4}"))
    assertResult(13.q(8).named("nat"))(engine.eval("10 => nat[plus,1]{2}[plus,2]{4}"))
    assertResult(13.q(80).named("nat"))(engine.eval("10{10} => nat{10}[plus,1]{2}[plus,2]{4}"))
    assertResult(13.q(80))(engine.eval("10{10} => int{80}<=nat{10}[plus,1]{2}[plus,2]{4}"))
    assertResult(int)(engine.eval("1 => [type]"))
    // assertResult(__("nat") <= int.is(bool <= int.gt(0)))(engine.eval("1 => nat[type]"))
    // assertResult(int.named("nat"))(engine.eval("1 => nat[type]").domain)
    assertResult(__("nat"))(engine.eval("1 => nat[type]").range)
    assertResult(int.plus(2).plus(3))(engine.eval("1 => [plus,2][plus,3][type]"))
    //    assertResult("int[plus,nat<=int[is,bool<=int[gt,0]][plus,2]]")(engine.eval("int[plus,nat[plus,2]]").toString)
    assertResult("nat<=int[is,bool<=int[gt,0]]")(engine.eval("nat<=int[is>0]").toString)
    assertResult("nat{?}<=nat[is,bool<=nat[gt,0]]")(engine.eval("nat<=nat[is>0]").toString)
    //    assertResult(int.plus(__("nat") <= int.is(bool <= int.gt(0)).plus(2)))(engine.eval("int[plus,nat[plus,2]]"))
    assertResult("nat")(engine.eval("nat").toString)
    assertResult(5.named("nat"))(engine.eval("5 => nat"))
    assertResult(5.named("nat"))(engine.eval("5 => nat[id]"))
    assertResult(int(5))(engine.eval("5 => int<=nat[id]"))
    assertResult(int(5))(engine.eval("5 => int<=nat"))
    assertResult(zeroObj)(engine.eval("5 => nat{0}"))
    assertResult(2.named("nat"))(engine.eval("1 => nat[plus,1]"))
    assertResult(4.named("nat"))(engine.eval("1 => nat[plus,nat[plus,2]]"))
    assertResult(int(4))(engine.eval("1 => int[plus,nat[plus,2]]"))
    assertResult(1.named("nat"))(engine.eval("0 => nat<=int[plus,1]"))

    // assertResult(int(-1))(engine.eval("1 => int<=nat[plus,-2]"))
    // assertResult(labelNotFound(rec(str("name") -> str("marko")), "person"))(intercept[LanguageException](engine.eval("('name'->'marko') => person")))
    // assertResult(labelNotFound(rec(str("name") -> str("marko")), "person"))(intercept[LanguageException](engine.eval("('name'->'marko') => person.name")))
    //assertResult(typingError(int(-1), __("nat") <= int.is(bool <= int.gt(0))))(intercept[LanguageException](engine.eval("-1 => int[plus,nat[plus,2]]")))
    assertResult(typingError(int(-1), __("nat") <= int.is(bool <= int.gt(0))))(intercept[LanguageException](engine.eval("-1 => int<=nat[plus,2]")))
    assertResult(typingError(int(0), __("nat") <= int.is(bool <= int.gt(0))))(intercept[LanguageException](engine.eval("0 => nat[plus,1]")))
    assertResult(typingError(int(0), __("nat") <= int.is(bool <= int.gt(0))))(intercept[LanguageException](engine.eval("0 => nat[plus,0]")))
    assertResult(typingError(int(0), __("nat") <= int.is(bool <= int.gt(0))))(intercept[LanguageException](engine.eval("0 => nat")))
    assertResult(typingError(int.q(2), int.q(10)))(intercept[LanguageException](engine.eval("66{2} => int{10}")))
    assertResult(typingError(bool.q(10), int.q(10)))(intercept[LanguageException](engine.eval("true{10} => int{10}")))
    assertResult(LanguageException.typingError(int(3), real))(intercept[LanguageException](engine.eval("3[plus,42.5]")))
    assertResult(LanguageException.typingError(int(3), real))(intercept[LanguageException](engine.eval("3[mult,42.5]")))
    // assertResult(LanguageException.typingError(int, bool))(intercept[LanguageException](engine.eval("bool<=int")))
    engine.eval(":{1}")
    // assertResult(LanguageException.typingError(int, bool))(intercept[LanguageException](engine.eval("bool<=int")))
  }

  test("empty space parsing") {
    assert(!engine.eval("").alive)
    assert(!engine.eval("    ").alive)
    assert(!engine.eval("  \n  ").alive)
    assert(!engine.eval("\t  \n  ").alive)

    println(engine.eval("(1,1,2)"))
    //  engine.eval("[true,6,7.8,'ryan']-<([type][zero],[zero])")
  }

  test("canonical type parsing") {
    assertResult(bool)(engine.eval("bool"))
    assertResult(int)(engine.eval("int"))
    assertResult(real)(engine.eval("real"))
    assertResult(str)(engine.eval("str"))
    assertResult(lst)(engine.eval("lst"))
    assertResult(rec)(engine.eval("rec"))
    assertResult(__)(engine.eval("_"))
  }

  test("quantified canonical type parsing") {
    assertResult(bool.q(int(2)))(engine.eval("bool{2}"))
    assertResult(int.q(int(0), int(1)))(engine.eval("int{?}"))
    assertResult(real.q(int(0), int(0)))(engine.eval("real{0}"))
    assertResult(rec.q(int(5), int(10)))(engine.eval("rec{5,10}"))
    assertResult(lst.q(int(5), int(10)))(engine.eval("lst{5,10}"))
    assertResult(str.q(+))(engine.eval("str{+}"))
    assertResult(__.q(2))(engine.eval("_{2}"))
    assertResult(__.q(+))(engine.eval("_{+}"))
    assertResult(__.q(2).id)(engine.eval("_{2}[id]"))
    assertResult(zeroObj)(engine.eval("{0}"))
    assertResult(oneObj)(engine.eval("{1}"))
    assertResult(zeroObj)(engine.eval("1=>{0}"))
  }

  test("atomic value parsing") {
    assertResult(btrue)(engine.eval("true"))
    assertResult(bfalse)(engine.eval("false"))
    assertResult(int(5))(engine.eval("5"))
    assertResult(int(-51))(engine.eval("-51"))
    assertResult(real(5.0))(engine.eval("5.0"))
    assertResult(real(0.0))(engine.eval("0.0"))
    assertResult(real(1.0))(engine.eval("1.0"))
    assertResult(real(-51.2))(engine.eval("-51.2"))
    assertResult(real(0.21))(engine.eval("0.21"))
    assertResult(str("marko"))(engine.eval("'marko'"))
    assertResult(str("marko comp3 45AHA\"\"\\'-%^&"))(engine.eval("'marko comp3 45AHA\"\"\\'-%^&'"))
  }

  test("strm parsing") {
    assertResult(int(1, 2, 3))(engine.eval("[1,2,3]"))
    assertResult(str(str("a").q(3), str("b")))(engine.eval("['a'{3},'b']"))
    assertResult(strm)(engine.eval("[]"))
    assertResult(strm)(engine.eval("_{0}"))
    assertResult(bool(btrue, bfalse, bfalse, bfalse))(engine.eval("[false{3},true]"))
    assertResult("_{0}")(engine.eval("[6{0}]").toString)
  }

  test("atomic named value parsing") {
    assertResult(bool(name = "keep", g = true))(engine.eval("keep:true"))
    assertResult(int(name = "nat", g = 5))(engine.eval("nat:5"))
    assertResult(int(name = "score", g = -51))(engine.eval("score:-51"))
    assertResult(str(name = "fname", g = "marko"))(engine.eval("fname:'marko'"))
    assertResult(str(name = "garbage", g = "marko comp3 45AHA\"\"\\'-%^&"))(engine.eval("garbage:'marko comp3 45AHA\"\"\\'-%^&'"))
  }

  test("rec value parsing") {
    assertResult(rec(str("name") -> str("marko")))(engine.eval("('name'->'marko')"))
    assertResult(rec(str("name") -> str("marko"), str("age") -> int(29)))(engine.eval("('name'->'marko','age'->29)"))
    assertResult(rec(str("name") -> str("marko"), str("age") -> int(29)))(engine.eval("('name'->  'marko' , 'age' ->29)"))
    assertResult(str("marko"))(engine.eval("('name'->'marko','age'->29)[head]"))
    assertResult(rec(str("age") -> int(29)))(engine.eval("('name'->'marko','age'->29)[tail]"))
    assertResult(str("name") -> str("marko") `_;` str("age") -> int(29))(engine.eval("('a'->23;'name'->'marko';'age'->29)[tail]"))
  }

  test("rec type parsing") {
    assertResult(rec(str("name") -> str, str("age") -> int))(engine.eval("(   'name'   ->str ,   'age' ->int)"))
    assertResult(rec(str("name") -> str, str("age") -> int))(engine.eval("('name'->str,'age'->int)"))
    assertResult(rec(str("name") -> str, str("age") -> int).q(30))(engine.eval("('name'->str,'age'->int){30}"))
    assertResult(rec(str("name") -> str, str("age") -> int).q(30).get("age", int).gt(30))(engine.eval("('name'->str,'age'->int){30}[get,'age',int][gt,30]"))
    assertResult(rec(str("name") -> str, str("age") -> int).q(30).get("age", int).gt(30))(engine.eval("bool{30}<=('name'->str,'age'->int){30}[get,'age',int][gt,30]"))
    assertResult(bool.q(*) <= rec(str("name") -> str, str("age") -> int).q(*).get("age", int).gt(30))(engine.eval("bool{*}<=('name'->str,'age'->int){*}[get,'age',int][gt,30]"))
  }

  test("rec named value parsing") {
    assertResult(rec(str("name") -> str("marko"), str("age") -> int(29)).named("person"))(engine.eval("person:(   'name'   ->'marko' ,   'age' ->29)"))
    assertResult(rec(str("name") -> str("marko"), str("age") -> int(29)).named("person"))(engine.eval("person:('name'->'marko','age'->29)"))
  }


  test("rec get/put") {
    assertResult(bfalse)(engine.eval("(1->'a',2->'b')==(2->'b',1->'a')"))
    val person:Rec[Obj, Obj] = rec(g = (Tokens.`|`, List(str("name") -> str, str("age") -> int)))
    //    assertResult(str <= person.get("name"))(engine.eval("('name'->str|'age'->int)[get,'name']"))
    //    assertResult(int <= person.get("age"))(engine.eval("('name'->str|'age'->int)[get,'age']"))
    //    assertResult(str <= person.get("name"))(engine.eval("str<=('name'->str|'age'->int)[get,'name']"))
    //    assertResult(int <= person.get("age"))(engine.eval("int<=('name'->str|'age'->int)[get,'age']"))
    assertResult(int <= rec[Str, Int].put(str("age"), int).get(str("age")))(engine.eval("rec[put,'age',int][get,'age']"))
    assertResult(int <= rec[Str, Int].put(str("age"), int).get(str("age")).plus(int(10)))(engine.eval("rec[put,'age',int][get,'age'][plus,10]"))
    assertResult(int <= rec[Str, Int].put(str("age"), int).get(str("age")).plus(int(10)))(engine.eval("int<=rec[put,'age',int][get,'age'][plus,10]"))
    assertResult(int(20))(engine.eval("('name'->'marko') rec[put,'age',10][get,'age'][plus,10]"))
    assertResult(int(20))(engine.eval("('name'->'marko')[put,'age',10][get,'age'][plus,10]"))
    assertResult(int(20))(engine.eval("('name'->'marko') => int<=rec[put,'age',10][get,'age'][plus,10]"))
    // assertResult(int(4, 6))(engine.eval("(1 -> 2 , 3 -> 4,5 -> 6)[get,is>1]"))
    // assertResult(int(6))(engine.eval("(1 -> 2 , 3 -> 4,5 -> 6)[get,is>3]"))
    // assertResult(int(2, 4, 6))(engine.eval("(1 -> 2 , 3 -> 4,5 -> 6)[get,is<100]"))
    // assertResult(int(2, 4, 6))(engine.eval("(1 -> 2 , 3 -> 4,5 -> 6)[get,int]"))
    // assertThrows[LanguageException] {
    assertResult(zeroObj)(engine.eval("(1 -> 2 , 3 -> 4,5 -> 6)[get,str]"))
  }

  test("quantified value parsing") {
    assertResult(btrue.q(int(2)))(engine.eval("true{2}"))
    assertResult(bfalse)(engine.eval("false{1}"))
    assertResult(int(5).q(+))(engine.eval("5{+}"))
    assertResult(int(6).q(qZero))(engine.eval("6{0}"))
    assertResult(real(7.563).q(qZero))(engine.eval("7.563{0,0}"))
    assertResult(str("marko").q(int(10), int(100)))(engine.eval("'marko'{10,100}"))
    assertResult(int(20).q(int(10)))(engine.eval("13{10}[plus,7]"))
    assertResult(int(13).q(int(10)))(engine.eval("13{10}[is>5]"))
  }

  test("anonymous type") {
    assertResult(__.plus(1).mult(2))(engine.eval("[plus,1][mult,2]"))
    assertResult(__.plus(1).mult(__.plus(10)))(engine.eval("[plus,1][mult,[plus,10]]"))
    assertResult(__.plus(1.2).mult(__.plus(10.1)))(engine.eval("[plus,1.2][mult,[plus,10.1]]"))
    assertResult(int(75))(engine.eval("4[plus,1][mult,[plus,10]]"))
    assertResult(__.q(qZero))(engine.eval("_{0}"))
    assertResult(__)(engine.eval("_{1}"))
    assertResult(int(10))(engine.eval("int[_{0}|10]"))
    assertResult(int(5))(engine.eval("5[_{0}|_{1}]"))
    assertResult(zeroObj)(engine.eval("5-<(_{0};_{1})>-"))
    assertResult(int(5))(engine.eval("5[_{0},_{1}]"))
  }

  test("quantifier inst parsing") {
    assertResult(int.id.q(2).id.q(4))(engine.eval("int[id]{2}[id]{4}"))
    assertResult("int{8}<=int[id]{2}[id]{4}")(engine.eval("int[id]{2}[id]{4}").toString)
    assertResult(int(10).q(8))(engine.eval("10 int[id]{2}[id]{4}"))
    assertResult(int(10).q(8))(engine.eval("10[id]{2}[id]{4}"))
    assertResult("int{8}<=int[plus,10]{2}[id]{4}")(engine.eval("int[plus,10]{2}[id]{4}").toString)
    assertResult(int(15).q(8))(engine.eval("5[plus,10]{2}[id]{4}"))
    assertResult(int(17).q(8))(engine.eval("5[plus,10]{2}[id]{4}[plus,2]"))
    assertResult(int(17).q(16))(engine.eval("5{2}[plus,10]{2}[id]{4}[plus,2]"))
    assertResult(int.q(0, 24))(engine.eval("int[plus,10]{2}[id]{4}[is,[gt,2]]{3}").asInstanceOf[IntType].range)
    assertResult(int(15).q(16))(engine.eval("5{2}[plus,10]{2}[id]{4}"))
    assertResult(int.q(2) ==> (int.q(0, 48) <= int.q(2).plus(10).q(2).id.q(4).is(bool.q(16) <= int.gt(2)).q(3)))(engine.eval("int{2}[plus,10]{2}[id]{4}[is,[gt,2]]{3}"))
    assertResult(int.q(2) ==> (int.q(0, 48) <= int.q(2).plus(10).q(2).id.q(4).is(int.q(16).gt(2)).q(3)))(engine.eval("int{2}[plus,10]{2}[id]{4}[is,[gt,2]]{3}"))
    assertResult(int.q(2) ==> (int.q(0, 48) <= int.q(2).plus(10).q(2).id.q(4).is(bool.q(16) <= int.q(16).gt(2)).q(3)))(engine.eval("int{2}[plus,10]{2}[id]{4}[is,[gt,2]]{3}"))
    assertResult(int.q(0, 48) <= int.q(2).plus(10).q(2).id.q(4).is(int.q(16).gt(2)).q(3))(engine.eval("int{2}[plus,10]{2}[id]{4}[is,[gt,2]]{3}"))
    assertResult(int.q(0, 48) <= int.q(2).plus(10).q(2).id.q(4).is(bool.q(16) <= int.q(16).gt(2)).q(3))(engine.eval("int{2}[plus,10]{2}[id]{4}[is,[gt,2]]{3}"))
    assertResult(int(15).q(48))(engine.eval("5{2}[plus,10]{2}[id]{4}[is,[gt,2]]{3}"))
    assertResult(__.q(64) <= __.plus(2).q(2).mult(3).q(32).plus(4))(engine.eval("[plus,2]{2}[mult,3]{32}[plus,4]"))
  }

  test("refinement type parsing") {
    assertResult(int.q(?) <= int.is(int.gt(int(10))))(engine.eval("int[is,int[gt,10]]"))
    // assertResult(int <= int.is(int.gt(int(10))))(engine.eval("int<=int[is,int[gt,10]]")) //TODO: when a range is specified by the user, use that during compilation
    assertResult(int.q(?) <= int.is(int.gt(int(10))))(engine.eval("int[is>10]"))
  }

  test("empty result sets") {
    assertResult(zeroObj)(engine.eval("1-<([id]{1},[id]{-1})>-"))
    assertResult(zeroObj)(engine.eval("1[[id]{1},[id]{-1}]"))
    assertResult(zeroObj)(engine.eval("_{0}"))
    assertResult(zeroObj)(engine.eval("int{0}"))
    assertResult(zeroObj)(engine.eval("1[plus,1]{0}"))
    assertResult(zeroObj)(engine.eval("int[plus,1]{0}"))
    assertResult(int.q(0))(engine.eval("int[plus,1]{0}"))
    assertResult(str.q(0))(engine.eval("int[plus,1]{0}"))
    assertResult(int(1).q(0))(engine.eval("int[plus,1]{0}"))
    assertResult(int.q(0))(engine.eval("1{0}"))
    assertResult(int.q(0))(engine.eval("'hello'{0}"))
    assertResult(int.q(0))(engine.eval("(1,1{-1})>-[id]"))
    assertResult(str.q(0))(engine.eval("(1,1{-1})>-[id]"))
    assertResult(str.q(0))(engine.eval("(10,35,1{-1})>-[id]{0}"))
    assertResult(str.q(0))(engine.eval("(10,35,1{-1})>-[id]{0}[plus,2]"))
  }

  test("as instruction parsing") {
    assertResult(int(1))(engine.eval("1[as,int]"))
    assertResult(str("1"))(engine.eval("1[as,str]"))
    assertResult(int(14))(engine.eval("'1'[plus,'4'][as,int]"))
    assertResult(int(16))(engine.eval("'1'[plus,'4'][as,int[plus,2]]"))
    assertResult(int(16))(engine.eval("'1'[plus,'4'][as,int][plus,2]"))
    assertResult(str("14"))(engine.eval("5[plus,2][mult,2][as,str]"))
    assertResult(str("14hello"))(engine.eval("5 int[plus,2][mult,2][as,str][plus,'hello']"))
    assertResult(str("14hello"))(engine.eval("5[plus,2][mult,2][as,str][plus,'hello']"))
    // assertResult(rec(str("x") -> int(7)))(engine.eval("5 int[plus,2][as,rec:('x'->int)]"))
    // assertResult(rec(str("x") -> int(7), str("y") -> int(10)))(engine.eval("5 int[plus 2]<x>[plus 3]<y>[as,rec:('x'-><.x>,'y'-><.y>)]"))
    // assertResult(rec(str("x") -> int(7), str("y") -> int(10)))(engine.eval("5 int[plus 2]<x>[plus 3]<y>[as,rec:('x'->int<.x>,'y'->int<.y>)]"))
    // assertResult(int(10))(engine.eval("5 int[plus 2]<x>[plus 3]<y>[as,rec:('a'->int<.x>,'b'->int<.y>)][get,'b']"))
    // assertResult(int(10))(engine.eval("5 int[plus 2]<x>[plus 3]<y>[as,rec:['a'-><.x>,'b'-><.y>]][get,'b']"))
    // assertResult(int ==> int.to("x").plus(1).to("y").as(rec(str("a") -> int.from("x"), str("b") -> int.from("y"))).get("b"))(engine.eval("int<x>[plus,1]<y>[as,rec:('a'->int<.x>,'b'->int<.y>)].b"))
    // assertResult(rec(str("x") -> int(7), str("y") -> int(10), str("z") -> rec(str("a") -> int(17))))(engine.eval("5 int[plus 2]<x>[plus 3]<y>[as,rec:('x'->int<.x>,'y'->int<.y>,'z'->[as,rec:('a'-><.x> + <.y>)])]"))
  }

  test("start instruction initial") {
    //assertResult(int(1, 2, 3))(engine.eval("[start,[1,2,3]]"))
    assertResult(int)(engine.eval("[start,int]"))
    assertResult(int)(engine.eval("int{20}[start,int]"))
    //assertResult(int)(engine.eval("2[start,int]"))
    //assertResult(str.plus("a"))(engine.eval("2[start,str[plus,'a']]"))
    //assertResult(int)(engine.eval("'a'{4}[start,int]"))
    assertResult(str.plus("a"))(engine.eval("_{0}[start,str[plus,'a']]"))
    assertResult(int(1) `,` 2 `,` 3)(engine.eval("_{0}[start,(1,2,3)]"))

  }

  test("a instruction parsing") {
    assertResult(btrue)(engine.eval("1[a,int]"))
    assertResult(btrue)(engine.eval("1[a,1]"))
    assertResult(bfalse)(engine.eval("1[a,7]"))
    assertResult(btrue)(engine.eval("1.2[a,real]"))
    assertResult(bfalse)(engine.eval("'1'[a,int]"))
    assertResult(int(1))(engine.eval("1?int"))
    assertResult(int(1))(engine.eval("1 ?int"))
    assertResult(int(1))(engine.eval("1[is,[a,int]]"))
    assertResult(btrue)(engine.eval("1[a,[int|str]]"))
    assertResult(bfalse)(engine.eval("1[a,[int[is<0]|str]]"))
    assertResult(btrue)(engine.eval("1[a,[int[is>0]|str]]"))

  }

  test("prefix model") {
    println(engine.eval(":[model,numbers:('type'->(nat -> (nat<=int[is>0]))) <= mm]"))
    println(engine.eval("53 => int").model)
    println(engine.eval("53[as,nat]"))
    println(engine.eval(":{1}"))
  }

  test("explain instruction parsing") {
    assert(engine.eval("int{3}[is>50 -> +10 | is<5 -> +20 | _ -> +30][explain]").toString.contains("[is,bool{3}<=int{3}[lt,5]]"))
    assert(engine.eval("int[define,nat<=int[is>0]]<x>[plus,[mult,x]][[is,[a,nat]][plus,10]|[define,nonat<=int[plus,0]]]<y>[plus,x][explain]").toString.contains("nat->nat<=int[is,bool<=int[gt,0]] x->int nonat->nonat<=int[plus,0]"))
    assert(engine.eval("int{3}[+1,+2,+3][explain]").toString.contains("[int{3}[plus,1],int{3}[plus,2],int{3}[pl..."))
    assert(engine.eval("int[plus,int[mult,6]][explain]").toString.contains("inst"))
    assert(engine.eval("int[plus,[plus,2][mult,7]]<x>[mult,[plus,5]<y>[mult,[plus,<y>]]][is,[gt,<x>]<z>[id]][plus,5][explain]").toString.contains("z->bool"))
    /*assertResult(str("\n" +
      "int<x>[plus,int<y>[plus,int<z>[plus,x][plus,y][plus,z]][plus,y]][plus,x]\n\n" +
      "inst                                           domain      range state\n" +
      "-----------------------------------------------------------------------\n" +
      "[plus,int<y>[plus,int<z>[plus,x][plus,y]...    int    =>   int    x->int \n" +
      " [plus,int<z>[plus,x][plus,y]...                int   =>    int   x->int y->int \n" +
      "  [plus,x]                                       int  =>     int  x->int y->int z->int \n" +
      "  [plus,y]                                       int  =>     int  x->int y->int z->int \n" +
      "  [plus,z]                                       int  =>     int  x->int y->int z->int \n" +
      " [plus,y]                                       int   =>    int   x->int y->int \n" +
      "[plus,x]                                        int    =>   int    x->int \n"))(
      str(engine.eval("int<x>[plus,int<y>[plus,int<z>[plus,x][plus,y][plus,z]][plus,y]][plus,x][explain]").asInstanceOf[StrValue].g))*/
  }

  test("[path] access parsing") {
    println(engine.eval("5[plus,7][mult,4][plus,11][path].1"))
    println(engine.eval("4[+1,+2][type]"))
    assertResult(int(5))(engine.eval("5[plus,1][mult,2][path].0"))
    assertResult(PlusOp(1))(engine.eval("5[plus,1][mult,2][path].1"))
    assertResult(int(6))(engine.eval("5[plus,1][mult,2][path].2"))
    assertResult(MultOp(2))(engine.eval("5[plus,1][mult,2][path].3"))
    assertResult(int(12))(engine.eval("5[plus,1][mult,2][path].4"))
  }

  test("map instruction parsing") {
    assertResult(int.to("x").map(int.from('x).plus(int.from('x))))(engine.eval("int<x>[map,<.x>+<.x>]"))
    assertResult(int(10))(engine.eval("5<x>[map,<.x>+<.x>]"))
    assertResult(int(11))(engine.eval("5<x>[plus,1]<y>[map,<.x>+<.y>]"))
  }

  test("split/merge/branch quantification parsing") {
    assertResult(int.q(8))(engine.eval("[split,(int,int)][merge]{4}").range)
    assertResult(int(5).q(8))(engine.eval("5[split,(int,int)][merge]{4}"))
    assertResult(int(5).q(720))(engine.eval("5{2}[split,(+{10}0,+{20}0)]{3}[merge]{4}"))
    assertResult(int.q(8))(engine.eval("_-<(int,int)>-{4}").range)
    assertResult(int(5).q(8))(engine.eval("5-<(int,int)>-{4}"))
    assertResult(int.q(80))(engine.eval("_-<(int,int){10}>-{4}").range)
    assertResult(int(5).q(80))(engine.eval("5-<(int,int){10}>-{4}"))
    assertResult(int.q(80))(engine.eval("[split,(int,int)]{10}[merge]{4}").range)
    assertResult(int(5).q(80))(engine.eval("5=>[split,(int,int)]{10}[merge]{4}"))
    assertResult(int.q(8))(engine.eval("int[int,int]{4}").range)
    assertResult(int(5).q(8))(engine.eval("5[int,int]{4}"))
    assertResult(int.id.q(8))(engine.eval("int[branch,(int,int)]{4}"))
    assertResult(int.id.q(8))(engine.eval("int[branch,(_,_)]{4}"))
    //
    /* TODO: think about ambient semantics around ;
       assertResult(int.q(-1))(engine.eval("[int{-1};int]").range)
        assertResult(int.id.q(-1))(engine.eval("[int;int[id]{-1}]"))
        assertResult(int.id.q(5))(engine.eval("[int[id]{-5};int[id]{-1};int{1}]"))
        assertResult(int(5).q(-1))(engine.eval("[1{-1};5]"))
        assertResult(int(5))(engine.eval("[1{-1};5{-1}]"))*/
  }

  test("choice with given") {
    assertResult(int -< (int.plus(1) | int.plus(2)))(engine.eval("int-<(+1|+2)"))
    assertResult(int(6) | zeroObj)(engine.eval("5-<(+1|+2)"))
    assertResult("(6->15)")(engine.eval("5-<(int+1[is>0] -> +10 | str -> +'a')").toString)
    assertResult("'aa'")(engine.eval("'a'-<(int+1[is>0] -> +10 | str -> +'a')>-").toString)
    assertResult("100")(engine.eval("5-<(int+1[is>0] -> +10 ; int -> 100)>-").toString)
    assertResult("(6->15)")(engine.eval("5-<(int+1[is>0] -> +10 | int -> 100)").toString)
    assertResult("15")(engine.eval("5-<(int+1[is>0] -> +10 | int -> 100)>-").toString)
    assertResult(btrue)(engine.eval("  5 [plus,2][[is>5]->true|[is==1]->[plus 2]|int->20]"))
    assertResult(int(3))(engine.eval("-1[plus,2]-<(int[is>5]->34|int[is==1]->int[plus2]|int->20)>-"))
    assertResult(int(3))(engine.eval("-1 int[plus,2]-<(int[is>5]->34|int[is==1]->int[plus2]|int->20)>-"))
    assertResult(btrue)(engine.eval("  5 [plus,2][[is>5]->true|[is==1]->[plus 2]|int->20]"))
    assertResult(btrue)(engine.eval("  5 [plus,2]-<([is>5]->true|[is==1]->[plus 2]|int->20)>-"))
    assertResult(int(3))(engine.eval("-1[plus,2]-<(int[is>5]->34|int[is==1]->int[plus2]|int->20)>-"))
    assertResult(int(3))(engine.eval("-1 int[plus,2]-<(int[is>5]->34|int[is==1]->int[plus2]|int->20)>-"))
    assertResult(int(20))(engine.eval("1 [plus,2]-<([is>5]->true|[is==1]->[plus 2]|int->20)>-"))
    assertResult(int(20))(engine.eval("1 [plus,2][split,([is>5]->true|[is==1]->[plus 2]|int->20)]>-"))
    assertResult(__)(engine.eval("int[plus,2]-<(int[is>5]->true|[is==1]->[plus2]|int->20)>-").range)
    assertResult(bfalse)(engine.eval("4[plus,1]-<([is>5] -> true | int -> false)>-"))
    assertResult(bfalse.q(3))(engine.eval("(4,2,1)>-[plus,1]-<([is>5] -> true | int -> false)>-"))
    assertResult(bool(btrue, bfalse.q(2)))(engine.eval("(5,2,1)>-[plus,1][int[is>5] -> true | int -> false]"))
    assertResult(bool(btrue, bfalse.q(2)))(engine.eval("(5,2,1)>-[plus,1]-<(int[is>5] -> true | int -> false)>-"))
    assertResult(bool(btrue.q(2), bfalse))(engine.eval("(5,2,1)>-[plus,1]-<([is<5] -> true | int -> false)>-"))
    assertResult(bool(btrue.q(2), bfalse))(engine.eval("(5,2,1)>-[plus,1][[is<5] -> true | int -> false]"))
    assertResult(bool(btrue.q(2), bfalse))(engine.eval("(5,2,1)>-[is>0][plus,1][[is<5] -> true | int -> false]"))
    assertResult(bool(btrue.q(2), bfalse))(engine.eval("(5,2,1)>-[plus,1][is>0][mult,1][[is<5] -> true | int -> false]"))
    assertResult(bool(bfalse.q(3), btrue.q(4)))(engine.eval("(4,2,1,10,10,10{2})>-[plus,1]-<([is>5] -> true | int -> false)>-"))
    assertResult(btrue)(engine.eval("5[plus,1]-<([is>5] -> true | int -> false)>-"))
    assertResult(btrue)(engine.eval("true-<(bool -> bool | int -> int)>-"))
    assertResult(int(10))(engine.eval("10-<(bool{?} -> bool | int -> int)>-"))
    assertResult(int(10))(engine.eval("10-<(bool{?} -> true | int -> int)>-"))
    assertResult(int(11))(engine.eval("10-<(bool{?} -> true | int -> int[plus,1])>-"))
    assertResult(int(11, 51, 61))(engine.eval("(10,50,60)>--<(bool{?} -> true | int -> int[plus,1])>-"))
    assertResult(int(11, 51, 61))(engine.eval("(10,50,60)>-[bool{?} -> true | int -> int[plus,1]]"))
    assertResult(int(11, 51, 51, 61))(engine.eval("(10,50{2},60)>--<(bool{?} -> true , int -> int[plus,1])>-"))
    assertResult(int(11, 51, 51, 61))(engine.eval("(10,50{2},60)>-[bool{?} -> true | int -> int[plus,1]]"))
    assertResult(int(11).q(2))(engine.eval("(10,10)>--<(bool{?} -> true | int -> int[plus,1])>-"))
    assertResult(int(11).q(2))(engine.eval("10{2}-<(bool{?} -> true | int -> int[plus,1])>-"))
    assertResult(int(302, 42))(engine.eval(
      """ (0,1,2,3)>-
        | [plus,1][is>2]-<(
        |     int[is>3] -> int[mult,10]
        |   | int  -> int[mult,100])>-[plus,2]""".stripMargin))
    assertResult(bfalse)(engine.eval("4[plus,1]-<([is>5] -> true | int -> false)>-"))
    assertResult(btrue)(engine.eval("5[plus,1]-<([is>5] -> true | int -> false)>-"))
  }

  test("choice with rec") {
    assertResult(str("a"))(engine.eval("['a'{-1}]{-1}"))
    assertResult(str("b"))(engine.eval("['a'{0},'b']"))
    assertResult(str("b".q(6), "c".q(8)))(engine.eval("'a'{2}['b'{3},'c'{4}]"))
    assertResult("15")(engine.eval("5 int[int+1[is>0] -> +10 | str -> +'a']").toString)
    assertResult("'aa'")(engine.eval("'a'[int{?} -> +10 | str -> +'a']").toString)
    assertResult("'aa'")(engine.eval("'a'[int+1[is>0] -> +10 | str -> +'a']").toString)
    assertResult("100")(engine.eval("5[int+1[is>0] -> +10 ; int -> 100]").toString)
    assertResult("15")(engine.eval("5[int+1[is>0] -> +10 | int -> 100]").toString)
    assertResult(btrue)(engine.eval("  5 [plus,2][[is>5]->true|[is==1]->[plus 277]|int->20]"))
    assertResult(int(3))(engine.eval("-1[plus,2]-<(int[is>5]->34|int[is==1]->int[plus2]|int->20)>-"))
    assertResult(int(3))(engine.eval("-1 int[plus,2]-<(int[is>5]->34|int[is==1]->int[plus2]|int->20)>-"))
    assertResult(btrue)(engine.eval("  5 [plus,2][[is>5]->true|[is==1]->[plus 2]|int->20]"))
    assertResult(btrue)(engine.eval("  5 [plus,2]-<([is>5]->true|[is==1]->[plus 2]|int->20)>-"))
    assertResult(int(3))(engine.eval("-1[plus,2]-<(int[is>5]->34|int[is==1]->int[plus2]|int->20)>-"))
    assertResult(int(3))(engine.eval("-1 int[plus,2]-<(int[is>5]->34|int[is==1]->int[plus2]|int->20)>-"))
    assertResult(int(20))(engine.eval("1 [plus,2]-<([is>5]->true|[is==1]->[plus 2]|int->20)>-"))
    assertResult(int(20))(engine.eval("1 [plus,2][split,([is>5]->true|[is==1]->[plus 2]|int->20)]>-"))
    // TODO assertResult(obj.q(0, 2))(engine.eval("int[plus,2][int[is>5]->true,[is==1]->[plus2][is,int]]").range)
    assertResult(__.q(?))(engine.eval("int[plus,2][int[is>5]->true|[is==1]->[plus2][is,int]]").range)
    assertResult(__)(engine.eval("int[plus,2][int[is>5]->true|[is==1]->[plus2]|int->20]").range)
    assertResult(bfalse)(engine.eval("4[plus,1]-<([is>5] -> true | int -> false)>-"))
    assertResult(bfalse.q(3))(engine.eval("(4,2,1)>-[plus,1]-<([is>5] -> true | int -> false)>-"))
    assertResult(bool(btrue, bfalse.q(2)))(engine.eval("(5,2,1)>-[plus,1][int[is>5] -> true | int -> false]"))
    assertResult(bool(btrue, bfalse.q(2)))(engine.eval("(5,2,1)>-[plus,1]-<(int[is>5] -> true | int -> false)>-"))
    assertResult(bool(btrue.q(2), bfalse))(engine.eval("(5,2,1)>-[plus,1]-<([is<5] -> true | int -> false)>-"))
    assertResult(bool(btrue.q(2), bfalse))(engine.eval("(5,2,1)>-[plus,1][[is<5] -> true | int -> false]"))
    assertResult(bool(btrue.q(2), bfalse))(engine.eval("(5,2,1)>-[is>0][plus,1][[is<5] -> true | int -> false]"))
    assertResult(bool(btrue.q(2), bfalse))(engine.eval("(5,2,1)>-[plus,1][is>0][mult,1][[is<5] -> true | int -> false]"))
    assertResult(bool(bfalse.q(3), btrue.q(4)))(engine.eval("(4,2,1,10,10,10{2})>-[plus,1]-<([is>5] -> true | int -> false)>-"))
    assertResult(btrue)(engine.eval("5[plus,1]-<([is>5] -> true | int -> false)>-"))
    assertResult(btrue)(engine.eval("true-<(bool -> bool | int -> int)>-"))
    assertResult(int(10))(engine.eval("10-<(bool{?} -> bool | int -> int)>-"))
    assertResult(int(10))(engine.eval("10-<(bool{?} -> true | int -> int)>-"))
    assertResult(int(11))(engine.eval("10-<(bool{?} -> true | int -> int[plus,1])>-"))
    assertResult(int(11, 51, 61))(engine.eval("(10,50,60)>--<(bool{?} -> true | int -> int[plus,1])>-"))
    assertResult(int(11, 51, 61))(engine.eval("(10,50,60)>--<(bool{?} -> true | int -> int[plus,1])>-"))
    assertResult(int(11, 51, 51, 61))(engine.eval("(10,50{2},60)>--<(bool{?} -> true , int -> int[plus,1])>-"))
    assertResult(int(11, 51, 51, 61))(engine.eval("(10,50{2},60)>--<(bool{?} -> true | int -> int[plus,1])>-"))
    assertResult(int(11).q(2))(engine.eval("(10,10)>--<(bool{?} -> true | int -> int[plus,1])>-"))
    assertResult(int(11).q(2))(engine.eval("10{2}-<(bool{?} -> true | int -> int[plus,1])>-"))
    assertResult(int(302, 42))(engine.eval(
      """ (0,1,2,3)>-
        | [plus,1][is>2]-<(
        |     int[is>3] -> int[mult,10]
        |   | int  -> int[mult,100])>-[plus,2]""".stripMargin))
    assertResult(bfalse)(engine.eval("4[plus,1]-<([is>5] -> true | int -> false)>-"))
    assertResult(btrue)(engine.eval("5[plus,1]-<([is>5] -> true | int -> false)>-"))
  }

  test("parallel with rec types") {
    assertResult(zeroObj)(engine.eval("true-<(bool -> bool ; int -> int)>-"))
    assertResult(zeroObj)(engine.eval("10-<(bool -> bool ; int -> int)>-"))
    assertResult(int(10))(engine.eval("10-<(bool{?} -> true | int -> int)>-"))
    assertResult(int(13))(engine.eval("10-<(int{?} -> int[plus,2] ; _ -> int[plus,1])>-"))
    assertResult(int(11, 51, 61))(engine.eval("(10,50,60)>--<(int ->int ; _ -> int[plus,1])>-"))
    assertResult(zeroObj)(engine.eval("(10,50,60)>--<(bool{?} -> true ; int -> int[plus,1])>-"))
    assertResult(int(11, 51, 51, 61))(engine.eval("(10,50{2},60)>--<(int -> int ; int+1 -> int[plus,1])>-"))
    assertResult(zeroObj)(engine.eval("(10,10)>--<(bool{?} -> true ; int -> int[plus,1])>-"))
    assertResult(int(11).q(2))(engine.eval("10{2}-<(int+1 ->int ; int -> int[plus,1])>-"))
    assertResult(int(302, 42))(engine.eval(
      """ (0,1,2,3)>-
        | [plus,1][is>2]-<(
        |     int[is>3] -> int[mult,10]
        |   | is<4      -> int[mult,100])>-[plus,2]""".stripMargin))
    assertResult(int(302, 42))(engine.eval(
      """ (0,1,2,3)>-
        | [plus,1][is>2][
        |     int[is>3] -> int[mult,10]
        |   | is<4      -> int[mult,100]][plus,2]""".stripMargin))
    assertResult(int(11, 22))(engine.eval("['a','b'][?'a' -> 11 | ?'b' -> 22]"))
    assertResult(int(11, 22))(engine.eval("('a','b')>-[?'a' -> 11 | ?'b' -> 22]"))
    assertResult(zeroObj)(engine.eval("('a','b')[?'a' -> 11 | ?'b' -> 22]"))
    assertResult(bfalse)(engine.eval("4[plus,1]-<([is>5] -> true | int -> false)>-"))
    assertResult(bool(btrue, bfalse))(engine.eval("4[plus,56]-<([is>5] -> true , int -> false)>-"))
    assertResult(btrue)(engine.eval("5[plus,1]-<([is>5] -> true | int+1 -> false)>-"))
    assertResult(bfalse)(engine.eval("4[plus,1][[is>5] -> true | int -> false]"))
    assertResult(btrue)(engine.eval("5[plus,1][[is>5] -> true | int+3 -> false]"))
  }

  test("to/from state parsing") {
    assertResult(real(45.5))(engine.eval("45.0<x>[mult,0.0][plus,<.x>][plus,0.5]"))
    assertResult(int.to("a").plus(int(10)).to("b").mult(int(20)))(engine.eval("int<a>[plus,10]<b>[mult,20]"))
    assertResult(int.to("a").plus(int(10)).to("b").mult(int.from("a")))(engine.eval("int<a>[plus,10]<b>[mult,<.a>]"))
    assertResult(int.to("a").plus(int(10)).to("b").mult(int.from("a")))(engine.eval("int<a>[plus,10]<b>[mult,int<.a>]"))
    assertResult(int.to("x").plus(int(10)).to("y").mult('x))(engine.eval("int<x>[plus,10]<y>[mult,x]"))
    assertResult(int(600))(engine.eval("19[plus,1]<x>[plus,10][mult,x]"))
    assertResult(int(21))(engine.eval("5[plus,2]<x>[mult,2][plus,<.x>]"))
    assertResult(int(21))(engine.eval("5[plus,2]<x>[mult,2][plus,int<.x>]"))
    assertResult("int[plus,2]<x>[mult,2]<y>[plus,int<.x>[plus,int<.y>]]")(engine.eval("int[plus,2]<x>[mult,2]<y>[plus,<.x>[plus,<.y>]]").toString)
    assertResult(int(35))(engine.eval("5[plus,2]<x>[mult,2]<y>[plus,int<.x>[plus,<.y>]]"))
    assertResult(int(13))(engine.eval("5 => int<x>[plus,1][plus,x[plus,2]]"))
    assertResult(int(14))(engine.eval("5 => int<x>[plus,1][plus,<x>[plus,2]]"))
    assertResult(int(19))(engine.eval("5 => int<x>[plus,1][plus,<x>[plus,2]][plus,x]"))
    assertResult(int(28))(engine.eval("5 => int<x>[plus,1][plus,<x>[plus,2]][plus,<x>]"))
    assertResult(int(28).q(3))(engine.eval("[5,5,5] => int{3}<x>[plus,1][plus,<x>[plus,2]][plus,<x>]"))
    assertResult(int(28, 32, 36))(engine.eval("[5,6,7] => int{3}<x>[plus,1][plus,<x>[plus,2]][plus,<x>]"))
    assertThrows[LanguageException] {
      engine.eval("50[is>dog]")
    }
  }

  test("infix operator instruction parsing") {
    assertResult(real.plus(6.0))(real.plus(6.0))
    assertResult(int.plus(int(6)))(engine.eval("int+6"))
    assertResult(real.plus(real(6.0)))(engine.eval("real + 6.0"))
    assertResult(int.plus(int(6)).gt(int(10)))(engine.eval("int+6>10"))
    assertResult(int.plus(int(6)).lt(int(10)))(engine.eval("int+6<10"))
    assertResult(int.plus(int(6)).lte(int(10)))(engine.eval("int+6=<10"))
    assertResult(int.plus(int(6)).gte(int(10)))(engine.eval("int+6>=10"))
    assertResult(int.plus(int(1)).mult(int(2)).gt(int(10)))(engine.eval("int+1*2>10"))
    assertResult(str.plus(str("hello")))(engine.eval("str+'hello'"))
    assertResult(int.is(int.gt(int(5))))(engine.eval("int[is,int[gt,5]]"))
    assertResult(int.is(int.gt(int(5))))(engine.eval("int[is>5]"))
    assertResult(int.is(int.gt(int(5))))(engine.eval("int[is > 5]"))
    assertResult(int.is(int.lt(int(5))))(engine.eval("int[is < 5]"))
    assertResult(int.is(int.lt(int(5))))(engine.eval("int[is[lt,5]]"))
    assertResult(int.is(int.lte(int(5))))(engine.eval("int[is =< 5]"))
    assertResult(int.is(int.lte(int(5))))(engine.eval("int[is,[lte,5]]"))
    assertResult(int.is(int.gte(int(5))))(engine.eval("int[is >= 5]"))
    assertResult(int.is(int.gte(int(5))))(engine.eval("int[is[gte,5]]"))
    assertResult(int(6))(engine.eval("6[is,int[gt,5]]"))
    assertResult(int(6))(engine.eval("6[is>5]"))
    assertResult(int(6))(engine.eval("6[is > 5]"))
    assertResult(zeroObj)(engine.eval("6[is < 5]"))
    assertResult(int(4))(engine.eval("4[is[lt,5]]"))
    assertResult(int(5))(engine.eval("5[is =< 5]"))
    assertResult(zeroObj)(engine.eval("6[is,[lte,5]]"))
    assertResult(int(6))(engine.eval("6[is >= 5]"))
    assertResult(int(5))(engine.eval("5[is[gte,5]]"))
  }

  test("get dot-notation parsing") {
    assertResult(__.get(str("a")))(engine.eval(".a"))
    assertResult(__.get(str("a")).get(str("b")))(engine.eval(".a.b"))
    assertResult(__.get(str("a")).get(str("b")).get(str("c")))(engine.eval(".a.b.c"))
    assertResult(int(4))(engine.eval(
      """
        |('a'->
        |  ('aa'->1,
        |   'ab'->2),
        | 'b'->
        |   ('ba'->3,
        |    'bb'->
        |      ('bba'->4))).b.bb.bba""".stripMargin))
    assertResult(int(0))(engine.eval("('a'->('b'->('c'->('d'->0)))).a.b.c.d"))
    // assertResult(int(4, 12))(engine.eval("2[plus,2]<x>[mult,3]<y>[as,('a'->int<.x>,'b'->int<.y>)]-<([id]->.a,[is,true]->.b)>-"))
  }

  test("rec poly") {
    assertResult(int(14))(engine.eval("4-<(str{?}->'x'|int->+10)>-"))
    assertResult(int(2, 14))(engine.eval("4-<(int[is>0]->2,int->+10)>-"))
    ///
    assertResult(str.plus("a"))(engine.eval("str[[plus,'a'],[plus,'b']{0},[plus,'c']{0}]"))
    assertResult(str.branch(str.plus("a") `,` str.plus("c")))(engine.eval("str[[plus,'a'],[plus,'b']{0},[plus,'c']]"))
    assertResult(str.plus("a"))(engine.eval("str[str -> [plus,'a'],int -> [plus,'b'], int -> [plus,'c']]"))
    assertResult(str.branch(rec(str.is(str.gt("b")) -> str.plus("a"))))(engine.eval("str[str[is>'b'] -> [plus,'a'],int -> [plus,'b'], int -> [plus,'c']]"))
    assertResult(str.branch(str -> str.plus("a") `_,` str -> str.plus("c")))(engine.eval("str[str -> [plus,'a'],int -> [plus,'b'], str -> [plus,'c']]"))
  }

  test("bool strm input parsing") {
    assertResult(btrue)(engine.eval("(true,false)>-[is,[id]]"))
    assertResult(btrue)(engine.eval("(true,false)>-[is,[id]]"))
    assertResult(btrue)(engine.eval("[true,false] => bool{*}[is,[id]]"))
    assertResult(btrue)(engine.eval("[true,false][is,[id]]"))
  }

  test("int strm input parsing") {
    assertResult(int(-1, 0))(engine.eval("[0,1] => int{+}[plus,-1]"))
    assertResult(int(-1, 0))(engine.eval("[0,1] => int{+}[plus,-1]"))
    assertResult(int(1, 2, 3))(engine.eval("[0,1,2][plus,1]"))
    assertResult(int(int(1).q(3), int(2).q(10), int(3)))(engine.eval("[0{3},1{10},2][plus,1]"))
    assertResult(int(30, 40))(engine.eval("(0,1,2,3)>-[plus,1][is,int[gt,2]][mult,10]"))
    assertResult(int(300, 40))(engine.eval("(0,1,2,3)>-[plus,1][is,int[gt,2]][int[is,int[gt,3]] -> int[mult,10] | int -> int[mult,100]]"))
    assertResult(int(300, 40))(engine.eval("(0,1,2,3)>-[plus,1][is,int[gt,2]]-<(int[is,int[gt,3]][mult,10] | int[mult,100])>-"))
    assertResult(int(300, 40))(engine.eval("(0,1,2,3)>-[plus,1][is,int[gt,2]]-<(int[is,int[gt,3]] -> int[mult,10] | int -> int[mult,100])>-"))
    assertResult(int(30, 40))(engine.eval("(0,1,2,3)>-[plus,1][is,int[gt,2]][mult,10]"))
  }

  test("real strm input parsing") {
    assertResult(real(-1.2, 0.0))(engine.eval("[0.0,1.2] => real{+}[plus,-1.2]"))
    assertResult(zeroObj)(engine.eval("real{5}[is,false]"))
  }

  test("str strm input parsing") {
    assertResult(str("marko"))(engine.eval("""('m','a','r','k','o')>-[fold,x.0+x.1]"""))
    assertResult(str("marko"))(engine.eval("""('m','a','r','k','o')>-[id][fold,[zero],x.0+x.1]"""))
    assertResult(str("dr. marko"))(engine.eval("""('m','a','r','k','o')>-[fold,[zero]+'dr. ',x.0+x.1]"""))
    assertResult(str("marko"))(engine.eval("""['m','a','r','k','o'][fold,'',x.0[plus,x.1]]"""))
    assertResult(str("marko"))(engine.eval("""['m','a','r','k','o'][fold,'',x.0+x.1]"""))
  }

  test("int count parsing") {
    assertResult(int(6))(engine.eval("""(1,2,3)>-[sum]"""))
    assertResult(int(33))(engine.eval("""(1,2,3{10})>-[sum]"""))
    assertResult(int(66))(engine.eval("""[1,2,3{10}][plus,int][sum]"""))
  }

  test("rec strm input parsing") {
    //  assertResult(rec(rec(str("a") -> int(1), str("b") -> int(0)), rec(str("a") -> int(2), str("b") -> int(0))))(engine.eval("""[('a'->1),('a'->2)][plus,('b'->0)]"""))
  }

  test("anonymous expression parsing") {
    assertResult(int.is(int.gt(int.id)))(engine.eval("int[is,[gt,[id]]]"))
    assertResult(real.is(real.gt(real.id)))(engine.eval("real[is,[gt,[id]]]"))
    assertResult(int.plus(int(1)).plus(int.plus(int(5))))(engine.eval("int[plus,1][plus,[plus,5]]"))
    assertResult(int.plus(int(1)).is(int.gt(int(5))))(engine.eval("int[plus,1][is,[gt,5]]"))
    assertResult(int.q(?) <= int.is(int.gt(int(5))))(engine.eval("int[is,[gt,5]]"))
    assertResult(int.q(?) <= int.is(int.gt(int.mult(int.plus(int(5))))))(engine.eval("int[is,[gt,[mult,[plus,5]]]]"))
    assertResult(int.q(?) <= int.is(int.gt(int.mult(int.plus(int(5))))))(engine.eval("int[is,int[gt,int[mult,int[plus,5]]]]"))
    assertResult(engine.eval("int[is,int[gt,int[mult,int[plus,5]]]]"))(engine.eval("int[is,[gt,[mult,[plus,5]]]]"))
    assertResult(int.split(int.is(int.gt(int(5))) -> int(1) | int -> int(2)))(engine.eval(" int-<([is>5] -> 1 | int -> 2)"))
    assertResult(int.plus(int(10)).split((int.is(int.gt(int(10))) -> int.gt(int(20)).asInstanceOf[Obj]) `_|` int -> int.plus(int(10))))(engine.eval(" int[plus,10]-<([is,[gt,10]]->[gt,20] | int->[plus,10])"))
    assertResult(int.plus(int(10)).split(int.is(int.gt(int(5))) -> int(1) | int -> int(2)))(engine.eval(" int[plus,10]-<([is>5] -> 1 | int -> 2)"))
    assertResult(int(302, 42))(engine.eval(
      """ (0,1,2,3)>-
        | [plus,1][is>2]
        |   [[is>3] -> [mult,10]
        |   | int   -> [mult,100]][plus,2]""".stripMargin))
    assertResult(bfalse)(engine.eval("4[plus,1][[is>5] -> true | int -> false]"))
    assertResult(btrue)(engine.eval("5[plus,1]-<([is>5] -> true | int -> false)>-"))
    assertResult(btrue)(engine.eval("true[bool -> bool | int -> int]"))
    assertResult(int(10))(engine.eval("10[bool{?} -> bool | int -> int]"))
    assertResult(int(10))(engine.eval("10[bool{?} -> true | int -> int]"))
    assertResult(int(11))(engine.eval("10[bool{?} -> true | int -> int[plus,1]]"))
  }

  test("expression parsing") {
    assertResult(btrue)(engine.eval("true bool[is,bool]"))
    assertResult(int(7))(engine.eval("5[plus,2]"))
    assertResult(int(70))(engine.eval("10[plus,int[mult,6]]"))
    assertResult(int(55))(engine.eval("5[plus,int[mult,int[plus,5]]]"))
    assertResult(bfalse)(engine.eval("0 int+1*2>10"))
    assertResult(str("marko rodriguez"))(engine.eval("'marko' str[plus,' '][plus,'rodriguez']"))
    assertResult(int(10))(engine.eval("10 int[is,bool<=int[gt,5]]"))
    assertResult(int.q(?) <= int.plus(int(10)).is(int.gt(int(5))))(engine.eval(" int[plus,10][is,bool<=int[gt,5]]"))
    assertResult(int.q(?) <= int.plus(int(10)).is(int.gt(int(5))))(engine.eval("int[plus,10][is,int[gt,5]]"))
    assertResult(int.q(0, 3) <= int.q(3).plus(int(10)).is(int.q(3).gt(int(5))))(engine.eval("int{3}[plus,10][is,int[gt,5]]"))
  }

  test("language error message") {
    assert(intercept[LanguageException] {
      engine.eval("1<x>[plus,2]<y>-<")
    }.getMessage.contains("lus,2]<y>-<\n          ^ near here"))
    assert(intercept[LanguageException] {
      engine.eval("!")
    }.getMessage.contains("!\n^ near here"))
    assert(intercept[LanguageException] {
      engine.eval("[[")
    }.getMessage.contains("[[\n^ near here"))
    assert(intercept[LanguageException] {
      engine.eval("int[[")
    }.getMessage.contains("int[[\n   ^ near here"))
  }

  test("reducing expressions") {
    assertResult(int(7))(engine.eval("[5{7}][plus,2][count]"))
    assertResult(int(7))(engine.eval("(5,6,7,8,9,0,1)>-[count]"))
    assertResult(int(5))(engine.eval("[1,3,7,2,1][plus,2][count]"))
    assertResult(int(6))(engine.eval("[1,3,7,2,1,10][plus,2][count]"))
    assertResult(int(2))(engine.eval("[1,3,7,2,1,10][plus,2][is>5][count]"))
    assertResult(int(3))(engine.eval("[1.0,3.1,7.2,2.5,1.1,10.1]+2.0[is>5.0][count]"))
    assertResult(int(3))(engine.eval("(1.0,3.1,7.2,2.5,1.1,10.1)>-+2.0[is>5.0][count]"))
  }

  test("logical expressions") {
    assertResult(btrue)(engine.eval("true[and,true]"))
    // TODO:    assertResult(bfalse.q(3))(engine.eval("true{3} => bool{2,7}[and,true][or,false]"))
    /*    assertResult(bfalse.q(3))(engine.eval("true{3} => bool{3}[and,true][or,false]"))
        assertResult(bfalse.q(3))(engine.eval("true{3}[and,true][or,false]"))
        assertResult(btrue.q(3))(engine.eval("true{3}[and,true][or,[and,bool]]"))
        assertResult(bfalse.q(3, 30))(engine.eval("true{3,30}[and,false][or,[and,bool]]"))
        assertResult(bfalse.q(3, 30))(engine.eval("true{3,30}[and,false][and,[or,bool]]"))*/
  }

  test("lst play") {
    println(engine.eval("int<x>[plus,1][plus,x][path]"))
    println(engine.eval("1<x>[plus,1][plus,x][path]"))
    assertResult(int(3))(engine.eval("1<x>[plus,1][plus,x]"))
  }

  test("lst variables") {
    assertResult(int(3))(engine.eval("1<x>[plus,1][plus,x]"))
    assertResult(int(5, 23))(engine.eval("1-<([plus,1]<x>,[plus,10]<x>)>-[plus,1][plus,x]"))
    assertResult(int(3, 1))(engine.eval("1<x>[plus,2]-<(<.x>[plus,2],<.x>)>-"))
    assertResult(int(3, 1))(engine.eval("1<x>[plus,2]-<(int<.x>[plus,2],int<.x>)>-"))
    assertResult(int(3, 1))(engine.eval("1<x>[plus,2]-<(x[plus,2],x)>-"))
    assertResult(int(3, 1))(engine.eval("1<x><y>[plus,2]-<(y[plus,2],x)>-"))
    assertResult(int(3))(engine.eval("1<x>[plus,2]-<(x[plus,2]|x)>-"))
    //assertResult(zeroObj | int(3) | zeroObj)(engine.eval("1<x>[plus,2]-<(x[is>100]|x[plus,2]|x)"))
    assertThrows[LanguageException] {
      engine.eval("1[plus,1][plus,x]")
    }
  }

  test("lst values w/ [mult], [plus], and [zero]") {
    assertResult(lst)(engine.eval("()"))
    assertResult(int(1) `,`)(engine.eval("(1)"))
    assertResult("('a';'a')")(engine.eval("'a'-<(_;_)").toString)
    assertResult("('b','a')")(engine.eval("('a';'b')-<(.1,.0)").toString)
    //assertResult("('aZ';'bz')")(engine.eval("('a';'b')<w>-<(.1+'z'<x>;<.w>.0+'Z'<y>)>--<(y;x)").toString) // TODO
    assertResult(zeroObj |)(engine.eval("(|)")) // TODO: this might be bad
    assertResult(zeroObj | zeroObj)(engine.eval("(|)"))
    assertResult("a" | "b" | zeroObj)(engine.eval("('a'|'b'|)"))
    assertResult("a" | zeroObj | zeroObj)(engine.eval("('a'||)"))
    assertResult(zeroObj | "b" | zeroObj)(engine.eval("(|'b'|)"))
    assertResult(zeroObj | zeroObj | zeroObj)(engine.eval("(||)"))
    assertResult(zeroObj | zeroObj | "c")(engine.eval("(||'c')"))
    assertResult(zeroObj | zeroObj | "c" | zeroObj)(engine.eval("(||'c'|)"))
    // mult
    assertResult("('a';'b';'c';'d')")(engine.eval("('a';'b')(str;str;str;str)<=(str;str)[mult,('c';'d')]").toString)
    //assertResult("(('a';'b';'c')|('a';'b';'d'))")(engine.eval("('a';'b')[mult,('c'|'d')]").toString)
    //assertResult("(('a';'b';'c')|('a';'b';'d'))")(engine.eval("('a';'b')*('c'|'d')").toString)
    //assertResult("(('a'|'c')|('a'|'d')|('b'|'c')|('b'|'d'))")(engine.eval("('a'|'b')[mult,('c'|'d')]").toString) // ac+ad + bc+bd
    //assertResult("(('a'|'c')|('a'|'d')|('b'|'c')|('b'|'d'))")(engine.eval("('a'|'b')*('c'|'d')").toString)
    //assertResult("(('a';'c';'d')|('b';'c';'d'))")(engine.eval("('a'|'b')[mult,('c';'d')]").toString) // (a*c*d)+(b*c*d)
    //assertResult("(('a';'c';'d')|('b';'c';'d'))")(engine.eval("('a'|'b')*('c';'d')").toString)
    /////////////
    assertResult("(('a';'b';'c'),('a';'b';'d'))")(engine.eval("('a';'b')[mult,('c','d')]").toString)
    assertResult("(('a';'b';'c'),('a';'b';'d'))")(engine.eval("('a';'b')*('c','d')").toString)
    assertResult("(('a','c'),('a','d'),('b','c'),('b','d'))")(engine.eval("('a','b')[mult,('c','d')]").toString) // ac+ad + bc+bd
    assertResult("(('a','c'),('a','d'),('b','c'),('b','d'))")(engine.eval("('a','b')*('c','d')").toString)
    assertResult("(('a';'c';'d'),('b';'c';'d'))")(engine.eval("('a','b')[mult,('c';'d')]").toString) // (a*c*d)+(b*c*d)
    assertResult("(('a';'c';'d'),('b';'c';'d'))")(engine.eval("('a','b')*('c';'d')").toString)
    // plus
    //assertResult("('a'|'b'|'c'|'d')")(engine.eval("('a'|'b')[plus,('c'|'d')]").toString)
    assertResult("(('a';'b'),('c';'d'))")(engine.eval("('a';'b')[plus,('c';'d')]").toString)
    //assertResult("(('a';'b')|('c'|'d'))")(engine.eval("('a';'b')[plus,('c'|'d')]").toString)
    //assertResult("(('a'|'b')|('c';'d'))")(engine.eval("('a'|'b')[plus,('c';'d')]").toString)
    //assertResult("('a'|'b')")(engine.eval("('a'|'b')[plus,[zero]]").toString)
    /////////////
    assertResult("('a','b','c','d')")(engine.eval("('a','b')[plus,('c','d')]").toString)
    assertResult("(('a';'b'),('c';'d'))")(engine.eval("('a';'b')[plus,('c';'d')]").toString)
    assertResult("(('a';'b'),('c','d'))")(engine.eval("('a';'b')[plus,('c','d')]").toString)
    assertResult("(('a','b'),('c';'d'))")(engine.eval("('a','b')[plus,('c';'d')]").toString)
    //assertResult("('a'|'b')")(engine.eval("('a'|'b')[plus,[zero]]").toString) // TODO: early optimization okay?
    //    assertResult("(('a';'b'),( ))")(engine.eval("('a';'b')[plus,[zero]]").toString) // TODO: type on zero
    // assertResult("('a','b')")(engine.eval("('a','b')[plus,[zero]]").toString) // TODO: early optimization okay?
    // mult w; types
    //assertResult("[int[plus,2][plus,5][id]]<=[int;[plus,2]][mult,[[plus,5];[id]]]")(engine.eval("[int;[plus,2]][mult,[[plus,5];[id]]]").toString)
    //assertResult("[int{?}<=int[plus,2][plus,5][is,bool<=int[gt,0]]]<=[int;[plus,2]][mult,[[plus,5];[is,[gt,0]]]]")(engine.eval("[int;[plus,2]][mult,[[plus,5];[is>0]]]").toString)
  }

  test("poly split/merge/get") {
    assertResult(int(7))(engine.eval("1[plus,1][plus,2][plus,3][path]>-"))
    //
    assertResult("(1)")(engine.eval("1-<(int|str)").toString)
    assertResult("(1)")(engine.eval("1-<(str{?}|int)").toString)
    assertResult("(1)")(engine.eval("1-<(int|int)").toString)
    assertResult("(1)")(engine.eval("1-<(int|0)").toString)
    //
    assertResult("(1)")(engine.eval("1-<(_[is>0]|_[is<0])").toString)
    assertResult("(1)")(engine.eval("1-<([is>0]|[is<0])").toString)
    assertResult("(1)")(engine.eval("1-<(_[is>0]|int[is<0])").toString)
    assertResult("(1)")(engine.eval("1-<(int[is>0]|_[is<0])").toString)
    assertResult("(1)")(engine.eval("(1|2|3)").toString)
    assertResult("(1;2;3)")(engine.eval("(1;2;3)").toString)
    assertResult("(1;(2))")(engine.eval("(1;(2|3))").toString)
    assertResult("'a'")(engine.eval("('a'|).0").toString)
    assertResult("(2)")(engine.eval("(1;(2|3))[get,1]").toString)
    assertResult("2")(engine.eval("(1;(2|3))[get,1][get,0]").toString)
    assertResult("6")(engine.eval("(1;(2;(3;(4;5;6)))).1.1.1.2").toString)
    //////
    assertResult("(str;{0};{0})\n   <=str[split,(str;{0};{0})]")(engine.eval("str-<(str;int;int[plus,2])").toString)
    assertResult("int{8}<=(int{2};int{4}<=int[plus,2]{4})[merge][is,true][id]")(engine.eval("(int{2};int[plus,2]{4})>-[is,true][id]").toString)
    assertResult("(str)\n   <=str[split,(str)]")(engine.eval("str-<(int{?}|bool{?}|str)").toString)
    assertResult("str[split,(str)][merge][plus,'hello']")(engine.eval("str-<(str,,)>-[plus,'hello']").toString)
    assertResult("'kuppitzhello'")(engine.eval("'kuppitz' str-<(str,int,int[plus,2])>-[plus,'hello']").toString)
    assertResult("'kuppitzhello'")(engine.eval("'kuppitz'-<(str,int,int[plus,2])>-[plus,'hello']").toString)
    assertResult("(3)")(engine.eval("int-<(3|int|int[plus,2])").toString)
    assertResult("4")(engine.eval("int-<(3|int|int[is<0])>-[plus,1]").toString)
    /////
    assertResult(zeroObj `,` int(10))(engine.eval("10-<(bool,int)"))
    assertResult(zeroObj `,` int(10))(engine.eval("10 int[id]-<(bool,int)"))
    assertResult(int(10))(engine.eval("10-<(bool,int)>-"))
    assertResult(int(10))(engine.eval("10-<(bool,int)>-[id]"))
    assertResult(int(110))(engine.eval("10-<(bool,int)>-[plus,100]"))
    //
    assertResult("int[plus,100][plus,200][split,(int;int[plus,2])][merge][plus,20]")(engine.eval("int[plus,100][plus,200]-<(int;int[plus,2])>-[plus,20]").toString)
    assertResult("int[plus,100][plus,200][split,(int)][merge][plus,20]")(engine.eval("int[plus,100][plus,200]-<(int|int[plus,2])>-[plus,20]").toString)
    assertResult("(10;10;11)")(engine.eval("10[split,(bool,int)][merge][plus,1][path,(_;)]").toString)
    assertResult("[12,14]")(engine.eval("1[plus,1][split,(int,int[plus,2])]>-[plus,10]").toString)
    //
    // assertResult("bool<=int[plus,10][lt,50]")(engine.eval("(int;[plus,10];int;[lt,50];bool)>-").toString)
  }

  test("[type] instruction parsing") {
    assertResult("bool<=int[plus,1][mult,5][gt,int[mult,int]]")(engine.eval("5[plus,1][mult,5][gt,int[mult,int]][type]").toString)
    assertResult("int{?}<=int[is,bool<=int[lt,10]]")(engine.eval("5[is<10][type]").toString)
    assertResult("[int{0,2}<=int[is,bool<=int[lt,10]],int{?}<=int[is,bool<=int[lt,10]]]")(engine.eval("(5,6,7)>-[is<10][type]").toString)
  }

  test("ring axioms") {
    assertResult(strm(List(int(2).q(2), int(3))))(engine.eval("1[[[plus,1],[mult,2]],[mult,3]]"))
    assertResult(strm(List(int(2).q(2), int(3))))(engine.eval("1[[plus,1],[[mult,2],[mult,3]]]"))
    assertResult(int(2))(engine.eval("1[[id]{0},[plus,1]]"))
    assertResult(int(2))(engine.eval("1[[plus,1],[id]{0}]"))
    assertResult(zeroObj)(engine.eval("1[[plus,1],[plus,1]{-1}]"))
    assertResult(zeroObj)(engine.eval("1[id]{0}"))
    assertResult(int(2).q(2))(engine.eval("1[[plus,1],[mult,2]]"))
    assertResult(int(2).q(2))(engine.eval("1[[mult,2],[plus,1]]"))
    assertResult(int(12))(engine.eval("1[[[plus,1];[mult,2]];[mult,3]]"))
    assertResult(int(12))(engine.eval("1[[plus,1];[[mult,2];[mult,3]]]"))
    assertResult(int(2))(engine.eval("1[[id];[plus,1]]"))
    assertResult(int(2))(engine.eval("1[[id];[plus,1]]"))
    assertResult(int(2))(engine.eval("1[[plus,1];[id]]"))
    assertResult(int(2))(engine.eval("1[plus,1]"))
    assertResult(int(6).q(2))(engine.eval("1[[[plus,1],[mult,2]];[mult,3]]"))
    assertResult(int(6).q(2))(engine.eval("1[[[plus,1];[mult,3]],[[mult,2];[mult,3]]]"))
    assertResult(strm(List(int(4), int(6))))(engine.eval("1[[plus,1];[[mult,2],[mult,3]]]"))
    assertResult(strm(List(int(4), int(6))))(engine.eval("1[[[plus,1];[mult,2]],[[plus,1];[mult,3]]]"))
  }

  test("ring theorems") {
    assertResult(int(2).q(-2))(engine.eval("1[[plus,1],[mult,2]]{-1}"))
    assertResult(int(2).q(-2))(engine.eval("1[[plus,1]{-1},[mult,2]{-1}]"))
    assertResult(int(2))(engine.eval("1[[plus,1]{-1}|]{-1}"))
    assertResult(int(2))(engine.eval("1[plus,1]"))
    assertResult(zeroObj)(engine.eval("1[[plus,1];[id]{0}]"))
    assertResult(zeroObj)(engine.eval("1[id]{0}"))
    assertResult(zeroObj)(engine.eval("1[[id]{0};[plus,1]]"))
    assertResult(int(4).q(-1))(engine.eval("1[[plus,1];[mult,2]{-1}]"))
    assertResult(int(4).q(-1))(engine.eval("1[[plus,1]{-1};[mult,2]]"))
    assertResult(int(4).q(-1))(engine.eval("1[[plus,1];[mult,2]]{-1}"))
    assertResult(int(4))(engine.eval("1[[plus,1]{-1};[mult,2]{-1}]"))
    assertResult(int(4))(engine.eval("1[[plus,1];[mult,2]]"))
  }

  test("define parsing") {
    engine.eval(":[model,mm]")
    assertResult("nat")(engine.eval("int[define,nat<=int[is>0]][as,nat][plus,10]").name)
    assertResult(true.q(3))(engine.eval("(1,2,3)[define,nat<=int[is>0]]>-[a,nat]"))
    assertResult(btrue)(engine.eval("10[define,big<=int[is>4]][a,big]"))
    assertResult(btrue)(engine.eval("10[define,big<=_[is>4]][a,big]"))
    assertResult(bfalse)(engine.eval("2[define,big<=int[is>4]][a,big]"))
    assertResult(bfalse)(engine.eval("2[define,big<=_[is>4]][a,big]"))
    assertResult(int(120))(engine.eval("10[define,big<=int[plus,100]][plus,0][plus,big]"))
    assertResult(int(120))(engine.eval("10[define,big<=int[plus,100]][plus,big]"))
    assertResult((str("name") -> str("marko")) `_,` str("age") -> int(29))(engine.eval("('name'->'marko','age'->29)[define,person:('name'->str,'age'->int)][as,person]-<('name'->.name,'age'->.age)"))
    assertResult(strm(List(str("marko").q(2), int(29).q(2))))(engine.eval("('name'->'marko','age'->29)[define,person:('name'->str,'age'->int)][as,person]-<('name'->.name,'age'->.age)>--<(_,_)>-"))
    assertResult(str("marko"))(engine.eval("('name'->'marko','age'->29)[define,person:('name'->str,'age'->int)][as,person][get,'name']"))
    assertResult(btrue)(engine.eval("('name'->'marko','age'->29)[define,person:('name'->str,'age'->int)][a,person]"))
    assertResult(btrue)(engine.eval("('name'->'marko','age'->29)[define,person:('name'->str,'age'->int)][as,person][a,person]"))
    assertResult(bfalse)(engine.eval("('name'->'marko')[define,person:('name'->str,'age'->int)][a,person]"))
    assertResult(btrue.q(100))(engine.eval("('name'->'marko','age'->29)[define,person:('name'->str,'age'->int)][a,person]{100}"))
    assertResult(btrue.q(100))(engine.eval("('name'->'marko','age'->29)[define,person:('name'->str)][a,person]{100}"))
    assertResult(bfalse.q(100))(engine.eval("('name'->'marko')[define,person:('name'->str,'age'->int)][a,person]{100}"))
    assertResult(bfalse.q(100))(engine.eval("('name'->'marko')[define,person:('name'->str,'age'->int)][a,person]{100}"))
    assertResult(btrue.q(100))(engine.eval("('name'->'marko')[define,person:('name'->str,'age'->int)][plus,('age'->29)][a,person]{100}"))
    assertResult(bfalse.q(100))(engine.eval("('name'->'marko')[define,person:('name'->str,'age'->int)][plus,('years'->29)][a,person]{100}"))
    assertResult(btrue.q(350))(engine.eval("('name'->'marko','age'->29)[define,person:('name'->str,'age'->years)][define,years<=int][a,person]{350}"))
    //    assertResult(rec(str("name") -> str("marko"), str("age") -> int(29)).q(350))(engine.eval("('name'->'marko')[define,person:('name'->str,'age'->int)][put,'age',29][is,[a,person]]{350}"))
    //    assertResult(rec(str("name") -> str("marko"), str("age") -> int(29)).q(350))(engine.eval("('name'->'marko','age'->29)[define,person:('name'->str,'age'->years)][define,years<=int][is,[a,person]]{350}"))
    assertResult(str("old guy"))(engine.eval(
      """ ('name'->'marko','age'->29)
        | [define,person:('name'->str,'age'->int),old<=int[gt,20],young<=int[lt,20]]
        | [is,[a,person]][.age[is,old] -> 'old guy' , .age[is,young] -> 'young guy']""".stripMargin))
    assertResult(str("young guy"))(engine.eval(
      """ ('name'->'ryan','age'->2)
        | [define,person:('name'->str,'age'->int),old<=int[gt,20],young<=int[lt,20]]
        | [is,[a,person]][.age[is,old] -> 'old guy' , .age[is,young] -> 'young guy']""".stripMargin))
    assertResult(zeroObj)(engine.eval(
      """ ('name'->'marko','age'->29)
        | [define,person:('name'->str,'age'->young),old<=int[is>20],young<=int[is<20]]
        | [is,[a,person]][.age+-100[is>0] -> 'old guy' , .age+-100[is<0] -> 'young guy']""".stripMargin))
    assertResult(str("old guy"))(engine.eval(
      """ ('name'->'marko','age'->29)
        | [define,person:('name'->str,'age'->int)]
        | [is,[a,person]][[is.age>20] -> 'old guy' , [is.age<20] -> 'young guy']""".stripMargin))
    assertResult(str("old guy"))(engine.eval(
      """ [('name'->'marko','age'->-29),('name'->'marko','age'->29)]
        | [define,nat<=int[is>0],person:('name'->str,'age'->nat)]
        | [is,[a,person]][[is,[get,'age'][gt,20]] -> 'old guy' , [is,[get,'age'][lt,20]] -> 'young guy']""".stripMargin))
    assertResult(rec(str("name") -> str("ryan"), str("age") -> int(2)))(engine.eval(
      """ [('name'->'ryan','age'->2),('name'->'marko','age'->-29)]
        | [define,nat<=int[is>0],person:('name'->str,'age'->nat)]
        | [is,[a,person]]""".stripMargin))
    assertResult(zeroObj)(engine.eval(
      """ ('name'->'marko','age'->-29)
        | [define,nat<=int[is>0]]
        | [define,person:('name'->str,'age'->nat)]
        | [is,[a,person]]""".stripMargin))
    assertResult(rec(str("name") -> str("marko"), str("age") -> int(29)))(engine.eval(
      """ ('name'->'marko','age'->29)
        | [define,nat<=int[is>0]]
        | [define,person:('name'->str,'age'->nat)]
        | [is,[a,person]]""".stripMargin))
    assertResult(zeroObj)(engine.eval(
      """ ('name'->'marko','age'->29)
        | [define,person:('name'->str,'age'->int),old<=int[is>20],young<=int[is<20]]
        | [is,[a,person]][is.age<0][.age+-100[is,[a,old]] -> 'old guy' , .age+-100[is,[a,young]] -> 'young guy']""".stripMargin))
    ///////////////////
    assertResult(btrue `,` bfalse `,` btrue)(engine.eval(
      """ 1
        | [define,nat<=int[is>0]]
        | [define,z<=int[is==[zero]]]
        | [define,o<=int[is==[one]]]
        |   -<([a,nat],[a,z],[a,o])""".stripMargin))
    /* assertResult("(bool<=int[a,int{?}<=int[is,bool<=int[gt,0]]],bool<=int[a,int{?}<=int[is,bool<=int[eq,0]]],bool<=int[a,int{?}<=int[is,bool<=int[eq,1]]])")(engine.eval(
          """ int
            | [define,nat<=int[is>0]]
            | [define,z<=int[is==[zero]]]
            | [define,o<=int[is==[one]]]
            |   -<([a,nat],[a,z],[a,o])""".stripMargin).range.toString) */
    assertResult(int(11))(engine.eval(
      """ 10
        | [define,z<=int[zero],o<=int[one]]
        | [plus,z][plus,o]""".stripMargin))
    assertResult(int(1).named("o"))(engine.eval(
      """ 10
        | [define,z:0]
        | [define,o<=int[one]]
        | [as,o]""".stripMargin))
    engine.eval(":{1}")
  }

  test("defined types") {
    engine.eval(":[define,nat<=int[is>0]]")
    assertResult(5.named("nat"))(engine.eval("5 => nat"))
    assertThrows[LanguageException] {
      engine.eval("0 => nat")
    }
    engine.eval(":[define,list<=[(_){?}|(_,list)]]")
    assertResult(bfalse)(engine.eval("1              => [a,list]"))
    assertResult(bfalse)(engine.eval("(1,1)          => [a,list]"))
    assertResult(btrue)(engine.eval("(1)             => [a,list]"))
    assertResult(btrue)(engine.eval("(1,(1))         => [a,list]"))
    assertResult(btrue)(engine.eval("(1,(1,(1)))     => [a,list]"))
    assertResult(btrue)(engine.eval("(1,(1,(1,(1)))) => [a,list]"))
    assertThrows[LanguageException] {
      engine.eval("1 => list")
    }
    assertThrows[LanguageException] {
      engine.eval("(1,1) => list")
    }
    engine.eval(":{1}")
  }

  test("recursive definition parsing") {
    assertResult(bfalse)(engine.eval("(1,(2,'3'))[define,xyz<=_[[is,[a,int]]|[is,[a,(int,xyz)]]]][a,xyz]"))
    assertResult(bfalse)(engine.eval("(1,(2,('3',4)))[define,xyz<=_[[is,[a,int]]|[is,[a,(int,xyz)]]]][a,xyz]"))
    assertResult(bfalse)(engine.eval("(1,(2,(3,'4')))[define,xyz<=_[[is,[a,int]]|[is,[a,(int,xyz)]]]][a,xyz]"))
    assertResult(bfalse)(engine.eval("(1,(2,(3,4,5)))[define,xyz<=_[[is,[a,int]]|[is,[a,(int,xyz)]]]][a,xyz]"))
    //
    assertResult(btrue)(engine.eval("(1,2)[define,xyz<=_[[is,[a,int]]|[is,[a,(int,xyz)]]]][a,xyz]"))
    assertResult(btrue)(engine.eval("(1,(2,3))[define,xyz<=_[[is,[a,int]]|[is,[a,(int,xyz)]]]][a,xyz]"))
    assertResult(btrue)(engine.eval("(1,(2,(3,4)))[define,xyz<=_[[is,[a,int]]|[is,[a,(int,xyz)]]]][a,xyz]"))
    assertResult(btrue)(engine.eval("(1,(2,(3,(4,5))))[define,xyz<=_[[is,[a,int]]|[is,[a,(int,xyz)]]]][a,xyz]"))
    //
    assertResult(btrue)(engine.eval("(1,2)[define,xyz<=_[int{?}|(int,xyz)]][a,xyz]"))
    assertResult(btrue)(engine.eval("(1,(2,3))[define,xyz<=_[int{?}|(int,xyz)]][a,xyz]"))
    assertResult(btrue)(engine.eval("(1,(2,(3,4)))[define,xyz<=_[int{?}|(int,xyz)]][a,xyz]"))
    assertResult(btrue)(engine.eval("(1,(2,(3,(4,5))))[define,xyz<=_[int{?}|(int,xyz)]][a,xyz]"))
    assertResult(bfalse)(engine.eval("(1,'a')[define,xyz<=_[int{?}|(int,xyz)]][a,xyz]"))
    assertResult(bfalse)(engine.eval("(1,('a',3))[define,xyz<=_[int{?}|(int,xyz)]][a,xyz]"))
    assertResult(bfalse)(engine.eval("(1,(2,(3,'a')))[define,xyz<=_[int{?}|(int,xyz)]][a,xyz]"))
    assertResult(bfalse)(engine.eval("(1,('a',(3,(4,5))))[define,xyz<=_[int{?}|(int,xyz)]][a,xyz]"))
    //
    assertResult(btrue)(engine.eval("(1;(1;(1;(1;1))))[define,xyz<=_[is==1|(is==1;xyz)]][a,xyz]"))
    assertResult(bfalse)(engine.eval("(1;(1;(1;(1;2))))[define,xyz<=_[is==1|(is==1;xyz)]][a,xyz]"))
    assertResult(bfalse)(engine.eval("(1;(1;(2;(1;1))))[define,xyz<=_[is==1|(is==1;xyz)]][a,xyz]"))
    //
    assertResult(btrue)(engine.eval("(2;1)[define,wxy<=_[is==1]][define,xyz<=_[wxy{?}|(2;xyz)]][a,xyz]"))
    assertResult(btrue)(engine.eval("(1;(1;(1;(1;1))))[define,wxy<=_[is==1]][define,xyz<=_[wxy{?}|(wxy;xyz)]][a,xyz]"))
    assertResult(bfalse)(engine.eval("(1;(2;(1;(1;1))))[define,wxy<=_[is==1]][define,xyz<=_[wxy{?}|(wxy;xyz)]][a,xyz]"))
    assertResult(bfalse)(engine.eval("(1;(1;(1;(1;'1'))))[define,wxy<=_[is==1]][define,xyz<=_[wxy{?}|(wxy;xyz)]][a,xyz]"))
    assertResult(btrue)(engine.eval("1[define,wxy<=_[is==1]][define,xyz<=_[wxy{?}|(wxy,xyz)]][a,xyz]"))
    assertResult(bfalse)(engine.eval("2[define,wxy<=_[is==1]][define,xyz<=_[wxy{?}|(wxy,xyz)]][a,xyz]"))
    assertResult(btrue)(engine.eval("1[define,xyz<=xyz][a,xyz]"))
    //assertThrows[StackOverflowError] { // TODO: ungrounded types should not bind?
    assertResult(btrue)(engine.eval("1[define,wxy<=xyz][define,xyz<=wxy][a,xyz]"))
    //}
  }

  test("loading definitions parser") {
    val file1:String = "'" + getClass.getResource("/load/source-1.mm").getPath + "'"
    assertResult("person:('name'->'marko','age'->nat:29)")(engine.eval(s"('name'->'marko','age'->29)[load,${file1}][as,person]").toString)
    assertThrows[LanguageException] {
      engine.eval(s"('naame'->'marko','age'->29)[load,${file1}][as,person]")
    }
    assertThrows[LanguageException] {
      engine.eval(s"('name'->'marko','adge'->29)[load,${file1}][as,person]")
    }
    assertThrows[LanguageException] {
      engine.eval(s"('name'->'marko')[load,${file1}][as,person]")
    }
    assertThrows[LanguageException] {
      engine.eval(s"('age'->29)[load,${file1}][as,person]")
    }
    assertResult(bfalse)(engine.eval(s"('name'->'marko','age'->-10)[load,${file1}][a,person]"))
    assertResult(zeroObj)(engine.eval(s"('name'->'marko','age'->-10)[load,${file1}][is,[a,person]]"))

    assertResult("vertex:('id'->nat:10,'label'->'marko10')")(engine.eval(s"('name'->'marko','age'->10)[load,${file1}][as,person][as,vertex]").toString)
    assertResult("vertex:('id'->nat:5)")(engine.eval(s"5[load,${file1}][as,vertex]").toString)
  }

  /*  test("[as] parsing") {
      assertResult(btrue)(engine.eval("43 bool<=int[define,bool<=int>0]"))
      assertResult(bfalse)(engine.eval("-43[define,bool<=int>0][as,bool]"))
      println(engine.eval("1[is,-<(true|false)>-]"))
    }*/

  test("frobenius axioms parsing") {
    assertResult(1 `;` 2)(engine.eval("(1,2)-<(_,>-)=(>-,_)=(_;_)"))
    assertResult(1 `;` 2)(engine.eval("(1,2)-<(>-,_)=(_,>-)=(_;_)"))
    assertResult((1 `;` 1).q(2))(engine.eval("(1,1)>--<(_;_)"))
    assertResult(int(1).q(2))(engine.eval("1-<(_,_)>-"))
    // complex quantifier examples
    assertResult(int(1).q(4))(engine.eval("1{2}-<(_,_)>-"))
    assertResult(int(1).q(6))(engine.eval("1{2}-<([id]{2},_)>-"))
    assertResult(int(1).q(24))(engine.eval("1{2}-<([id]{2},_)>-{4}"))
    assertResult(int(1).q(24))(engine.eval("1{2}-<([id]{2},_){4}>-"))
    assertResult(int(1).q(240))(engine.eval("1{2}-<([id]{2},_){4}>-{10}"))

  }

  test("model example") {
    engine.eval(":{1}")
    val ex:Model = LoadOp.loadObj[Model](getClass.getResource("/model/ex.mm").getPath)
    assert(toBaseName(ex).toString.nonEmpty)
    engine.getContext.getBindings(ScriptContext.ENGINE_SCOPE).put(":", __.model(ex))
    // assertResult(int(1))(engine.eval("'marko' => person => user => .login => user => .id"))
    assertResult(int(1).named("nat"))(engine.eval("'marko' => person => user => .login => person => .age"))
    //    assertResult((str("name") -> str("marko") `_,` str("age") -> int(1).named("nat")).named("person"))(engine.eval("('marko','') => person"))
    assertResult((str("name") -> str("marko") `_,` str("age") -> int(1).named("nat")).named("person"))(engine.eval("'marko' => person"))
    assertResult((str("name") -> str("marko") `_,` str("age") -> int(1).named("nat")).named("person"))(engine.eval("('name'->'marko') => person"))
    assertResult((str("name") -> str("marko") `_,` str("age") -> int(29).named("nat")).named("person"))(engine.eval("('name'->'marko','age'->29) => person"))
    assertResult((str("name") -> str("marko") `_,` str("age") -> int(29).named("nat")).named("person"))(engine.eval("('name'->'marko') => person<=[put,'age',29][is,person.age>20][is,user.id[a,nat]]"))
    assertResult((str("id") -> int(29) `_,` str("login") -> str("marko")).named("user"))(engine.eval("('name'->'marko') => user<=person[put,'age',29][is,person.age>20][is,user.id[a,nat]]"))
    assertResult((str("name") -> str("marko") `_,` str("age") -> int(29).named("nat")).named("person"))(engine.eval("('name'->'marko') => person<=[put,'age',29][is,person.age>20][is,user.id[a,nat]]"))
    assertResult(zeroObj)(engine.eval("('name'->'marko') => [put,'age',29][is,person.age>20][as,user].id[is,[a,large]]"))
    assertResult(zeroObj)(engine.eval("('name'->'marko','age'->29) => person[is,person.age>20][as,user].id[is,[a,large]]"))
    // assertResult(zeroObj) (engine.eval("('name'->'marko','age'->29) => person[is,person.age>20][is,user.id[a,large]]"))
  }
  test("model parsing") {
    engine.eval(":[model,pp:('type' -> (person -> (person:('name'->str)))) <= mm]")
    assertResult("person:('name'->'marko')")(engine.eval("('name'->'marko') => [as,person]").toString)
    // assertResult("('type'->(person->(person)))<=_[map,('type'->(person->(person)))]")(engine.eval("[map,pp]").toString)
    engine.eval(":{1}")
    val mm:Model = LoadOp.loadObj[Model](getClass.getResource("/model/mm.mm").getPath)
    assert(toBaseName(mm).toString.nonEmpty)
    assert(mm.rewrites.nonEmpty)
    //println(mm.named("rec"))
    engine.getContext.getBindings(ScriptContext.ENGINE_SCOPE).put(":", __.model(mm))
    assertResult(13.q(8))(engine.eval("10 int[plus,1]{2}[plus,2]{4}"))
    assertResult(int)(engine.eval("int[plus,0]"))
    assertResult(int(0))(engine.eval("int[mult,0]"))
    assertResult(int)(engine.eval("int[mult,1]"))
    assertResult(int.plus(int.neg))(engine.eval("int[mult,1][plus,0][plus,[neg]]"))
    assertResult(int.plus(1).plus(2))(engine.eval("int[plus,1][mult,1][plus,2][mult,1][plus,0]"))
    assertResult(int(5).q(24))(engine.eval("1[plus,4]{24}"))
    assertResult(int(10).q(48))(engine.eval("1[plus,4]{24}[plus,5]{2}"))
    assertResult(int.q(2) <= int.id.q(2))(engine.eval("int[plus,0]{2}"))
    assertResult(int.q(4) <= int.q(2).id.q(2))(engine.eval("int{4}<=int{2}[plus,0]{2}"))
    assertResult(int(10).q(192))(engine.eval("1{2}[plus,4]{24}[plus,2]{2}[plus,3]{2}"))
    assertResult(int(5).q(72))(engine.eval("1{3}[plus,4]{24}"))
    engine.eval(":{1}")
    assertResult(int(10).q(192))(engine.eval("1{2}[plus,4]{24}[plus,2]{2}[plus,3]{2}"))
    assertResult(int(5).q(72))(engine.eval("1{3}[plus,4]{24}"))
  }

  test("model rec parsing") {
    engine.eval(":[model,mm:('type' -> (person -> (person:('name'->str[plus,str],'age'->int{*}+10))))]")
    assertResult("person:('name'->'markomarko')")(engine.eval("('name'->'marko') => [as,person]").toString)
    assertResult("person:('name'->'markomarko')")(engine.eval("('name'->23) person<=rec[put,'name','marko']").toString)
    assertResult("'markomarko'")(engine.eval("('name'->'marko') rec[plus,person[get,'name']-<('name'->_)][get,'name']").toString)
    assertResult("person:('name'->'markomarko','age'->45)")(engine.eval("('name'->'marko') rec[plus,person<=rec[get,'name']-<('name'->_)][is,[a,person]][as,person][put,'age',45]").toString)
    engine.eval(":{1}")
  }

  test("play") {
    assertResult(int)(engine.eval("int[define,(int)<=^:(int[mult,1])][mult,1]"))
    println(engine.eval("10[define,big<=int[plus,100]][plus,big]"))
    println(engine.eval("4[is>3 -> 1 , 4 -> 2]"))
    println(engine.eval("(3)"))
    println(engine.eval("(int;[plus,2];-<([mult,2],[plus,10])>-)<x>[map,5][split,x]"))
    println(engine.eval("(1,2,(3,(4,5)))=(_,_,=(int,=(+20,+10)))"))
  }
}
