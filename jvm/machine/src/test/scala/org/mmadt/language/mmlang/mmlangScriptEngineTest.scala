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

  test("value parsing"){
    assertResult(btrue)(engine.eval("true").next)
    assertResult(bfalse)(engine.eval("false").next)
    assertResult(int(5))(engine.eval("5").next)
    assertResult(int(-51))(engine.eval("-51").next)
    assertResult(str("marko"))(engine.eval("'marko'").next)
    assertResult(str("marko comp3 45AHA\"\"\\'-%^&"))(engine.eval("'marko comp3 45AHA\"\"\\'-%^&'").next)
    assertResult(rec(str("name") -> str("marko")))(engine.eval("['name':'marko']").next)
    assertResult(rec(str("name") -> str("marko"),str("age") -> int(29)))(engine.eval("['name':'marko','age':29]").next)
    assertResult(rec(str("name") -> str("marko"),str("age") -> int(29)))(engine.eval("['name':  'marko' , 'age' :29]").next)
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
    val chooseInst:Obj = int.plus(int(2)).choose(rec(int.is(int.gt(int(10))) -> int.gt(int(20)),int -> int.plus(int(10))))
    assertResult(chooseInst)(engine.eval("int[plus,2][choose,[int[is,int[gt,10]]:int[gt,20],int:int[plus,10]]]").next)
    assertResult(chooseInst)(engine.eval("int[plus,2][choose,[int[is,int[gt,10]]->int[gt,20] | int->int[plus,10]]]").next)
    assertResult(chooseInst)(engine.eval("int[plus,2][int[is,int[gt,10]]->int[gt,20] | int->int[plus,10]]").next)
    assertResult(chooseInst)(engine.eval(
      """
        | int[plus,2]
        |    [int[is,int[gt,10]] -> int[gt,20]
        |    |int                -> int[plus,10]]""".stripMargin).next)
  }

  test("infix operator instruction parsing"){
    assertResult(int.plus(int(6)))(engine.eval("int+6").next)
    assertResult(int.plus(int(6)).gt(int(10)))(engine.eval("int+6>10").next)
    assertResult(int.plus(int(1)).mult(int(2)).gt(int(10)))(engine.eval("int+1*2>10").next)
    assertResult(str.plus(str("hello")))(engine.eval("str+'hello'").next)
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
}