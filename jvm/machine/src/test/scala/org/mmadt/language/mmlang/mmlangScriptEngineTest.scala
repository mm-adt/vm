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

import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.obj.Obj._
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.{Obj, Rec}
import org.mmadt.language.{LanguageException, LanguageFactory, Tokens}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmlangScriptEngineTest extends FunSuite {

  lazy val engine: mmADTScriptEngine = LanguageFactory.getLanguage("mmlang").getEngine.get()

  test("empty space parsing") {
    assert(!engine.eval("").alive)
    assert(!engine.eval("    ").alive)
    assert(!engine.eval("  \n  ").alive)
    assert(!engine.eval("\t  \n  ").alive)
  }

  test("parsing errors") {
    /*    assertThrows[LanguageException] {
          engine.eval("_[bad]")
        }*/
    /*    assertThrows[LanguageException] {
          engine.eval("_(bdad,'hello')")
        }*/
    /*    assertThrows[LanguageException] {
          engine.eval("_(bad,1,3)")
        }*/
    assertThrows[LanguageException] {
      engine.eval("3[plus,42.5]")
    }
    assertThrows[LanguageException] {
      engine.eval("3[mult,42.5]")
    }
    assertThrows[LanguageException] {
      println(engine.eval("bool<=int"))
    }
    //TODO
  }

  test("canonical type parsing") {
    assertResult(obj)(engine.eval("obj"))
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
    assertResult(str)(engine.eval("str{1}"))
    assertResult(rec.q(int(5), int(10)))(engine.eval("rec{5,10}"))
    assertResult(lst.q(int(5), int(10)))(engine.eval("lst{5,10}"))
    // assertResult(__.q(+))(engine.eval("_{+}"))
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
    assertResult(int(1, 2, 3))(engine.eval("{1,2,3}"))
    assertResult(str(str("a").q(3), str("b")))(engine.eval("{'a'{3},'b'}"))
    assertResult(strm)(engine.eval("{}"))
    assertResult(strm)(engine.eval("_{0}"))
    assertResult(bool(btrue, bfalse, bfalse, bfalse))(engine.eval("{false{3},true}"))
  }

  test("atomic named value parsing") {
    assertResult(vbool(name = "keep", g = true))(engine.eval("keep:true"))
    assertResult(vint(name = "nat", g = 5))(engine.eval("nat:5"))
    assertResult(vint(name = "score", g = -51))(engine.eval("score:-51"))
    assertResult(vstr(name = "fname", g = "marko"))(engine.eval("fname:'marko'"))
    assertResult(vstr(name = "garbage", g = "marko comp3 45AHA\"\"\\'-%^&"))(engine.eval("garbage:'marko comp3 45AHA\"\"\\'-%^&'"))
  }

  test("rec value parsing") {
    assertResult(rec(str("name") -> str("marko")))(engine.eval("('name'->'marko')"))
    assertResult(rec(str("name") -> str("marko"), str("age") -> int(29)))(engine.eval("('name'->'marko','age'->29)"))
    assertResult(rec(str("name") -> str("marko"), str("age") -> int(29)))(engine.eval("('name'->  'marko' , 'age' ->29)"))
    assertResult(str("marko"))(engine.eval("('name'->'marko','age'->29)[head]"))
    assertResult(rec(str("age") -> int(29)))(engine.eval("('name'->'marko','age'->29)[tail]"))
    assertResult(rec(str("name") -> str("marko"), str("age") -> int(29)))(engine.eval("('a'->23,'name'->'marko','age'->29)[tail]"))
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
    val person: Rec[Obj, Obj] = rec(g = (Tokens.`|`, Map(str("name") -> str, str("age") -> int).asInstanceOf[Map[Obj, Obj]]))
    assertResult(str <= person.get("name"))(engine.eval("('name'->str|'age'->int)[get,'name']"))
    assertResult(int <= person.get("age"))(engine.eval("('name'->str|'age'->int)[get,'age']"))
    assertResult(str <= person.get("name"))(engine.eval("str<=('name'->str|'age'->int)[get,'name']"))
    assertResult(int <= person.get("age"))(engine.eval("int<=('name'->str|'age'->int)[get,'age']"))
    assertResult(int <= rec.put(str("age"), int).get(str("age")))(engine.eval("rec[put,'age',int][get,'age']"))
    assertResult(int <= rec.put(str("age"), int).get(str("age")).plus(int(10)))(engine.eval("rec[put,'age',int][get,'age'][plus,10]"))
    assertResult(int <= rec.put(str("age"), int).get(str("age")).plus(int(10)))(engine.eval("int<=rec[put,'age',int][get,'age'][plus,10]"))
    assertResult(int(20))(engine.eval("('name'->'marko') rec[put,'age',10][get,'age'][plus,10]"))
    assertResult(int(20))(engine.eval("('name'->'marko')[put,'age',10][get,'age'][plus,10]"))
    // assertResult(int(20))(engine.eval("('name'->'marko') int<=rec[put,'age',10][get,'age'][plus,10]"))
    // assertResult(int(20))(engine.eval("('name'->'marko') int<=rec[put,'age',10][get,'age'][plus,10]"))
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
    //    assertResult(int(10))(engine.eval("int[_{0}|10]"))
    assertResult(int(5))(engine.eval("5[_{0}|_{1}]"))
    assertResult(zeroObj)(engine.eval("5-<(_{0};_{1})>-"))
    assertResult(int(5))(engine.eval("5[_{0},_{1}]"))
  }

  test("quantifier inst parsing") {
    assertResult(int.id().q(2).id().q(4))(engine.eval("int[id]{2}[id]{4}"))
    assertResult("int{8}<=int[id]{2}[id]{4}")(engine.eval("int[id]{2}[id]{4}").toString)
    assertResult(int(10).q(8))(engine.eval("10 int[id]{2}[id]{4}"))
    assertResult(int(10).q(8))(engine.eval("10[id]{2}[id]{4}"))
    assertResult("int{8}<=int[plus,10]{2}[id]{4}")(engine.eval("int[plus,10]{2}[id]{4}").toString)
    assertResult(int(15).q(8))(engine.eval("5[plus,10]{2}[id]{4}"))
    assertResult(int(17).q(8))(engine.eval("5[plus,10]{2}[id]{4}[plus,2]"))
    assertResult(int(17).q(16))(engine.eval("5{2}[plus,10]{2}[id]{4}[plus,2]"))
    assertResult(int.q(0, 24))(engine.eval("int[plus,10]{2}[id]{4}[is,[gt,2]]{3}").asInstanceOf[IntType].range)
    assertResult(int(15).q(16))(engine.eval("5{2}[plus,10]{2}[id]{4}"))
    assertResult(int.q(2) ==> (int.q(0, 48) <= int.q(2).plus(10).q(2).id().q(4).is(int.gt(2)).q(3)))(engine.eval("int{2}[plus,10]{2}[id]{4}[is,[gt,2]]{3}"))
    assertResult(int.q(2) ==> (int.q(0, 48) <= int.q(2).plus(10).q(2).id().q(4).is(int.q(16).gt(2)).q(3)))(engine.eval("int{2}[plus,10]{2}[id]{4}[is,[gt,2]]{3}"))
    assertResult(int.q(2) ==> (int.q(0, 48) <= int.q(2).plus(10).q(2).id().q(4).is(bool.q(16) <= int.q(16).gt(2)).q(3)))(engine.eval("int{2}[plus,10]{2}[id]{4}[is,[gt,2]]{3}"))
    assertResult(int.q(0, 48) <= int.q(2).plus(10).q(2).id().q(4).is(int.q(16).gt(2)).q(3))(engine.eval("int{2}[plus,10]{2}[id]{4}[is,[gt,2]]{3}"))
    assertResult(int.q(0, 48) <= int.q(2).plus(10).q(2).id().q(4).is(bool.q(16) <= int.q(16).gt(2)).q(3))(engine.eval("int{2}[plus,10]{2}[id]{4}[is,[gt,2]]{3}"))
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
    assertResult(zeroObj)(engine.eval("obj{0}"))
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
    assertResult(int(1, 2, 3))(engine.eval("[start,{1,2,3}]"))
    assertResult(int)(engine.eval("[start,int]"))
    assertResult(int)(engine.eval("int{20}[start,int]"))
    //assertResult(int)(engine.eval("2[start,int]"))
    //assertResult(str.plus("a"))(engine.eval("2[start,str[plus,'a']]"))
    //assertResult(int)(engine.eval("'a'{4}[start,int]"))
    assertResult(str.plus("a"))(engine.eval("obj{0}[start,str[plus,'a']]"))
    assertResult(int(1) `,` 2 `,` 3)(engine.eval("obj{0}[start,(1,2,3)]"))

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

  test("explain instruction parsing") {
    println(engine.eval("int{3}[+1,+2,+3][explain]"))
    println(engine.eval("int{3}[is>10 -> +1 | is==0 -> +2 | int -> +3][explain]"))
    assert(engine.eval("int[define,nat<=int[is>0]]<x>[plus,[mult,x]][[is,[a,nat]][plus,10]|[define,nonat<=int[plus,0]]]<y>[plus,x][explain]").toString.contains("nat->nat<=int[is,bool<=int[gt,0]] x->int nonat->nonat<=int[plus,0]"))
    assert(engine.eval("int{3}[+1,+2,+3][explain]").toString.contains("(int[plus,1],int[plus,2],int[plus,3]){3}...    =>   int{9}"))
    assert(engine.eval("int[plus,int[mult,6]][explain]").toString.contains("instruction"))
    assert(engine.eval("int[plus,[plus,2][mult,7]]<x>[mult,[plus,5]<y>[mult,[plus,<y>]]][is,[gt,<x>]<z>[id]][plus,5][explain]").toString.contains("z->bool"))
  }

  test("map instruction parsing") {
    assertResult(int.to("x").map(int.from("x").plus(int.from("x"))))(engine.eval("int<x>[map,<.x>+<.x>]"))
    assertResult(int(10))(engine.eval("5<x>[map,<.x>+<.x>]"))
    assertResult(int(11))(engine.eval("5<x>[plus,1]<y>[map,<.x>+<.y>]"))
  }

  test("choice instruction parsing") {
    List(
      int.plus(int(2)).-<(int.is(int.gt(int(10))) --> int.gt(int(20)) | int --> int.plus(int(10)))).
      foreach(choiceInst => {
        assertResult(choiceInst)(engine.eval("int[plus,2]-<(int[is,int[gt,10]]--> int[gt,20] | int --> int[plus,10])"))
        assertResult(choiceInst)(engine.eval("int[plus,2]-<([is,[gt,10]]-->[gt,20] | int-->[plus,10])"))
        assertResult(choiceInst)(engine.eval(
          """
            | int[plus,2]-<
            |    (int[is,int[gt,10]] --> int[gt,20]
            |    |int                --> int[plus,10])""".stripMargin))
        assertResult(choiceInst)(engine.eval(
          """
            | int[plus,2]-<(
            |     int[is,int[gt,10]] --> int[gt,20]
            |    |int                --> int[plus,10])""".stripMargin))
      })
  }

  test("split instruction parsing") {
    val branchString: String = int.plus(2).-<((int.is(int.gt(10)) --> int.gt(20)) `;` (int --> int.plus(10))).toString
    assertResult(branchString)(engine.eval("int[plus,2]-<(int[is,int[gt,10]]-->int[gt,20] ; int --> int[plus,10])").toString)
    // assertResult(branchString)(engine.eval("int[plus,2][[is,int[gt,10]]-->int[gt,20] ; int-->int[plus,10]]").toString) // TODO: choice generalization
  }

  test("choice with given") {
    assertResult(int -< (int.plus(1) | int.plus(2)))(engine.eval("int-<(+1|+2)"))
    assertResult(int(6) | zeroObj)(engine.eval("5-<(+1|+2)"))
    assertResult("(15|)")(engine.eval("5-<(int+1[is>0] --> +10 | str --> +'a')").toString)
    assertResult("(|'aa')")(engine.eval("'a'-<(int+1[is>0] --> +10 | str --> +'a')").toString)
    assertResult("(15;100)")(engine.eval("5-<(int+1[is>0] --> +10 ; int --> 100)").toString)
    assertResult("(15|)")(engine.eval("5-<(int+1[is>0] --> +10 | int --> 100)").toString)
    assertResult("15")(engine.eval("5-<(int+1[is>0] --> +10 | int --> 100)>-").toString)
    assertResult(btrue)(engine.eval("  5 [plus,2][[is>5]-->true|[is==1]-->[plus 2]|int-->20]"))
    assertResult(int(3))(engine.eval("-1[plus,2]-<(int[is>5]-->34|int[is==1]-->int[plus2]|int-->20)>-"))
    assertResult(int(3))(engine.eval("-1 int[plus,2]-<(int[is>5]-->34|int[is==1]-->int[plus2]|int-->20)>-"))
    assertResult(btrue)(engine.eval("  5 [plus,2][[is>5]-->true|[is==1]-->[plus 2]|int-->20]"))
    assertResult(btrue)(engine.eval("  5 [plus,2]-<([is>5]-->true|[is==1]-->[plus 2]|int-->20)>-"))
    assertResult(int(3))(engine.eval("-1[plus,2]-<(int[is>5]-->34|int[is==1]-->int[plus2]|int-->20)>-"))
    assertResult(int(3))(engine.eval("-1 int[plus,2]-<(int[is>5]-->34|int[is==1]-->int[plus2]|int-->20)>-"))
    assertResult(int(20))(engine.eval("1 [plus,2]-<([is>5]-->true|[is==1]-->[plus 2]|int-->20)>-"))
    assertResult(int(20))(engine.eval("1 [plus,2][split,([is>5]-->true|[is==1]-->[plus 2]|int-->20)]>-"))
    assertResult(obj.q(?))(engine.eval("int[plus,2]-<(int[is>5]-->true|[is==1]-->[plus2]|int-->20)>-").range)
    assertResult(bfalse)(engine.eval("4[plus,1]-<([is>5] --> true | int --> false)>-"))
    assertResult(bfalse.q(3))(engine.eval("(4,2,1)>-[plus,1]-<([is>5] --> true | int --> false)>-"))
    assertResult(bool(btrue, bfalse.q(2)))(engine.eval("(5,2,1)>-[plus,1][int[is>5] --> true | int --> false]"))
    assertResult(bool(btrue, bfalse.q(2)))(engine.eval("(5,2,1)>-[plus,1]-<(int[is>5] --> true | int --> false)>-"))
    assertResult(bool(btrue.q(2), bfalse))(engine.eval("(5,2,1)>-[plus,1]-<([is<5] --> true | int --> false)>-"))
    assertResult(bool(btrue.q(2), bfalse))(engine.eval("(5,2,1)>-[plus,1][[is<5] --> true | int --> false]"))
    assertResult(bool(btrue.q(2), bfalse))(engine.eval("(5,2,1)>-[is>0][plus,1][[is<5] --> true | int --> false]"))
    assertResult(bool(btrue.q(2), bfalse))(engine.eval("(5,2,1)>-[plus,1][is>0][mult,1][[is<5] --> true | int --> false]"))
    assertResult(bool(bfalse.q(3), btrue.q(4)))(engine.eval("(4,2,1,10,10,10{2})>-[plus,1]-<([is>5] --> true | int --> false)>-"))
    assertResult(btrue)(engine.eval("5[plus,1]-<([is>5] --> true | int --> false)>-"))
    assertResult(btrue)(engine.eval("true-<(bool --> bool | int --> int)>-"))
    assertResult(int(10))(engine.eval("10-<(bool --> bool | int --> int)>-"))
    assertResult(int(10))(engine.eval("10-<(bool --> true | int --> int)>-"))
    assertResult(int(11))(engine.eval("10-<(bool --> true | int --> int[plus,1])>-"))
    /*    assertResult(int(11, 51, 61))(engine.eval("(10,50,60)>--<(bool --> true | int --> int[plus,1])>-"))
        assertResult(int(11, 51, 61))(engine.eval("(10,50,60)>-[bool --> true | int --> int[plus,1]]"))
        assertResult(int(11, 51, 51, 61))(engine.eval("(10,50{2},60)>--<(bool --> true , int --> int[plus,1])>-"))
        assertResult(int(11, 51, 51, 61))(engine.eval("(10,50{2},60)>-[bool --> true | int --> int[plus,1]]"))
        assertResult(int(11).q(2))(engine.eval("(10,10)>--<(bool --> true | int --> int[plus,1])>-"))*/
    assertResult(int(11).q(2))(engine.eval("10{2}-<(bool --> true | int --> int[plus,1])>-"))
    assertResult(int(302, 42))(engine.eval(
      """ (0,1,2,3)>-
        | [plus,1][is>2]-<(
        |     int[is>3] --> int[mult,10]
        |   | int  --> int[mult,100])>-[plus,2]""".stripMargin))
    assertResult(bfalse)(engine.eval("4[plus,1]-<([is>5] --> true | int --> false)>-"))
    assertResult(btrue)(engine.eval("5[plus,1]-<([is>5] --> true | int --> false)>-"))
  }

  test("choice with rec") {
    assertResult("15")(engine.eval("5 int[int+1[is>0] -> +10 | str -> +'a']").toString)
    assertResult("'aa'")(engine.eval("'a'[int -> +10 | str -> +'a']").toString)
    assertResult("'aa'")(engine.eval("'a'[int+1[is>0] -> +10 | str -> +'a']").toString)
    assertResult("'aa'")(engine.eval("'a'[int+1[is>0] --> +10 | str --> +'a']").toString)
    assertResult("100")(engine.eval("5[int+1[is>0] -> +10 ; int -> 100]").toString)
    assertResult("15")(engine.eval("5[int+1[is>0] -> +10 | int -> 100]").toString)
    assertResult(btrue)(engine.eval("  5 [plus,2][[is>5]->true|[is==1]->[plus 2]|int->20]"))
    assertResult(int(3))(engine.eval("-1[plus,2]-<(int[is>5]->34|int[is==1]->int[plus2]|int->20)>-"))
    assertResult(int(3))(engine.eval("-1 int[plus,2]-<(int[is>5]->34|int[is==1]->int[plus2]|int->20)>-"))
    assertResult(btrue)(engine.eval("  5 [plus,2][[is>5]->true|[is==1]->[plus 2]|int->20]"))
    assertResult(btrue)(engine.eval("  5 [plus,2]-<([is>5]->true|[is==1]->[plus 2]|int->20)>-"))
    assertResult(int(3))(engine.eval("-1[plus,2]-<(int[is>5]->34|int[is==1]->int[plus2]|int->20)>-"))
    assertResult(int(3))(engine.eval("-1 int[plus,2]-<(int[is>5]->34|int[is==1]->int[plus2]|int->20)>-"))
    assertResult(int(20))(engine.eval("1 [plus,2]-<([is>5]->true|[is==1]->[plus 2]|int->20)>-"))
    assertResult(int(20))(engine.eval("1 [plus,2][split,([is>5]->true|[is==1]->[plus 2]|int->20)]>-"))
    //assertResult(obj.q(0, 2))(engine.eval("int[plus,2][int[is>5]->true|[is==1]->[plus2][is,int]]").range)
    assertResult(obj)(engine.eval("int[plus,2][int[is>5]->true|[is==1]->[plus2]|int->20]").range)
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
    assertResult(int(10))(engine.eval("10-<(bool -> bool | int -> int)>-"))
    assertResult(int(10))(engine.eval("10-<(bool -> true | int -> int)>-"))
    assertResult(int(11))(engine.eval("10-<(bool -> true | int -> int[plus,1])>-"))
    assertResult(int(11, 51, 61))(engine.eval("(10,50,60)>--<(bool -> true | int -> int[plus,1])>-"))
    assertResult(int(11, 51, 61))(engine.eval("(10,50,60)>--<(bool -> true | int -> int[plus,1])>-"))
    assertResult(int(11, 51, 51, 61))(engine.eval("(10,50{2},60)>--<(bool -> true , int -> int[plus,1])>-"))
    assertResult(int(11, 51, 51, 61))(engine.eval("(10,50{2},60)>--<(bool -> true | int -> int[plus,1])>-"))
    assertResult(int(11).q(2))(engine.eval("(10,10)>--<(bool -> true | int -> int[plus,1])>-"))
    assertResult(int(11).q(2))(engine.eval("10{2}-<(bool -> true | int -> int[plus,1])>-"))
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
    assertResult(int(10))(engine.eval("10-<(bool -> true | int -> int)>-"))
    assertResult(int(13))(engine.eval("10-<(int -> int[plus,2] ; _ -> int[plus,1])>-"))
    assertResult(int(11, 51, 61))(engine.eval("(10,50,60)>--<(int ->int ; _ -> int[plus,1])>-"))
    assertResult(zeroObj)(engine.eval("(10,50,60)>--<(bool -> true ; int -> int[plus,1])>-"))
    assertResult(int(11, 51, 51, 61))(engine.eval("(10,50{2},60)>--<(int -> int ; int+1 -> int[plus,1])>-"))
    assertResult(zeroObj)(engine.eval("(10,10)>--<(bool -> true ; int -> int[plus,1])>-"))
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
    assertResult(int(11, 22))(engine.eval("{'a','b'}[?'a' -> 11 | ?'b' -> 22]"))
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
    assertResult(int.to("x").plus(int(10)).to("y").mult(int.from("x")))(engine.eval("int<x>[plus,10]<y>[mult,x]"))
    assertResult(int(600))(engine.eval("19[plus,1]<x>[plus,10][mult,x]"))
    assertResult(int(21))(engine.eval("5[plus,2]<x>[mult,2][plus,<.x>]"))
    assertResult(int(21))(engine.eval("5[plus,2]<x>[mult,2][plus,int<.x>]"))
    assertResult("int[plus,2]<x>[mult,2]<y>[plus,int<.x>[plus,int<.y>]]")(engine.eval("int[plus,2]<x>[mult,2]<y>[plus,<.x>[plus,<.y>]]").toString)
    assertResult(int(35))(engine.eval("5[plus,2]<x>[mult,2]<y>[plus,int<.x>[plus,<.y>]]"))
    assertResult(int(13))(engine.eval("5 => int<x>[plus,1][plus,x[plus,2]]"))
    assertResult(int(14))(engine.eval("5 => int<x>[plus,1][plus,<x>[plus,2]]"))
    assertResult(int(19))(engine.eval("5 => int<x>[plus,1][plus,<x>[plus,2]][plus,x]"))
    assertResult(int(28))(engine.eval("5 => int<x>[plus,1][plus,<x>[plus,2]][plus,<x>]"))
    assertResult(int(28).q(3))(engine.eval("{5,5,5} => int<x>[plus,1][plus,<x>[plus,2]][plus,<x>]"))
    assertResult(int(28, 32, 36))(engine.eval("{5,6,7} => int<x>[plus,1][plus,<x>[plus,2]][plus,<x>]"))
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
    assertResult(int(14))(engine.eval("4-<(str->'x'|int->+10)>-"))
    assertResult(int(2, 14))(engine.eval("4-<(int[is>0]->2,int->+10)>-"))
  }

  test("bool strm input parsing") {
    assertResult(btrue)(engine.eval("(true,false)>-[is,[id]]"))
    assertResult(btrue)(engine.eval("(true,false)>-[is,[id]]"))
    assertResult(btrue)(engine.eval("{true,false} => bool{*}[is,[id]]"))
    assertResult(btrue)(engine.eval("{true,false}[is,[id]]"))
  }

  test("int strm input parsing") {
    assertResult(int(-1, 0))(engine.eval("{0,1} => int{+}[plus,-1]"))
    assertResult(int(-1, 0))(engine.eval("{0,1} => int{+}[plus,-1]"))
    assertResult(int(1, 2, 3))(engine.eval("{0,1,2}[plus,1]"))
    assertResult(int(int(1).q(3), int(2).q(10), int(3)))(engine.eval("{0{3},1{10},2}[plus,1]"))
    assertResult(int(30, 40))(engine.eval("(0,1,2,3)>-[plus,1][is,int[gt,2]][mult,10]"))
    assertResult(int(300, 40))(engine.eval("(0,1,2,3)>-[plus,1][is,int[gt,2]][int[is,int[gt,3]] --> int[mult,10] | int --> int[mult,100]]>-"))
    assertResult(int(300, 40))(engine.eval("(0,1,2,3)>-[plus,1][is,int[gt,2]][int[is,int[gt,3]] --> int[mult,10] | int --> int[mult,100]]>-"))
    assertResult(int(300, 40))(engine.eval("(0,1,2,3)>-[plus,1][is,int[gt,2]]-<(int[is,int[gt,3]][mult,10] | int[mult,100])>-"))
    assertResult(int(300, 40))(engine.eval("(0,1,2,3)>-[plus,1][is,int[gt,2]]-<(int[is,int[gt,3]] --> int[mult,10] | int --> int[mult,100])>-"))
    assertResult(int(30, 40))(engine.eval("(0,1,2,3)>-[plus,1][is,int[gt,2]][mult,10]"))
  }

  test("real strm input parsing") {
    assertResult(real(-1.2, 0.0))(engine.eval("{0.0,1.2} => real{+}[plus,-1.2]"))
    assertResult(zeroObj)(engine.eval("real{5}[is,false]"))
  }

  test("str strm input parsing") {
    assertResult(str("marko"))(engine.eval("""('m','a','r','k','o')>-[fold,x.0+x.1]"""))
    assertResult(str("marko"))(engine.eval("""('m','a','r','k','o')>-[id][fold,[zero],x.0+x.1]"""))
    assertResult(str("dr. marko"))(engine.eval("""('m','a','r','k','o')>-[fold,[zero]+'dr. ',x.0+x.1]"""))
    assertResult(str("marko"))(engine.eval("""{'m','a','r','k','o'}[fold,'',x.0[plus,x.1]]"""))
    assertResult(str("marko"))(engine.eval("""{'m','a','r','k','o'}[fold,'',x.0+x.1]"""))
  }

  test("rec strm input parsing") {
    //  assertResult(rec(rec(str("a") -> int(1), str("b") -> int(0)), rec(str("a") -> int(2), str("b") -> int(0))))(engine.eval("""[('a'->1),('a'->2)][plus,('b'->0)]"""))
  }

  test("anonymous expression parsing") {
    assertResult(int.is(int.gt(int.id())))(engine.eval("int[is,[gt,[id]]]"))
    assertResult(real.is(real.gt(real.id())))(engine.eval("real[is,[gt,[id]]]"))
    assertResult(int.plus(int(1)).plus(int.plus(int(5))))(engine.eval("int[plus,1][plus,[plus,5]]"))
    assertResult(int.plus(int(1)).is(int.gt(int(5))))(engine.eval("int[plus,1][is,[gt,5]]"))
    assertResult(int.q(?) <= int.is(int.gt(int(5))))(engine.eval("int[is,[gt,5]]"))
    assertResult(int.q(?) <= int.is(int.gt(int.mult(int.plus(int(5))))))(engine.eval("int[is,[gt,[mult,[plus,5]]]]"))
    assertResult(int.q(?) <= int.is(int.gt(int.mult(int.plus(int(5))))))(engine.eval("int[is,int[gt,int[mult,int[plus,5]]]]"))
    assertResult(engine.eval("int[is,int[gt,int[mult,int[plus,5]]]]"))(engine.eval("int[is,[gt,[mult,[plus,5]]]]"))
    assertResult(int.split(int.is(int.gt(int(5))) --> int(1) | int --> int(2)))(engine.eval(" int-<([is>5] --> 1 | int --> 2)"))
    assertResult(int.plus(int(10)).split(int.is(int.gt(int(10))) --> int.gt(int(20)) | int --> int.plus(int(10))))(engine.eval(" int[plus,10]-<([is,[gt,10]]-->[gt,20] | int-->[plus,10])"))
    assertResult(int.plus(int(10)).split(int.is(int.gt(int(5))) --> int(1) | int --> int(2)))(engine.eval(" int[plus,10]-<([is>5] --> 1 | int --> 2)"))
    assertResult(int(302, 42))(engine.eval(
      """ (0,1,2,3)>-
        | [plus,1][is>2]
        |   [[is>3] -> [mult,10]
        |   | int   -> [mult,100]][plus,2]""".stripMargin))
    assertResult(int(302, 42))(engine.eval(
      """ (0,1,2,3)>-
        | [plus,1][is>2][
        |    [is>3] --> [mult,10]
        |   | int   --> [mult,100]][plus,2]""".stripMargin))
    assertResult(bfalse)(engine.eval("4[plus,1][[is>5] --> true | int --> false]"))
    assertResult(btrue)(engine.eval("5[plus,1]-<([is>5] --> true | int --> false)>-"))
    assertResult(btrue)(engine.eval("true[bool --> bool | int --> int]"))
    assertResult(int(10))(engine.eval("10[bool --> bool | int --> int]"))
    assertResult(int(10))(engine.eval("10[bool --> true | int --> int]"))
    assertResult(int(11))(engine.eval("10[bool --> true | int --> int[plus,1]]"))
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
    assertResult(int(7))(engine.eval("{5{7}}[plus,2][count]"))
    assertResult(int(7))(engine.eval("(5,6,7,8,9,0,1)>-[count]"))
    assertResult(int(5))(engine.eval("{1,3,7,2,1}[plus,2][count]"))
    assertResult(int(6))(engine.eval("{1,3,7,2,1,10}[plus,2][count]"))
    assertResult(int(2))(engine.eval("{1,3,7,2,1,10}[plus,2][is>5][count]"))
    assertResult(int(3))(engine.eval("{1.0,3.1,7.2,2.5,1.1,10.1}+2.0[is>5.0][count]"))
    assertResult(int(3))(engine.eval("(1.0,3.1,7.2,2.5,1.1,10.1)>-+2.0[is>5.0][count]"))
  }

  test("logical expressions") {
    assertResult(btrue)(engine.eval("true[and,true]"))
    assertResult(bfalse)(engine.eval("true{3}[and,true][or,false]"))
    assertResult(btrue.q(3))(engine.eval("true{3}[and,true][or,[and,bool]]"))
    assertResult(bfalse.q(3, 30))(engine.eval("true{3,30}[and,false][or,[and,bool]]"))
    assertResult(bfalse.q(3, 30))(engine.eval("true{3,30}[and,false][and,[or,bool]]"))
  }

  test("lst play") {
    println(engine.eval("int<x>[plus,1][plus,x][trace]"))
    println(engine.eval("1<x>[plus,1][plus,x][trace]"))
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
    assertResult(zeroObj | int(3) | zeroObj)(engine.eval("1<x>[plus,2]-<(x[is>100]|x[plus,2]|x)"))
    assertThrows[LanguageException] {
      engine.eval("1[plus,1][plus,x]")
    }
  }

  test("lst values w/ [mult], [plus], and [zero]") {
    //assertResult(lst)(engine.eval("()"))
    //assertResult(lst)(engine.eval("()"))
    //assertResult(lst)(engine.eval("()"))
    //assertResult(int(1) `;`)(engine.eval("(1)")) // TODO: no sep on singles
    assertResult(int(1) `,`)(engine.eval("(1)"))
    //assertResult(int(1) `|`)(engine.eval("(1)"))  // TODO: no sep on singles
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
    assertResult("('a';'b';'c';'d')")(engine.eval("('a';'b')[mult,('c';'d')]").toString)
    assertResult("(('a';'b';'c')|('a';'b';'d'))")(engine.eval("('a';'b')[mult,('c'|'d')]").toString)
    assertResult("(('a';'b';'c')|('a';'b';'d'))")(engine.eval("('a';'b')*('c'|'d')").toString)
    assertResult("(('a'|'c')|('a'|'d')|('b'|'c')|('b'|'d'))")(engine.eval("('a'|'b')[mult,('c'|'d')]").toString) // ac+ad + bc+bd
    assertResult("(('a'|'c')|('a'|'d')|('b'|'c')|('b'|'d'))")(engine.eval("('a'|'b')*('c'|'d')").toString)
    assertResult("(('a';'c';'d')|('b';'c';'d'))")(engine.eval("('a'|'b')[mult,('c';'d')]").toString) // (a*c*d)+(b*c*d)
    assertResult("(('a';'c';'d')|('b';'c';'d'))")(engine.eval("('a'|'b')*('c';'d')").toString)
    /////////////
    assertResult("(('a';'b';'c'),('a';'b';'d'))")(engine.eval("('a';'b')[mult,('c','d')]").toString)
    assertResult("(('a';'b';'c'),('a';'b';'d'))")(engine.eval("('a';'b')*('c','d')").toString)
    assertResult("(('a','c'),('a','d'),('b','c'),('b','d'))")(engine.eval("('a','b')[mult,('c','d')]").toString) // ac+ad + bc+bd
    assertResult("(('a','c'),('a','d'),('b','c'),('b','d'))")(engine.eval("('a','b')*('c','d')").toString)
    assertResult("(('a';'c';'d'),('b';'c';'d'))")(engine.eval("('a','b')[mult,('c';'d')]").toString) // (a*c*d)+(b*c*d)
    assertResult("(('a';'c';'d'),('b';'c';'d'))")(engine.eval("('a','b')*('c';'d')").toString)
    // plus
    assertResult("('a'|'b'|'c'|'d')")(engine.eval("('a'|'b')[plus,('c'|'d')]").toString)
    assertResult("(('a';'b'),('c';'d'))")(engine.eval("('a';'b')[plus,('c';'d')]").toString)
    assertResult("(('a';'b')|('c'|'d'))")(engine.eval("('a';'b')[plus,('c'|'d')]").toString)
    assertResult("(('a'|'b')|('c';'d'))")(engine.eval("('a'|'b')[plus,('c';'d')]").toString)
    assertResult("('a'|'b')")(engine.eval("('a'|'b')[plus,[zero]]").toString)
    /////////////
    assertResult("('a','b','c','d')")(engine.eval("('a','b')[plus,('c','d')]").toString)
    assertResult("(('a';'b'),('c';'d'))")(engine.eval("('a';'b')[plus,('c';'d')]").toString)
    assertResult("(('a';'b'),('c','d'))")(engine.eval("('a';'b')[plus,('c','d')]").toString)
    assertResult("(('a','b'),('c';'d'))")(engine.eval("('a','b')[plus,('c';'d')]").toString)
    assertResult("('a'|'b')")(engine.eval("('a'|'b')[plus,[zero]]").toString) // TODO: early optimization okay?
    //    assertResult("(('a';'b'),( ))")(engine.eval("('a';'b')[plus,[zero]]").toString) // TODO: type on zero
    assertResult("('a','b')")(engine.eval("('a','b')[plus,[zero]]").toString) // TODO: early optimization okay?
    // mult w; types
    //assertResult("[int[plus,2][plus,5][id]]<=[int;[plus,2]][mult,[[plus,5];[id]]]")(engine.eval("[int;[plus,2]][mult,[[plus,5];[id]]]").toString)
    //assertResult("[int{?}<=int[plus,2][plus,5][is,bool<=int[gt,0]]]<=[int;[plus,2]][mult,[[plus,5];[is,[gt,0]]]]")(engine.eval("[int;[plus,2]][mult,[[plus,5];[is>0]]]").toString)
  }

  test("choice type checking") {
    assertResult(btrue)(engine.eval("'marko'[a,[str|int]]"))
    assertResult(bfalse)(engine.eval("'marko'[a,[real|int]]"))
    assertResult(str)(engine.eval("str-<(str|str|str)>-").range)
    assertResult(str)(engine.eval("str-<(str|str|str)>-").domain)
    assertResult(str)(engine.eval("str[str|str|str]").domain)
    assertResult(str)(engine.eval("str[str|str|str]").range)
    assertResult(str.q(3))(engine.eval("str[str,str,str]").range)
    assertResult(str.q(6))(engine.eval("str[str[id]{1};str[id]{3};str[id]{2}]").range)
    assertResult(str | str | str)(engine.eval("str-<(str|str|str)").range)
    assertResult(str | str | str)(engine.eval("str-<(_|_|_)").range)
    assertResult(str)(engine.eval("str[str[id]{2}|str[id]{5}|str[id]{3,7}]").domain)
    assertResult(str.q(2, 7))(engine.eval("str[str[id]{2}|str[id]{5}|str[id]{3,7}]").range)
    assertResult(str)(engine.eval("str[str{2}|str{5}|str{3,7}]").domain)
    // assertResult(str.q(2, 7))(engine.eval("str[str{2}|str{5}|str{3,7}]").range)
    assertResult(str)(engine.eval("str-<(str[id]{2}|str[id]{5}|str[id]{3,7})>-").domain)
    assertResult(str.q(2, 7))(engine.eval("str-<(str[id]{2}|str[id]{5}|str[id]{3,7})>-").range)
    assertResult(int(9, 7))(engine.eval("2[int+7, int+5]"))
  }

  test("poly split/merge/get") {
    assertResult("(1|)")(engine.eval("1-<(int|str)").toString)
    assertResult("(|1)")(engine.eval("1-<(str|int)").toString)
    assertResult("(1|)")(engine.eval("1-<(int|int)").toString)
    assertResult("(1|)")(engine.eval("1-<(int|0)").toString)
    //
    assertResult("(1|)")(engine.eval("1-<(_[is>0]|_[is<0])").toString)
    assertResult("(1|)")(engine.eval("1-<([is>0]|[is<0])").toString)
    assertResult("(1|)")(engine.eval("1-<(_[is>0]|int[is<0])").toString)
    assertResult("(1|)")(engine.eval("1-<(int[is>0]|_[is<0])").toString)
    assertResult("(1|2|3)")(engine.eval("(1|2|3)").toString)
    assertResult("(1;2;3)")(engine.eval("(1;2;3)").toString)
    assertResult("(1;(2|3))")(engine.eval("(1;(2|3))").toString)
    assertResult("'a'")(engine.eval("('a'|).0").toString)
    assertResult("(2|3)")(engine.eval("(1;(2|3))[get,1]").toString)
    assertResult("3")(engine.eval("(1;(2|3))[get,1][get,1]").toString)
    assertResult("6")(engine.eval("(1;(2;(3|(4|5|6)))).1.1.1.2").toString)
    //////
    assertResult("(str;;)<=str-<(str;;)")(engine.eval("str-<(str;int;int[plus,2])").toString)
    assertResult("int{12}<=(int{2};int{12}<=int{3}[plus,2]{4})>-[is,true][id]")(engine.eval("(int{2};int{3}[plus,2]{4})>-[is,true][id]").toString)
    assertResult("(||str)<=str-<(||str)")(engine.eval("str-<(int|bool|str)").toString)
    assertResult("str-<(str,,)>-[plus,'hello']")(engine.eval("str-<(str,,)>-[plus,'hello']").toString)
    assertResult("'kuppitzhello'")(engine.eval("'kuppitz' str-<(str,int,int[plus,2])>-[plus,'hello']").toString)
    assertResult("'kuppitzhello'")(engine.eval("'kuppitz'-<(str,int,int[plus,2])>-[plus,'hello']").toString)
    assertResult("(3|int|int[plus,2])<=int-<(3|int|int[plus,2])")(engine.eval("int-<(3|int|int[plus,2])").toString)
    assertResult("int{?}<=int-<(3|int|int{?}<=int[is,bool<=int[lt,0]])>-[plus,1]")(engine.eval("int-<(3|int|int[is<0])>-[plus,1]").toString)
    /////
    assertResult(zeroObj `,` int(10))(engine.eval("10-<(bool,int)"))
    assertResult(zeroObj `,` int(10))(engine.eval("10 int[id]-<(bool,int)"))
    assertResult(int(10))(engine.eval("10-<(bool,int)>-"))
    assertResult(int(10))(engine.eval("10-<(bool,int)>-[id]"))
    assertResult(int(110))(engine.eval("10-<(bool,int)>-[plus,100]"))
    //
    assertResult("int[plus,100][plus,200]-<(int;int[plus,2])>-[plus,20]")(engine.eval("int[plus,100][plus,200]-<(int;int[plus,2])>-[plus,20]").toString)
    assertResult("int[plus,100][plus,200]-<(int|int[plus,2])>-[plus,20]")(engine.eval("int[plus,100][plus,200]-<(int|int[plus,2])>-[plus,20]").toString)
    assertResult("(10;10;11)")(engine.eval("10-<(bool,int)>-[plus,1][path]").toString)
    assertResult("int{2}")(engine.eval("1[plus,1]-<(int,int[plus,2])>-[plus,10]").toString)
  }

  test("[type] instruction parsing") {
    assertResult("bool<=int[plus,1][mult,5][gt,int[mult,int]]")(engine.eval("5[plus,1][mult,5][gt,int[mult,int]][type]").toString)
    assertResult("int{?}<=int[is,bool<=int[lt,10]]")(engine.eval("5[is<10][type]").toString)
    assertResult("int{0,3}<=int[is,bool<=int[lt,10]]")(engine.eval("(5,6,7)>-[is<10][type]").toString)
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


  test("repeat parsing") {
    assertResult(int(64))(engine.eval("20[plus,10][repeat,int[plus,1],34]"))
    assertResult(int(64))(engine.eval("20[plus,10](int[plus,1])^(34)"))
    assertResult(int(64))(engine.eval("20[plus,10]([plus,1])^(34)"))
    assertResult(int(64))(engine.eval("20+10(+1)^(34)"))
    assertResult(int(67, 64))(engine.eval("{23,20}+10(+1)^(34)"))
    assertResult((1 `,` 1) `,` (1 `,` 1))(engine.eval("1(-<(_,_))^(2)"))
    assertResult(((1 `,` 1) `,` (1 `,` 1)) `,` ((1 `,` 1) `,` (1 `,` 1)))(engine.eval("1(-<(_,_))^(3)"))
    assertResult(int(11))(engine.eval("1(+2)^(<10)"))
    assertResult(int(11))(engine.eval("1(+1)^(10)"))
    println(engine.eval("[[[1,2],[3,4]],[[5,6],[7,8]]](>-)^(3)[path]"))
  }

  test("combine parsing") {
    assertResult("(2,2,8)")(engine.eval("(1,2,3)=(+1,_,+5)").toString)
    assertResult("(1,2,(3,(24,15)))")(engine.eval("(1,2,(3,(4,5)))=(_,_,=(int,=(+20,+10)))").toString)
    assertResult("(8,10)")(engine.eval("(1)>--<(+1,+2)=(*4,+7)").toString)
    assertResult("(8,10)")(engine.eval("1-<(+1,+2)=(*4,+7)").toString)
    assertResult("(8,(3,10))")(engine.eval("1-<(+1,+2)=(*4,-<(_,+7))").toString)
    assertResult("(8,10,(16,18))")(engine.eval("1-<(+1,+2,+3-<(+4,+5))=(+6,+7,=(+8,+9))").toString)
    //assertResult("(8,(3,10))WRONG")(engine.eval("1-<(+1,+2)=(*4,-<(_,+7))=(_,_)").toString)
    assertResult(int(1, 2, 3, 4, 5, 6, 7))(engine.eval("(1,(2,(3,4,(5,6,7))))(>-)^([a,lst])"))
    //assertResult("((1,1),(1,1)),((3,3),(3,3)),(((1,2),(3,4)),((1,2),(3,4))){3}")(engine.eval("[1,3,((1,2),(3,4))](-<(_,_))^([a,(int,int)])").toString) // TODO: strm q
    //assertResult(int(1, 2, 3))(engine.eval("1,2,[3,](-<(_,])^([a,[[[[int,],],],]][neg])=[=[=[=[<y>,],],],](>-)^([a,lst])[map,y?]"))
  }

  test("define parsing") {
    assertResult("nat")(engine.eval("int[define,nat<=int[is>0]][as,nat][plus,10]").name)
    assertResult(true.q(3))(engine.eval("(1,2,3)[define,nat<=int[is>0]]>-[a,nat]"))
    assertResult(btrue)(engine.eval("10[define,big<=int[is>4]][a,big]"))
    assertResult(btrue)(engine.eval("10[define,big<=_[is>4]][a,big]"))
    assertResult(bfalse)(engine.eval("2[define,big<=int[is>4]][a,big]"))
    assertResult(bfalse)(engine.eval("2[define,big<=_[is>4]][a,big]"))
    assertResult(int(120))(engine.eval("10[define,big<=int[plus,100]][plus,0][plus,big]"))
    assertResult(int(120))(engine.eval("10[define,big<=int[plus,100]][plus,big]"))
    assertResult(btrue)(engine.eval("('name'->'marko','age'->29)[define,person:('name'->str,'age'->int)][a,person]"))
    assertResult(btrue)(engine.eval("('name'->'marko','age'->29)[define,person:('name'->str,'age'->int)][as,person][a,person]"))
    assertResult(bfalse)(engine.eval("('name'->'marko')[define,person:('name'->str,'age'->int)][a,person]"))
    assertResult(btrue.q(100))(engine.eval("('name'->'marko','age'->29)[define,person:('name'->str,'age'->int)][a,person]{100}"))
    assertResult(btrue.q(100))(engine.eval("('name'->'marko','age'->29)[define,person:('name'->str)][a,person]{100}"))
    assertResult(bfalse.q(100))(engine.eval("('name'->'marko')[define,person:('name'->str,'age'->int)][a,person]{100}"))
    assertResult(bfalse.q(100))(engine.eval("('name'->'marko')[define,person<=('name'->str,'age'->int)][a,person]{100}"))
    assertResult(btrue.q(100))(engine.eval("('name'->'marko')[define,person:('name'->str,'age'->int)][plus,('age'->29)][a,person]{100}"))
    assertResult(bfalse.q(100))(engine.eval("('name'->'marko')[define,person:('name'->str,'age'->int)][plus,('years'->29)][a,person]{100}"))
    assertResult(btrue.q(350))(engine.eval("('name'->'marko','age'->29)[define,person<=('name'->str,'age'->years)][define,years<=int][a,person]{350}"))
    assertResult(rec(str("name") -> str("marko"), str("age") -> int(29)).q(350))(engine.eval("('name'->'marko')[define,person:('name'->str,'age'->int)][put,'age',29][is,[a,person]]{350}"))
    assertResult(rec(str("name") -> str("marko"), str("age") -> int(29)).q(350))(engine.eval("('name'->'marko','age'->29)[define,person<=('name'->str,'age'->years)][define,years<=int][is,[a,person]]{350}"))
    assertResult(str("old guy"))(engine.eval(
      """ ('name'->'marko','age'->29)
        | [define,person:('name'->str,'age'->int)]
        | [define,old<=int[gt,20]]
        | [define,young<=int[lt,20]]
        | [is,[a,person]][.age[is,old] -> 'old guy' , .age[is,young] -> 'young guy']""".stripMargin))
    assertResult(str("young guy"))(engine.eval(
      """ ('name'->'ryan','age'->2)
        | [define,person:('name'->str,'age'->int)]
        | [define,old<=int[gt,20]]
        | [define,young<=int[lt,20]]
        | [is,[a,person]][.age[is,old] -> 'old guy' , .age[is,young] -> 'young guy']""".stripMargin))
    assertResult(zeroObj)(engine.eval(
      """ ('name'->'marko','age'->29)
        | [define,person:('name'->str,'age'->young)]
        | [define,old<=int[is>20]]
        | [define,young<=int[is<20]]
        | [is,[a,person]][.age+-100[is>0] -> 'old guy' , .age+-100[is<0] -> 'young guy']""".stripMargin))
    assertResult(str("old guy"))(engine.eval(
      """ ('name'->'marko','age'->29)
        | [define,person:('name'->str,'age'->int)]
        | [is,[a,person]][[is.age>20] -> 'old guy' , [is.age<20] -> 'young guy']""".stripMargin))
    assertResult(str("old guy"))(engine.eval(
      """ {('name'->'marko','age'->-29),('name'->'marko','age'->29)}
        | [define,nat<=int[is>0]]
        | [define,person:('name'->str,'age'->nat)]
        | [is,[a,person]][[is,[get,'age'][gt,20]] -> 'old guy' , [is,[get,'age'][lt,20]] -> 'young guy']""".stripMargin))
    assertResult(rec(str("name") -> str("ryan"), str("age") -> int(2)))(engine.eval(
      """ {('name'->'ryan','age'->2),('name'->'marko','age'->-29)}
        | [define,nat<=int[is>0]]
        | [define,person:('name'->str,'age'->nat)]
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
        | [define,person:('name'->str,'age'->int)]
        | [define,old<=int[is>20]]
        | [define,young<=int[is<20]]
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
        | [define,z<=int[zero]]
        | [define,o<=int[one]]
        | [plus,z][plus,o]""".stripMargin))
    assertResult(int(1).named("o"))(engine.eval(
      """ 10
        | [define,z:0]
        | [define,o<=int[one]]
        | [as,o]""".stripMargin))
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
    assertResult(btrue)(engine.eval("(1,2)[define,xyz<=_[int|(int,xyz)]][a,xyz]"))
    assertResult(btrue)(engine.eval("(1,(2,3))[define,xyz<=_[int|(int,xyz)]][a,xyz]"))
    assertResult(btrue)(engine.eval("(1,(2,(3,4)))[define,xyz<=_[int|(int,xyz)]][a,xyz]"))
    assertResult(btrue)(engine.eval("(1,(2,(3,(4,5))))[define,xyz<=_[int|(int,xyz)]][a,xyz]"))
    assertResult(bfalse)(engine.eval("(1,'a')[define,xyz<=_[int|(int,xyz)]][a,xyz]"))
    assertResult(bfalse)(engine.eval("(1,('a',3))[define,xyz<=_[int|(int,xyz)]][a,xyz]"))
    assertResult(bfalse)(engine.eval("(1,(2,(3,'a')))[define,xyz<=_[int|(int,xyz)]][a,xyz]"))
    assertResult(bfalse)(engine.eval("(1,('a',(3,(4,5))))[define,xyz<=_[int|(int,xyz)]][a,xyz]"))
    //
    assertResult(btrue)(engine.eval("(1,(1,(1,(1,1))))[define,xyz<=_[is==1|(is==1,xyz)]][a,xyz]"))
    assertResult(bfalse)(engine.eval("(1,(1,(1,(1,2))))[define,xyz<=_[is==1|(is==1,xyz)]][a,xyz]"))
    assertResult(bfalse)(engine.eval("(1,(1,(2,(1,1))))[define,xyz<=_[is==1|(is==1,xyz)]][a,xyz]"))
    //
    assertResult(btrue)(engine.eval("(2,1)[define,wxy<=_[is==1]][define,xyz<=_[wxy|(2,xyz)]][a,xyz]"))
    assertResult(btrue)(engine.eval("(1,(1,(1,(1,1))))[define,wxy<=_[is==1]][define,xyz<=_[wxy|(wxy,xyz)]][a,xyz]"))
    assertResult(bfalse)(engine.eval("(1,(2,(1,(1,1))))[define,wxy<=_[is==1]][define,xyz<=_[wxy|(wxy,xyz)]][a,xyz]"))
    assertResult(bfalse)(engine.eval("(1,(1,(1,(1,'1'))))[define,wxy<=_[is==1]][define,xyz<=_[wxy|(wxy,xyz)]][a,xyz]"))
    assertResult(btrue)(engine.eval("1[define,wxy<=_[is==1]][define,xyz<=_[wxy|(wxy,xyz)]][a,xyz]"))
    assertResult(bfalse)(engine.eval("2[define,wxy<=_[is==1]][define,xyz<=_[wxy|(wxy,xyz)]][a,xyz]"))
    /*    assertThrows[StackOverflowError] {
          engine.eval("1[define,wxy<=xyz][define,xyz<=wxy][a,xyz]")
        }
        assertThrows[StackOverflowError] {
          engine.eval("1[define,xyz<=xyz][a,xyz]")
        }
     */
  }

  test("loading definitions parser") {
    val file1: String = "'" + getClass.getResource("/load/source-1.mm").getPath + "'"
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
    assertResult("vertex:('id'->5)")(engine.eval(s"5[load,${file1}][as,vertex]").toString)
  }

  test("[as] parsing") {
    assertResult(btrue)(engine.eval("43[define,bool<=int>0][as,bool]"))
    assertResult(bfalse)(engine.eval("-43[define,bool<=int>0][as,bool]"))
    println(engine.eval("1[is,-<(true|false)>-]"))
  }

  test("frobenius axioms parsing") {
    assertResult(int(1) `,` 1)(engine.eval("(1,1)=(_,-<(_,_))=(>-,_)"))
    assertResult(int(1) `,` 1)(engine.eval("(1,1)=(-<(_,_),_)=(_,>-)"))
    assertResult((int(1) `,` 1).q(2))(engine.eval("(1,1)>--<(_,_)"))
    assertResult(int(1).q(2))(engine.eval("1-<(_,_)>-"))
    // complex quantifier examples
    assertResult(int(1).q(4))(engine.eval("1{2}-<(_,_)>-"))
    assertResult(int(1).q(6))(engine.eval("1{2}-<([id]{2},_)>-"))
    assertResult(int(1).q(24))(engine.eval("1{2}-<([id]{2},_)>-{4}"))
    assertResult(int(1).q(24))(engine.eval("1{2}-<([id]{2},_){4}>-"))
    assertResult(int(1).q(240))(engine.eval("1{2}-<([id]{2},_){4}>-{10}"))

  }

  test("play") {
    /* val x: Obj = engine.eval(
       """int
         |[rewrite,_<=([plus,0])]               // a+0 = a
         |[rewrite,_<=([mult,1])]               // a*1 = a
         |[rewrite,_<=([neg][neg])]             // --a = a
         |[rewrite,(int[zero])<=([mult,0])]     // a*0 = 0
         |[rewrite,([plus,0])<=([plus,[neg]])]  // a-a = 0
         |  *1*0+36+0[plus,*0][plus,int[neg][plus,0][neg][neg]]""".stripMargin)
     assertResult(int(0))(x)*/
    // assertResult(int.plus(int).mult(int).plus(1))(x)
    assertResult(int)(engine.eval("int[rewrite,int<=(int[mult,1])][mult,1]"))
    println(engine.eval("10[define,big<=int[plus,100]][plus,big]"))
    println(engine.eval("4[is>3 -> 1 , 4 -> 2]"))
    println(engine.eval("(3)"))
    println(engine.eval("(int;[plus,2];-<([mult,2],[plus,10])>-)<x>[map,5][split,x]"))
    //println(engine.eval("1,2,[3,](-<(_,])^([a,[[[[int,],],],]][neg])=[=[=[=[<y>,],],],](>-)^([a,lst])[map,y?]"))
    //println(engine.eval("(1,(2,3))=(_,=(<y>,[id]))>-"))
    println(engine.eval("(1,2,(3,(4,5)))=(_,_,=(int,=(+20,+10)))"))
    //println(engine.eval("(1,2,3)=(<y>,_,<x>)>--<(x?|y?)"))
    //println(engine.eval("(1,2,3)=(<y>,_,<x>)>--<(x?[map,'x:']+x[as,str]|y?[map,'y:']+y[as,str])"))
    //println(engine.eval("(1,2,3)=(<y>,_,<x>)>--<('x' -> x?,'y' -> y?)"))
    println(engine.eval("1(-<(_,_))^(3)"))
  }
}
