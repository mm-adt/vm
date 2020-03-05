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

package org.mmadt.language.mmlang

import org.mmadt.language.Tokens
import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.{IntType,Type}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

import scala.collection.JavaConverters._


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmlangScriptEngineTest extends FunSuite {

  val engine:mmADTScriptEngine = new mmlangScriptEngineFactory().getScriptEngine

  test("empty space parsing"){
    val empty:java.util.Iterator[Obj] = asJavaIterator(Iterator.empty)
    assertResult(empty)(engine.eval(""))
    assertResult(empty)(engine.eval("    "))
    assertResult(empty)(engine.eval("  \n  "))
    assertResult(empty)(engine.eval("\t  \n  "))
  }

  test("canonical type parsing"){
    assertResult(bool)(engine.eval("bool").next)
    assertResult(int)(engine.eval("int").next)
    assertResult(str)(engine.eval("str").next)
    assertResult(rec)(engine.eval("rec").next)
  }

  test("quantified canonical type parsing"){
    assertResult(bool.q(int(2)))(engine.eval("bool{2}").next)
    assertResult(int.q(int(0),int(1)))(engine.eval("int{?}").next)
    assertResult(str)(engine.eval("str{1}").next)
    assertResult(rec.q(int(5),int(10)))(engine.eval("rec{5,10}").next)
  }

  test("atomic value parsing"){
    assertResult(btrue)(engine.eval("true").next)
    assertResult(bfalse)(engine.eval("false").next)
    assertResult(int(5))(engine.eval("5").next)
    assertResult(int(-51))(engine.eval("-51").next)
    assertResult(str("marko"))(engine.eval("'marko'").next)
    assertResult(str("marko comp3 45AHA\"\"\\'-%^&"))(engine.eval("'marko comp3 45AHA\"\"\\'-%^&'").next)
  }

  test("atomic named value parsing"){
    //assertResult(bool("keep",true))(engine.eval("keep:true").next)
    //assertResult(int("nat",5))(engine.eval("nat:5").next)
    //assertResult(int("score",-51))(engine.eval("score:-51").next)
    //assertResult(str("fname","marko"))(engine.eval("fname:'marko'").next)
    //assertResult(str("garbage","marko comp3 45AHA\"\"\\'-%^&"))(engine.eval("garbage:'marko comp3 45AHA\"\"\\'-%^&'").next)
  }

  test("rec value parsing"){
    assertResult(rec(str("name") -> str("marko")))(engine.eval("['name'->'marko']").next)
    assertResult(rec(str("name") -> str("marko"),str("age") -> int(29)))(engine.eval("['name'->'marko','age'->29]").next)
    assertResult(rec(str("name") -> str("marko"),str("age") -> int(29)))(engine.eval("['name'->  'marko' , 'age' ->29]").next)
  }

  test("rec named value parsing"){
    //assertResult(trec("single")(str("name") -> str("marko")))(engine.eval("single:['name'->'marko']").next)
    //assertResult(trec("person")(str("name") -> str("marko"),str("age") -> int(29)))(engine.eval("person:['name'->'marko','age'->29]").next)
    //assertResult(trec("person")(str("name") -> str("marko"),str("age") -> int(29)))(engine.eval("person:['name'->  'marko' , 'age' ->29]").next)
  }

  test("rec type parsing"){
    assertResult(trec(str("name") -> str,str("age") -> int))(engine.eval("[   'name'   ->str ,   'age' ->int]").next)
    assertResult(trec(str("name") -> str,str("age") -> int))(engine.eval("['name'->str,'age'->int]").next)
  }

  test("rec named type parsing"){
    //assertResult(trec("person")(str("name") -> str,str("age") -> int))(engine.eval("person:[   'name'   ->str ,   'age' ->int]").next)
    //assertResult(trec("person")(str("name") -> str,str("age") -> int))(engine.eval("person:['name'->str,'age'->int]").next)
  }

  test("composite type get/put"){
    assertResult(rec.get(str("name"),str))(engine.eval("str<=rec[get,'name',str]").next)
    assertResult(int <= rec.get(str("age"),int))(engine.eval("rec[get,'age',int]").next)
    assertResult(int <= rec.put(str("age"),int).get(str("age")))(engine.eval("rec[put,'age',int][get,'age']").next)
    assertResult(int <= rec.put(str("age"),int).get(str("age")).plus(int(10)))(engine.eval("rec[put,'age',int][get,'age'][plus,10]").next)
    assertResult(int(20))(engine.eval("['name'->'marko'] rec[put,'age',10][get,'age'][plus,10]").next)
    // TODO: these are rec types being used as rec values
    // assertResult(rec(str("name") -> str("marko"),str("age") -> int(20)))(engine.eval("['name':'marko'] => rec[put,'age',10][put,'age',rec[get,'age',int][plus,10]]").next)
    // assertResult(rec(str("name") -> str("marko"),str("age") -> int(25)))(engine.eval("['name':'marko'] => [put,'age',10][put,'age',[get,'age',int][plus,15]]").next)
  }

  test("quantified value parsing"){
    assertResult(btrue.q(int(2)))(engine.eval("true{2}").next)
    assertResult(bfalse)(engine.eval("false{1}").next)
    assertResult(int(5).q(+))(engine.eval("5{+}").next)
    assertResult(int(6).q(qZero))(engine.eval("6{0}").next)
    assertResult(int(7).q(qZero))(engine.eval("7{0,0}").next)
    assertResult(str("marko").q(int(10),int(100)))(engine.eval("'marko'{10,100}").next)
    assertResult(int(20).q(int(10)))(engine.eval("13{10}[plus,7]").next())
    assertResult(int(13).q(int(10)))(engine.eval("13{10}[is>5]").next())
  }

  test("refinement type parsing"){
    assertResult(int.q(?) <= int.is(int.gt(int(10))))(engine.eval("int[is,int[gt,10]]").next)
    assertResult(int <= int.is(int.gt(int(10))))(engine.eval("int<=int[is,int[gt,10]]").next)
    assertResult(int.q(?) <= int.is(int.gt(int(10))))(engine.eval("int[is>10]").next)
  }

  test("endomorphic type parsing"){
    assertResult(int.plus(int.mult(int(6))))(engine.eval("int[plus,int[mult,6]]").next)
  }
  test("explain instruction parsing"){
    assert(engine.eval("int[plus,int[mult,6]][explain]").next().toString.contains("instruction"))
    assert(engine.eval("int[plus,[plus,2][mult,7]]<x>[mult,[plus,5]<y>[mult,[plus,<y>]]][is,[gt,<x>]<z>[id]][plus,5][explain]").next().toString.contains("bool<z>"))
  }

  test("choose instruction parsing"){
    List(
      int.plus(int(2)).choose(int.is(int.gt(int(10))) -> int.gt(int(20)),int -> int.plus(int(10))),
      int.plus(int(2)).choose(trec(int.is(int.gt(int(10))) -> int.gt(int(20)),int -> int.plus(int(10)))),
      int.plus(int(2)).choose(trec(name = Tokens.rec,Map(int.is(int.gt(int(10))) -> int.gt(int(20)),int -> int.plus(int(10)))))).
      foreach(chooseInst => {
        assertResult(chooseInst)(engine.eval("int[plus,2][choose,[int[is,int[gt,10]]->int[gt,20] | int->int[plus,10]]]").next)
        assertResult(chooseInst)(engine.eval("int[plus,2][int[is,int[gt,10]]->int[gt,20] | int->int[plus,10]]").next)
        //assertResult(chooseInst)(engine.eval("int[plus,2][int[is,[gt,10]]->[gt,20] | int->[plus,10]]").next)  // TODO: anons get compiled automagically (need to have consistent behavior between anons and non-anons)
        assertResult(chooseInst)(engine.eval("int[plus,2][int[is,[gt,10]]->int[gt,20] | int->int[plus,10]]").next)
        // assertResult(chooseInst)(engine.eval("int[plus,2][[is,[gt,10]]->[gt,20] | int->[plus,10]]").next) // TODO: anons get compiled automagically (need to have consistent behavior between anons and non-anons)
        assertResult(chooseInst)(engine.eval(
          """
            | int[plus,2]
            |    [int[is,int[gt,10]] -> int[gt,20]
            |    |int                -> int[plus,10]]""".stripMargin).next)
      })
  }

  test("choose with mixed end types"){
    assertResult(btrue)(engine.eval("  5 [plus,2][[is>5]->true|[is==1]->[plus,2]|int->20]").next)
    assertResult(int(3))(engine.eval("-1 [plus,2][[is>5]->true|[is==1]->[plus,2]|int->20]").next)
    assertResult(int(20))(engine.eval("1 [plus,2][[is>5]->true|[is==1]->[plus,2]|int->20]").next)
    assertResult(obj)(engine.eval("int[plus,2][int[is>5]->true|[is==1]->[plus,2]|int->20]").next.asInstanceOf[Type[Obj]].range())
    //
    assertResult(btrue.q(int(3)))(engine.eval("             5{3} [plus,2][[is>5]->true|[is==1]->[plus,2]|int->20]").next)
    assertResult(int(3).q(int(5)))(engine.eval("           -1{5} [plus,2][[is>5]->true|[is==1]->[plus,2]|int->20]").next)
    assertResult(int(20).q(int(8),int(10)))(engine.eval("1{8,10} [plus,2][[is>5]->true|[is==1]->[plus,2]|int->20]").next)
    assertResult(obj.q(+))(engine.eval("int{+}[plus,2][[is>5]->true|[is==1]->[plus,2]|int->20]").next.asInstanceOf[Type[Obj]].range())
  }

  test("traverser read/write state parsing"){
    assertResult(int.to("a").plus(int(10)).to("b").mult(int(20)))(engine.eval("int<a>[plus,10]<b>[mult,20]").next)
    assertResult(int.to("a").plus(int(10)).to("b").mult(int.from[IntType]("a")))(engine.eval("int<a>[plus,10]<b>[mult,<a>]").next)
    assertResult(int.to("a").plus(int(10)).to("b").mult(int.from[IntType]("a")))(engine.eval("int<a>[plus,10]<b>[mult,int<a>]").next)
    assertResult(int.to("a").plus(int(10)).to("b").mult(int.from[IntType]("a")))(engine.eval("int<a>[plus,10]int<b>[mult,int<a>]").next)
  }

  test("infix operator instruction parsing"){
    assertResult(int.plus(int(6)))(engine.eval("int+6").next)
    assertResult(int.plus(int(6)).gt(int(10)))(engine.eval("int+6>10").next)
    assertResult(int.plus(int(1)).mult(int(2)).gt(int(10)))(engine.eval("int+1*2>10").next)
    assertResult(str.plus(str("hello")))(engine.eval("str+'hello'").next)
    assertResult(int.is(int.gt(int(5))))(engine.eval("int[is>5]").next)
    assertResult(int.is(int.gt(int(5))))(engine.eval("int[is > 5]").next)
    // assertResult()(engine.eval(".friend.name") // TODO: . for [get]
  }

  test("bool strm input parsing"){
    assertResult(Set(btrue))(asScalaIterator(engine.eval("true,false bool[is,[id]]")).toSet)
    assertResult(Set(btrue))(asScalaIterator(engine.eval("true,false[is,[id]]")).toSet)
  }

  test("int strm input parsing"){
    assertResult(Set(int(-1),int(0)))(asScalaIterator(engine.eval("0,1 int[plus,-1]")).toSet)
    assertResult(Set(int(1),int(2),int(3)))(asScalaIterator(engine.eval("0,1,2[plus,1]")).toSet)
    assertResult(Set(int(30),int(40)))(asScalaIterator(engine.eval("0,1,2,3 int[plus,1][is,int[gt,2]][mult,10]")).toSet)
    assertResult(Set(int(300),int(40)))(asScalaIterator(engine.eval("0,1,2,3[plus,1][is,int[gt,2]][int[is,int[gt,3]] -> int[mult,10] | int -> int[mult,100]]")).toSet)
    // assertResult(Set(int(30),int(40)))(asScalaIterator(engine.eval("0,1,2,3 ==> (int{3}=>int[plus,1][is,int[gt,2]][mult,10])")).toSet)
  }

  test("str strm input parsing"){
    assertResult(str("marko"))(engine.eval("""'m','a','r','k','o' str[fold,'seed','',str[plus,str<seed>]]""").next)
    assertResult(int(5))(engine.eval("""'m','a','r','k','o'[count]""").next)
  }

  test("rec strm input parsing"){
    assertResult(Set(vrec(str("a") -> int(1),str("b") -> int(0)),vrec(str("a") -> int(2),str("b") -> int(0))))(asScalaIterator(engine.eval("""['a'->1],['a'->2][plus,['b'->0]]""")).toSet)
  }

  test("anonymous expression parsing"){
    assertResult(int.is(int.gt(int.id())))(engine.eval("int[is,[gt,[id]]]").next)
    assertResult(int.plus(int(1)).plus(int.plus(int(5))))(engine.eval("int[plus,1][plus,[plus,5]]").next)
    assertResult(int.plus(int(1)).is(int.gt(int(5))))(engine.eval("int[plus,1][is,[gt,5]]").next)
    assertResult(int.q(?) <= int.is(int.gt(int(5))))(engine.eval("int[is,[gt,5]]").next)
    assertResult(int.q(?) <= int.is(int.gt(int.mult(int.plus(int(5))))))(engine.eval("int[is,[gt,[mult,[plus,5]]]]").next)
    assertResult(int.q(?) <= int.is(int.gt(int.mult(int.plus(int(5))))))(engine.eval("int[is,int[gt,int[mult,int[plus,5]]]]").next)
    assertResult(engine.eval("int[is,int[gt,int[mult,int[plus,5]]]]").next)(engine.eval("int[is,[gt,[mult,[plus,5]]]]").next)
    assertResult(int.choose(int.is(int.gt(int(5))) -> int(1),int -> int(2)))(engine.eval("int int[[is>5] -> 1 | int -> 2]").next) // TODO: a single type (not juxtaposed) should apply its domain to itself to compile itself
    assertResult(int.plus(int(10)).choose(trec(int.is(int.gt(int(10))) -> int.gt(int(20)),int -> int.plus(int(10)))))(engine.eval("int int[plus,10][[is,[gt,10]]->[gt,20] | int->[plus,10]]").next) // TODO: a single type (not juxtaposed) should apply its domain to itself to compile itself
    assertResult(int.plus(int(10)).choose(int.is(int.gt(int(5))) -> int(1),int -> int(2)))(engine.eval("int int[plus,10][[is>5] -> 1 | int -> 2]").next) // TODO: a single type (not juxtaposed) should apply its domain to itself to compile itself
    assertResult(Set(int(302),int(42)))(asScalaIterator(engine.eval(
      """ 0,1,2,3
        | [plus,1][is>2]
        |   [ [is>3] -> [mult,10]
        |   | int    -> [mult,100]][plus,2]""".stripMargin)).toSet)
    assertResult(bfalse)(engine.eval("4[plus,1][[is>5] -> true | int -> false]").next)
    assertResult(btrue)(engine.eval("5[plus,1][[is>5] -> true | int -> false]").next)
    assertResult(btrue)(engine.eval("true[bool -> bool | int -> int]").next)
    assertResult(int(10))(engine.eval("10[bool -> bool | int -> int]").next)
    assertResult(int(10))(engine.eval("10[bool -> true | int -> int]").next)
    assertResult(int(11))(engine.eval("10[bool -> true | int -> int[plus,1]]").next)
  }

  test("expression parsing"){
    assertResult(btrue)(engine.eval("true bool[is,bool]").next)
    assertResult(int(7))(engine.eval("5 int[plus,2]").next)
    assertResult(int(70))(engine.eval("10 int[plus,int[mult,6]]").next)
    assertResult(int(55))(engine.eval("5 int[plus,int[mult,int[plus,5]]]").next)
    assertResult(bfalse)(engine.eval("0 int+1*2>10").next)
    assertResult(str("marko rodriguez"))(engine.eval("'marko' str[plus,' '][plus,'rodriguez']").next)
    assertResult(int(10))(engine.eval("10 int[is,bool<=int[gt,5]]").next)
    assertResult(int.q(?) <= int.plus(int(10)).is(int.gt(int(5))))(engine.eval("int int[plus,10][is,bool<=int[gt,5]]").next)
    assertResult(int.q(?) <= int.plus(int(10)).is(int.gt(int(5))))(engine.eval("int int[plus,10][is,int[gt,5]]").next)
  }

  test("reducing expressions"){
    assertResult(int(7))(engine.eval("5{7} int{7}[plus,2][count]").next)
    assertResult(int(7))(engine.eval("5{7} [plus,2][count]").next)
    assertResult(int(5))(engine.eval("1,3,7,2,1 int[plus,2][count]").next)
    assertResult(int(6))(engine.eval("1,3,7,2,1,10 [plus,2][count]").next)
    assertResult(int(2))(engine.eval("1,3,7,2,1,10 +2[is>5][count]").next)
    ///
    assertResult(int(7))(engine.eval("1,2,3 int[fold,'seed',1,[plus,int<seed>]]").next)
  }

  test("logical expressions"){
    assertResult(btrue)(engine.eval("true[and,true]").next)
    assertResult(btrue.q(3))(engine.eval("true{3}[and,true][or,false]").next)
    assertResult(btrue.q(3))(engine.eval("true{3}[and,true][or,[and,bool]]").next)
    assertResult(bfalse.q(3,30))(engine.eval("true{3,30}[and,false][or,[and,bool]]").next)
  }

  test("composite expression parsing"){
    assertResult(rec(str("age") -> int(29),str("name") -> str("marko")))(engine.eval("['age'->29]rec[rec[is,rec[get,'age',int][gt,30]] -> rec[put,'name','bill'] | rec -> rec[put,'name','marko']]").next())
  }

  test("model parsing"){
    // assertResult(true)(engine.eval(""))
  }
}