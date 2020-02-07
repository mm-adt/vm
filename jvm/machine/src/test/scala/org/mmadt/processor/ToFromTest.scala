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

package org.mmadt.processor

import org.mmadt.machine.obj.impl.obj.int
import org.mmadt.machine.obj.theory.obj.Int
import org.mmadt.machine.obj.theory.obj.value.IntValue
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ToFromTest extends FunSuite {

  test("[to][from] w/ values") {
    assertResult(int(1))(int(1) ==> int.to("x").plus(1).map(int.from("x")))
    assertResult(int(1))(int(1) ==> int.to("x").plus(1).map(int(100)).from("x"))
    intercept[NoSuchElementException] {
      assertResult(int(20))(int(1) ==> int.from[IntValue]("x").plus(1).map(int.mult(10)))
    }
  }

  test("[to][from] w/ types") {
    assertResult(int(5))(int(1) ==> int.plus(1).map(int(5)).to("x").from("x"))
    assertResult(int(16))(int(1) ==> int.plus(2).to("x").plus(1).to("y").map(int.plus(int.from[Int]("x").mult(int.from[Int]("y")))))
    assertResult("int[plus,1][map,int][to,x]")(int.plus(1).map(int).to("x").toString)
    intercept[NoSuchElementException] {
      assertResult(int(20))(int(1) ==> int.plus(1).map(int.mult(10).to("x")).from("x"))
    }
  }
}