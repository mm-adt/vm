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

package org.mmadt.storage.obj.value

import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.IntType
import org.mmadt.language.obj.op.map.{IdOp, PlusOp}
import org.mmadt.language.obj.value.strm.IntStrm
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VIntTest extends FunSuite {

  test("int value") {
    assertResult(int(8))(4 + int)
    assertResult(int(3))(int(1) + int(2))
    assertResult(int(3))(int(1) + 2)
    assertResult(int(-4))(-int(4))
    assertResult(int(-4))(int(3) ==> int.plus(1).neg())
  }
  test("int value quantifiers") {
    assertResult(int(3).q(int(2)))(int(3).q(int(2)) ==> int.q(int(2)))
    assertResult(int(7).q(int(2)))(int(3).q(int(2)) ==> int.q(int(2)).plus(int(4)))
    assertResult(int(14).q(int(2)))(int(3).q(int(2)) ==> int.q(int(2)).plus(int(4)).mult(int(2).q(int(34))))
    assertResult(int(14).q(4))(int(3).q(2) ===> int.q(2).plus(int(4)).mult(int(2).q(int(34))).q(2))
    assertResult(bfalse.q(int(3)))(int(5).q(int(3)) ===> int.q(int(3)).plus(int(4)).gt(int(10)))
    assertResult(btrue.q(int(3)))(int(5).q(int(3)) ===> int.q(int(3)).plus(int(4)).gt(int(2)))
    assertResult(int(14).q(12))(int(3).q(2) ==> int.q(2).plus(int(4)).q(2).mult(int(2).q(34)).q(3))
    assertResult(btrue.q(40))(int(3).q(2) ===> int.q(2).plus(int(4)).q(2).gt(int(2).q(34)).q(10))
    assertResult(btrue.q(40))(int(3).q(2) ===> int.q(2).plus(int(4)).q(2).a(int.q(0, 4)).q(10))
    //
    println(int(1, 2, 3).map(int(2)).asInstanceOf[IntStrm].values.map(x => x.trace))
    assertResult(int(int(3).q(50), int(4).q(100)))(int(int(3).q(10), int(4).q(20)).q(5))
    assertResult(int(int(3).q(500), int(4).q(1000)))(int(int(3).q(10), int(4).q(20)).q(5).q(10))
  }
  test("nested lineages of types") {
    val atype = int.id().plus(int.plus(2))
    assertResult(2)(atype.trace.length)
    assertResult((int.id(), PlusOp(int.plus(2))))(atype.trace.last)
    assertResult(PlusOp(int.plus(2)))(atype.trace.last._2)
    assertResult(int.plus(2))(atype.trace.last._2.arg0[IntType])
    assertResult(List((int, PlusOp(2))))(atype.trace.last._2.arg0[IntType].trace)
  }
  test("nested lineages of values") {
    val atype = int.id().plus(int.plus(2))
    assertResult(int(8))(int(3) ===> atype)
    assertResult(2)((int(3) ===> atype).trace.length)
    assertResult((int(3), PlusOp(5)))((int(3) ===> atype).trace.last)
    assertResult(PlusOp(5))((int(3) ===> int.id().plus(int.plus(2))).trace.last._2)
    assertResult(int(5))((int(3) ===> int.id().plus(int.plus(2))).trace.last._2.arg0[Obj])
    assertResult(List((int(3), IdOp()), (int(3), PlusOp(2))))((int(3) ===> int.id().plus(int.plus(2))).trace.last._2.arg0[Obj].trace)
  }
}



