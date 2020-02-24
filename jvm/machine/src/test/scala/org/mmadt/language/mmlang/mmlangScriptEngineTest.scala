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

import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.IntType
import org.mmadt.storage.obj._
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

  test("composite value parsing"){
    assertResult(rec(str("name") -> str("marko")))(engine.eval("['name':'marko']").next)
    assertResult(rec(str("name") -> str("marko"),str("age") -> int(29)))(engine.eval("['name':'marko','age':29]").next)
    assertResult(rec(str("name") -> str("marko"),str("age") -> int(29)))(engine.eval("['name':  'marko' , 'age' :29]").next)
    //    assertResult(rec(str("name") -> str("marko"),str("age") -> int(29)))(engine.eval("['name'->'marko' , 'age' ->29]").next)
    //    assertResult(rec(str("name") -> str("marko"),str("age") -> int(29)))(engine.eval("['name'->'marko'|'age'->29]").next)
  }

  test("composite type get/put"){
    assertResult(rec.get(str("name"),str))(engine.eval("str<=rec[get,'name',str]").next)
    assertResult(int <= rec.get(str("age"),int))(engine.eval("rec[get,'age',int]").next)
  }

  test("quantified value parsing"){
    assertResult(btrue.q(int(2)))(engine.eval("true{2}").next)
    assertResult(bfalse)(engine.eval("false{1}").next)
    assertResult(int(5).q(qPlus))(engine.eval("5{+}").next)
    assertResult(int(6).q(qZero))(engine.eval("6{0}").next)
    assertResult(int(7).q(qZero))(engine.eval("7{0,0}").next)
    assertResult(str("marko").q(int(10),int(100)))(engine.eval("'marko'{10,100}").next)
  }

  test("refinement type parsing"){
    assertResult(int.q(qMark) <= int.is(int.gt(int(10))))(engine.eval("int[is,int[gt,10]]").next)
    assertResult(int <= int.is(int.gt(int(10))))(engine.eval("int<=int[is,int[gt,10]]").next)
  }

  test("endomorphic type parsing"){
    assertResult(int.plus(int.mult(int(6))))(engine.eval("int[plus,int[mult,6]]").next)
  }

  test("choose instruction parsing"){
    List(
      int.plus(int(2)).choose(int.is(int.gt(int(10))) -> int.gt(int(20)),int -> int.plus(int(10))),
      // int.plus(int(2)).choose(rec(int.is(int.gt(int(10))) -> int.gt(int(20)),int -> int.plus(int(10)))), //  TODO: is RecType any rec that has a type in it?
      int.plus(int(2)).choose(rec(Map(int.is(int.gt(int(10))) -> int.gt(int(20)),int -> int.plus(int(10)))))).
      foreach(chooseInst => {
        assertResult(chooseInst)(engine.eval("int[plus,2][choose,[int[is,int[gt,10]]:int[gt,20],int:int[plus,10]]]").next)
        // assertResult(chooseInst)(engine.eval("int[plus,2][choose,[int[is,int[gt,10]]->int[gt,20] | int->int[plus,10]]]").next)
        assertResult(chooseInst)(engine.eval("int[plus,2][int[is,int[gt,10]]->int[gt,20] | int->int[plus,10]]").next)
        assertResult(chooseInst)(engine.eval(
          """
            | int[plus,2]
            |    [int[is,int[gt,10]] -> int[gt,20]
            |    |int                -> int[plus,10]]""".stripMargin).next)
      })
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

  test("strm input parsing"){
    assertResult(Set(int(1),int(2),int(3)))(asScalaIterator(engine.eval("0,1,2 ==> int[plus,1]")).toSet)
    assertResult(Set(int(30),int(40)))(asScalaIterator(engine.eval("0,1,2,3 ==> int[plus,1][is,int[gt,2]][mult,10]")).toSet)
    assertResult(Set(int(300),int(40)))(asScalaIterator(engine.eval("0,1,2,3 ==> int[plus,1][is,int[gt,2]][int[is,int[gt,3]] -> int[mult,10] | int -> int[mult,100]]")).toSet)
    // assertResult(Set(int(30),int(40)))(asScalaIterator(engine.eval("0,1,2,3 ==> (int{3}=>int[plus,1][is,int[gt,2]][mult,10])")).toSet)
  }

  test("anonymous parsing"){
    assertResult(int.is(int.gt(int.id())))(engine.eval("int => [is,[gt,[id]]]").next)
    assertResult(int.plus(int(1)).plus(int.plus(int(5))))(engine.eval("int => [plus,1][plus,[plus,5]]").next)
    assertResult(int.plus(int(1)).is(int.gt(int(5))))(engine.eval("int => [plus,1][is,[gt,5]]").next)
    assertResult(int.q(?) <= int.is(int.gt(int(5))))(engine.eval("int => [is,[gt,5]]").next)
    assertResult(int.q(?) <= int.is(int.gt(int.mult(int.plus(int(5))))))(engine.eval("int => [is,[gt,[mult,[plus,5]]]]").next)
    assertResult(int.q(?) <= int.is(int.gt(int.mult(int.plus(int(5))))))(engine.eval("int[is,int[gt,int[mult,int[plus,5]]]]").next)
    assertResult(engine.eval("int[is,int[gt,int[mult,int[plus,5]]]]").next)(engine.eval("int => [is,[gt,[mult,[plus,5]]]]").next)
    assertResult(int.choose(int.is(int.gt(int(5))) -> int(1),int -> int(2)))(engine.eval("int => [[is>5] -> 1 | int -> 2]").next)
    assertResult(int.plus(int(10)).choose(int.is(int.gt(int(5))) -> int(1),int -> int(2)))(engine.eval("int => [plus,10][[is>5] -> 1 | int -> 2]").next)
    assertResult(bfalse)(engine.eval("4 => [plus,1][[is>5] -> true | int -> false]").next)
    assertResult(btrue)(engine.eval("5 => [plus,1][[is>5] -> true | int -> false]").next)
    assertResult(btrue)(engine.eval("true => [bool -> bool | int -> int]").next)
    // TODO: assertResult(int(10))(engine.eval("10 => [bool -> bool | int -> int]").next)
  }

  test("expression parsing"){
    assertResult(btrue)(engine.eval("true => bool[is,bool]").next)
    assertResult(int(7))(engine.eval("5 => int[plus,2]").next)
    assertResult(int(70))(engine.eval("10 => int[plus,int[mult,6]]").next)
    assertResult(int(55))(engine.eval("5 => int[plus,int[mult,int[plus,5]]]").next)
    assertResult(bfalse)(engine.eval("0 => int+1*2>10").next)
    assertResult(str("marko rodriguez"))(engine.eval("'marko' => str[plus,' '][plus,'rodriguez']").next)
    assertResult(int(10))(engine.eval("10=>int[is,bool<=int[gt,5]]").next)
    assertResult(int.q(?) <= int.plus(int(10)).is(int.gt(int(5))))(engine.eval("int => int[plus,10][is,bool<=int[gt,5]]").next)
    assertResult(int.q(?) <= int.plus(int(10)).is(int.gt(int(5))))(engine.eval("int => int[plus,10][is,int[gt,5]]").next)
  }

  test("composite expression parsing"){
    assertResult(rec(str("age") -> int(29),str("name") -> str("marko")))(engine.eval("['age':29] ==> rec[rec[is,rec[get,'age',int][gt,30]] -> rec[put,'name','bill'] | rec -> rec[put,'name','marko']]").next())
  }
}