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

package org.mmadt.language.obj.value

import org.mmadt.language.LanguageException
import org.mmadt.language.obj.{Obj, asType}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

class ValueTest extends FunSuite {

  test("value hashCode, equals, toString") {
    val values: List[Value[Obj]] = List(btrue, int(1), real(1.0), str("1"), (int(1) `,`).asInstanceOf[LstValue[Obj]], rec(int(1) -> str("1")).asInstanceOf[RecValue[Obj, Obj]])
    var sameCounter = 0
    var diffCounter = 0
    for (a <- values) {
      for (b <- values) {
        if (a.getClass == b.getClass) {
          sameCounter = sameCounter + 1
          assert(a == b)
          assert(a.name == b.name)
          assert(a.g == b.g)
          assert(a.hashCode == b.hashCode)
          assert(a.toString == b.toString)
        } else {
          diffCounter = diffCounter + 1
          assert(a != b)
          assert(a.name != b.name)
          assert(!a.g.equals(b.g)) // == in Scala converts numbers
          assert(a.hashCode != b.hashCode)
          assert(a.toString != b.toString)
        }
      }
    }
    assertResult(values.length)(sameCounter)
    assertResult(values.length * (values.length - 1))(diffCounter)
  }

  test("value structure w/ two canonical types") {
    val avalue = int(5).plus(10).id.mult(5).gt(10)
    assertResult(bool)(asType(avalue))
    assertResult(4)(avalue.trace.length)
    // rinvert
    assertResult(int(5).plus(10).id.mult(5))(avalue.rinvert[IntValue])
    assertResult(int(5).plus(10).id)(avalue.rinvert[IntValue].rinvert[IntValue])
    assertResult(int(5).plus(10))(avalue.rinvert[IntValue].rinvert[IntValue].rinvert[IntValue])
    assertResult(int(5))(avalue.rinvert[IntValue].rinvert[IntValue].rinvert[IntValue].rinvert[IntValue])
    assertThrows[LanguageException] {
      avalue.rinvert[IntValue].rinvert[IntValue].rinvert[IntValue].rinvert[IntValue].rinvert[IntValue]
    }
    // linvert
    assertResult(int(15).id.mult(5).gt(10))(avalue.linvert)
    assertResult(int(15).mult(5).gt(10))(avalue.linvert.linvert)
    assertResult(int(75).gt(10))(avalue.linvert.linvert.linvert)
    assertResult(btrue)(avalue.linvert.linvert.linvert.linvert)
    assertThrows[LanguageException] {
      avalue.linvert.linvert.linvert.linvert.linvert
    }
  }
}
