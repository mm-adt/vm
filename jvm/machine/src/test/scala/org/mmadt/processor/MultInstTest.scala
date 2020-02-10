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
import org.mmadt.machine.obj.theory.obj.`type`.IntType
import org.mmadt.machine.obj.theory.obj.value.IntValue
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class MultInstTest extends FunSuite {

  test("[mult] w/ int") {
    assertResult(int(3))(int(1).mult(int(3))) // value * value = value
    assert(int(1).mult(int(3)).isInstanceOf[IntValue])
    assert(int(1).mult(int(3)).isInstanceOf[Int])
    assertResult(int(1).mult(int))(int(1).mult(int)) // value * type = type
    assert(int(1).mult(int).isInstanceOf[IntType])
    assert(int(1).mult(int).isInstanceOf[Int])
    assertResult(int.mult(int(3)))(int.mult(int(3))) // type * value = type
    assert(int.mult(int(3)).isInstanceOf[IntType])
    assert(int.mult(int(3)).isInstanceOf[Int])
    assertResult(int.mult(int))(int.mult(int)) // type * type = type
    assert(int.mult(int).isInstanceOf[IntType])
    assert(int.mult(int).isInstanceOf[Int])
  }
}
