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

package org.mmadt.language.obj.`type`

import org.mmadt.language.LanguageException
import org.mmadt.language.obj.op.map.PlusOp
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TypeOperatorTest extends FunSuite {

  test("insts analysis from type composition") {
    assertResult(List((int, PlusOp(int(1)))))(int.plus(1).trace)
    assertResult(List((int, PlusOp(int(1))), (int.plus(1), PlusOp(int(2)))))(int.plus(1).plus(2).trace)
    assertResult(List((int, PlusOp(int(1))), (int.plus(1), PlusOp(int(2))), (int.plus(1).plus(2), PlusOp(int(3)))))(int.plus(1).plus(2).plus(3).trace)
    assertResult(List((int, PlusOp(int(1))), (int.plus(1), PlusOp(int(2)))))(int.plus(1).plus(2).plus(3).rinvert[IntType].trace)
    assertResult(List((int, PlusOp(int(1))), (int.plus(1), PlusOp(int(2))), (int.plus(1).plus(2), PlusOp(int(3)))))(int.plus(1).plus(2).plus(3).rinvert[IntType].plus(3).trace)
  }

  test("insts analysis from type right inverse") {
    assertResult(List((int, PlusOp(int(1))), (int.plus(1), PlusOp(int(2)))))(int.plus(1).plus(2).plus(3).rinvert[IntType].trace)
    assertResult(List((int, PlusOp(int(1)))))(int.plus(1).plus(2).plus(3).rinvert[IntType].rinvert[IntType].trace)
    assertResult(List())(int.plus(1).plus(2).plus(3).rinvert[IntType].rinvert[IntType].rinvert[IntType].trace)
    assertResult(int)(int.plus(1).plus(2).plus(3).rinvert[IntType].rinvert[IntType].rinvert[IntType])
    assertThrows[LanguageException] {
      int.plus(1).plus(2).plus(3).rinvert[IntType].rinvert[IntType].rinvert[IntType].rinvert
    }
    assertResult(List((int, PlusOp(int(1))), (int.plus(1), PlusOp(int(4)))))(int.plus(1).plus(2).plus(3).rinvert[IntType].rinvert[IntType].plus(4).trace)
    assertResult(List((int, PlusOp(int(1))), (int.plus(1), PlusOp(int(4))), (int.plus(1).plus(4), PlusOp(int(5)))))(int.plus(1).plus(2).plus(3).rinvert[IntType].rinvert[IntType].plus(4).plus(5).trace)
  }

  test("insts analysis from type left inverse") {
    assertResult(List((int, PlusOp(int(2))), (int.plus(2), PlusOp(int(3)))))(int.plus(1).plus(2).plus(3).linvert.trace)
    assertResult(List((int, PlusOp(int(3)))))(int.plus(1).plus(2).plus(3).linvert.linvert.trace)
    assertResult(List())(int.plus(1).plus(2).plus(3).linvert.linvert.linvert.trace)
    assertThrows[LanguageException] {
      assertResult(List())(int.plus(1).plus(2).plus(3).linvert.linvert.linvert.linvert.trace)
    }
  }
}
