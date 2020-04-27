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

import org.mmadt.language.LanguageException
import org.mmadt.language.obj.Obj
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TypeTest extends FunSuite {

  test("type hashCode, equals, toString") {
    val types: List[Type[Obj]] = List(obj, bool, int, real, str, rec) //, __)
    var sameCounter = 0
    var diffCounter = 0
    for (a <- types) {
      for (b <- types) {
        if (a.getClass == b.getClass) {
          sameCounter = sameCounter + 1
          assert(a == b)
          assert(a.name == b.name)
          assert(a.hashCode == b.hashCode)
          assert(a.toString == b.toString)
        } else {
          diffCounter = diffCounter + 1
          assert(a != b)
          assert(a.name != b.name)
          assert(a.hashCode != b.hashCode)
          assert(a.toString != b.toString)
        }
      }
    }
    assertResult(types.length)(sameCounter)
    assertResult(types.length * (types.length - 1))(diffCounter)
  }

  test("type structure w/ one canonical type") {
    var tobj: IntType = int.plus(10).mult(1).is(int.gt(20))
    assertResult(int)(tobj.domain())
    assertResult("int{?}<=int[plus,10][mult,1][is,bool<=int[gt,20]]")(tobj.toString)
    assertResult(int.q(0, 1))(tobj.range)
    assertResult(3)(tobj.lineage.length)
    //
    tobj = tobj.rinvert[IntType]()
    assertResult(int)(tobj.domain())
    assertResult("int[plus,10][mult,1]")(tobj.toString)
    assertResult(int)(tobj.range)
    assertResult(int)(tobj.range.q(qOne))
    assertResult(2)(tobj.lineage.length)
    //
    tobj = tobj.rinvert[IntType]()
    assertResult(int)(tobj.domain())
    assertResult("int[plus,10]")(tobj.toString)
    assertResult(int)(tobj.range)
    assertResult(1)(tobj.lineage.length)
    //
    tobj = tobj.rinvert[IntType]()
    assertResult(int)(tobj.domain())
    assertResult("int")(tobj.toString)
    assertResult(int)(tobj.range)
    assertResult(0)(tobj.lineage.length)

    assertThrows[LanguageException] {
      tobj.rinvert[IntType]()
    }
    //
    assertThrows[LanguageException] {
      tobj.linvert()
    }
  }

  test("type structure w/ two canonical types") {
    val boolType: BoolType = int.plus(10).mult(1).is(int.gt(20)).gt(100).asInstanceOf[BoolType]
    assertResult(int)(boolType.domain())
    assertResult(Nil)(boolType.domain[IntType]().lineage)
    assertResult("bool{?}<=int[plus,10][mult,1][is,bool<=int[gt,20]][gt,100]")(boolType.toString)
    assertResult(bool.q(0, 1))(boolType.range)
    assertResult(bool)(boolType.range.q(qOne))
    assertResult(4)(boolType.lineage.length)
    //
    var intType = boolType.rinvert[IntType]()
    assertResult(int)(intType.domain())
    assertResult("int{?}<=int[plus,10][mult,1][is,bool<=int[gt,20]]")(intType.toString)
    assertResult(int.q(0, 1))(intType.range)
    assertResult(int)(intType.range.q(qOne))
    assertResult(3)(intType.lineage.length)
    //
    intType = intType.rinvert[IntType]()
    assertResult(int)(intType.domain())
    assertResult("int[plus,10][mult,1]")(intType.toString)
    assertResult(int)(intType.range)
    assertResult(2)(intType.lineage.length)
    //
    intType = intType.rinvert[IntType]()
    assertResult(int)(intType.domain())
    assertResult("int[plus,10]")(intType.toString)
    assertResult(int)(intType.range)
    assertResult(1)(intType.lineage.length)
    //
    intType = intType.rinvert[IntType]()
    assertResult(int)(intType.domain())
    assertResult("int")(intType.toString)
    assertResult(int)(intType.range)
    assertResult(0)(intType.lineage.length)
    //
    assertThrows[LanguageException] {
      intType.rinvert[IntType]()
    }
    //
    assertThrows[LanguageException] {
      intType.linvert()
    }
  }

  def domainTest(atype: Type[_]): Unit = {
    assertResult(int)(atype.domain())
    assertThrows[LanguageException] {
      atype.domain[IntType]().rinvert()
    }
    assertThrows[LanguageException] {
      atype.domain[IntType]().linvert()
    }

  }

  test("type structure w/ three canonical types") {
    val boolType: BoolType = int.plus(int(10)).mult(int(1)).is(int.gt(int(20))).map(str("hello").plus(str)).gt("a").asInstanceOf[BoolType]
    assertResult("bool{?}<=int[plus,10][mult,1][is,bool<=int[gt,20]][map,str<=[start,'hello'][plus,str]][gt,'a']")(boolType.toString)
    assertResult(bool.q(0, 1))(boolType.range)
    assertResult(bool)(boolType.range.q(qOne))
    assertResult(5)(boolType.lineage.length)
    assertResult(int.mult(int(1)).is(int.gt(int(20))).map(str("hello").plus(str)).gt("a"))(boolType.linvert())
    assertResult(int.is(int.gt(int(20))).map(str("hello").plus(str)).gt("a"))(boolType.linvert().linvert())
    assertResult(int.mult(int(1)).is(int.gt(int(20))).map(str("hello").plus(str)))(boolType.linvert().rinvert())
    domainTest(boolType)
    //
    val strType: StrType = boolType.rinvert[StrType]()
    assertResult(int)(strType.domain())
    assertResult("str{?}<=int[plus,10][mult,1][is,bool<=int[gt,20]][map,str<=[start,'hello'][plus,str]]")(strType.toString)
    assertResult(str.q(0, 1))(strType.range)
    assertResult(str)(strType.range.q(qOne))
    assertResult(4)(strType.lineage.length)
    domainTest(strType)
    //
    var intType = strType.rinvert[IntType]()
    assertResult(int)(intType.domain())
    assertResult("int{?}<=int[plus,10][mult,1][is,bool<=int[gt,20]]")(intType.toString)
    assertResult(int.q(0, 1))(intType.range)
    assertResult(int)(intType.range.q(qOne))
    assertResult(3)(intType.lineage.length)
    domainTest(intType)
    //
    intType = intType.rinvert[IntType]()
    assertResult(int)(intType.domain())
    assertResult("int[plus,10][mult,1]")(intType.toString)
    assertResult(int)(intType.range)
    assertResult(2)(intType.lineage.length)
    domainTest(intType)
    //
    intType = intType.rinvert[IntType]()
    assertResult(int)(intType.domain())
    assertResult("int[plus,10]")(intType.toString)
    assertResult(int)(intType.range)
    assertResult(1)(intType.lineage.length)
    domainTest(intType)
    //
    intType = intType.rinvert[IntType]()
    assertResult(int)(intType.domain())
    assertResult("int")(intType.toString)
    assertResult(int)(intType.range)
    assertResult(0)(intType.lineage.length)
    domainTest(intType)
  }
}
