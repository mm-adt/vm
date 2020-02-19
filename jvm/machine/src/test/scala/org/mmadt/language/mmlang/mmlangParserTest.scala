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

import org.mmadt.language.obj.ORecType
import org.mmadt.language.obj.`type`.{BoolType,IntType,StrType}
import org.mmadt.language.obj.value.{BoolValue,IntValue,StrValue}
import org.mmadt.storage.obj._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmlangParserTest extends FunSuite {

  val parser:mmlangParser.type = mmlangParser

  test("canonical type parsing"){
    assertResult(bool)(parser.parse[BoolType]("bool"))
    assertResult(int)(parser.parse[IntType]("int"))
    assertResult(str)(parser.parse[StrType]("str"))
    assertResult(rec)(parser.parse[ORecType]("rec"))
  }

  test("quantified canonical type parsing"){
    assertResult(bool.q(int(2)))(parser.parse[BoolType]("bool{2}"))
    assertResult(int.q(int(0),int(1)))(parser.parse[IntType]("int{?}"))
    assertResult(str)(parser.parse[StrType]("str{1}"))
    assertResult(rec.q(int(5),int(10)))(parser.parse[ORecType]("rec{5,10}"))
  }

  test("value parsing"){
    assertResult(btrue)(parser.parse[BoolValue]("true"))
    assertResult(bfalse)(parser.parse[BoolValue]("false"))
    assertResult(int(5))(parser.parse[IntValue]("5"))
    assertResult(int(-51))(parser.parse[IntValue]("-51"))
    assertResult(str("marko"))(parser.parse[StrValue]("'marko'"))
    assertResult(str("marko comp3 45AHA\"\"\\'-%^&"))(parser.parse[StrValue]("'marko comp3 45AHA\"\"\\'-%^&'"))
    assertResult(rec(str("name") -> str("marko")))(parser.parse[StrValue]("['name':'marko']"))
    assertResult(rec(str("name") -> str("marko"),str("age") -> int(29)))(parser.parse[StrValue]("['name':'marko','age':29]"))
    assertResult(rec(str("name") -> str("marko"),str("age") -> int(29)))(parser.parse[StrValue]("['name':  'marko' , 'age' :29]"))
  }

  test("quantified value parsing"){
    assertResult(btrue.q(int(2)))(parser.parse[BoolValue]("true{2}"))
    assertResult(bfalse)(parser.parse[BoolValue]("false{1}"))
    assertResult(int(5).q(qPlus))(parser.parse[IntValue]("5{+}"))
    assertResult(int(6).q(qZero))(parser.parse[IntValue]("6{0}"))
    assertResult(int(7).q(qZero))(parser.parse[IntValue]("7{0,0}"))
    assertResult(str("marko").q(int(10),int(100)))(parser.parse[StrValue]("'marko'{10,100}"))
  }

  test("refinement type parsing"){
    assertResult(int.q(qMark) <= int.is(int.gt(int(10))))(parser.parse[IntType]("int[is,int[gt,10]]"))
    assertResult(int <= int.is(int.gt(int(10))))(parser.parse[IntType]("int<=int[is,int[gt,10]]"))
  }

  test("expression parsing"){
    assertResult(btrue)(parser.parse[BoolValue]("true => bool[is,bool]"))
    assertResult(int(7))(parser.parse[IntValue]("5 => int[plus,2]"))
    assertResult(str("marko rodriguez"))(parser.parse[IntValue]("'marko' => str[plus,' '][plus,'rodriguez']"))
    assertResult(int(10))(parser.parse[IntValue]("10=>int[is,bool<=int[gt,5]]"))
    assertResult(int.q(?) <= int.plus(int(10)).is(int.gt(int(5))))(parser.parse[IntType]("int => int[plus,10][is,bool<=int[gt,5]]"))
    assertResult(int.q(?) <= int.plus(int(10)).is(int.gt(int(5))))(parser.parse[IntType]("int => int[plus,10][is,int[gt,5]]"))
  }
}