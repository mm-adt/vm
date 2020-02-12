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

package org.mmadt.language.obj.`type`

import org.mmadt.storage.obj.{bool, int, str}
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TypeTest extends FunSuite {

  test("type structure w/ one canonical type") {
    var tobj: IntType = int.plus(10).mult(1).is(int.gt(20))
    assertResult("int{?}<=int[plus,10][mult,1][is,bool<=int[gt,20]]")(tobj.toString)
    assertResult(int.q(0, 1))(tobj.pure())
    assertResult(int)(tobj.canonical())
    assertResult(3)(tobj.insts().length)
    //
    tobj = tobj.rinvert[IntType]()
    assertResult("int[plus,10][mult,1]")(tobj.toString)
    assertResult(int)(tobj.pure())
    assertResult(int)(tobj.canonical())
    assertResult(2)(tobj.insts().length)
    //
    tobj = tobj.rinvert[IntType]()
    assertResult("int[plus,10]")(tobj.toString)
    assertResult(int)(tobj.pure())
    assertResult(int)(tobj.canonical())
    assertResult(1)(tobj.insts().length)
    //
    tobj = tobj.rinvert[IntType]()
    assertResult("int")(tobj.toString)
    assertResult(int)(tobj.pure())
    assertResult(int)(tobj.canonical())
    assertResult(0)(tobj.insts().length)
  }

  test("type structure w/ two canonical types") {
    val boolType: BoolType = int.plus(10).mult(1).is(int.gt(20)).gt(100)
    assertResult("bool{?}<=int[plus,10][mult,1][is,bool<=int[gt,20]][gt,100]")(boolType.toString)
    assertResult(bool.q(0, 1))(boolType.pure())
    assertResult(bool)(boolType.canonical())
    assertResult(4)(boolType.insts().length)
    //
    var intType = boolType.rinvert[IntType]()
    assertResult("int{?}<=int[plus,10][mult,1][is,bool<=int[gt,20]]")(intType.toString)
    assertResult(int.q(0, 1))(intType.pure())
    assertResult(int)(intType.canonical())
    assertResult(3)(intType.insts().length)
    //
    intType = intType.rinvert[IntType]()
    assertResult("int[plus,10][mult,1]")(intType.toString)
    assertResult(int)(intType.pure())
    assertResult(int)(intType.canonical())
    assertResult(2)(intType.insts().length)
    //
    intType = intType.rinvert[IntType]()
    assertResult("int[plus,10]")(intType.toString)
    assertResult(int)(intType.pure())
    assertResult(int)(intType.canonical())
    assertResult(1)(intType.insts().length)
    //
    intType = intType.rinvert[IntType]()
    assertResult("int")(intType.toString)
    assertResult(int)(intType.pure())
    assertResult(int)(intType.canonical())
    assertResult(0)(intType.insts().length)
  }

  test("type structure w/ three canonical types") {
    val boolType: BoolType = int.plus(int(10)).mult(int(1)).is(int.gt(int(20))).map(str("hello").plus(str)).gt("a")
    assertResult("bool{?}<=int[plus,10][mult,1][is,bool<=int[gt,20]][map,str<=str{0}[start,'hello'][plus,str]][gt,'a']")(boolType.toString)
    assertResult(bool.q(0, 1))(boolType.pure())
    assertResult(bool)(boolType.canonical())
    assertResult(5)(boolType.insts().length)
    println(boolType.insts())
    //
    val strType: StrType = boolType.rinvert[StrType]()
    assertResult("str{?}<=int[plus,10][mult,1][is,bool<=int[gt,20]][map,str<=str{0}[start,'hello'][plus,str]]")(strType.toString)
    assertResult(str.q(0, 1))(strType.pure())
    assertResult(str)(strType.canonical())
    assertResult(4)(strType.insts().length)
    //
    var intType = strType.rinvert[IntType]()
    assertResult("int{?}<=int[plus,10][mult,1][is,bool<=int[gt,20]]")(intType.toString)
    assertResult(int.q(0, 1))(intType.pure())
    assertResult(int)(intType.canonical())
    assertResult(3)(intType.insts().length)
    //
    intType = intType.rinvert[IntType]()
    assertResult("int[plus,10][mult,1]")(intType.toString)
    assertResult(int)(intType.pure())
    assertResult(int)(intType.canonical())
    assertResult(2)(intType.insts().length)
    //
    intType = intType.rinvert[IntType]()
    assertResult("int[plus,10]")(intType.toString)
    assertResult(int)(intType.pure())
    assertResult(int)(intType.canonical())
    assertResult(1)(intType.insts().length)
    //
    intType = intType.rinvert[IntType]()
    assertResult("int")(intType.toString)
    assertResult(int)(intType.pure())
    assertResult(int)(intType.canonical())
    assertResult(0)(intType.insts().length)
  }

}