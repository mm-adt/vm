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

import org.mmadt.language.obj.Bool
import org.mmadt.language.obj.`type`.BoolType
import org.mmadt.language.obj.value.BoolValue
import org.mmadt.storage.obj._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class GtInstTest extends FunSuite {

  test("[gt] w/ int") {
    assertResult(bool(false))(int(1).gt(int(3))) // value * value = value
    assert(int(1).gt(int(3)).isInstanceOf[BoolValue])
    assert(int(1).gt(int(3)).isInstanceOf[Bool])
    assertResult(int(1).gt(int))(int(1).gt(int)) // value * type = type
    assert(int(1).gt(int).isInstanceOf[BoolType])
    assert(int(1).gt(int).isInstanceOf[Bool])
    assertResult(int.gt(int(3)))(int.gt(int(3))) // type * value = type
    assert(int.gt(int(3)).isInstanceOf[BoolType])
    assert(int.gt(int(3)).isInstanceOf[Bool])
    assertResult(int.gt(int))(int.gt(int)) // type * type = type
    assert(int.gt(int).isInstanceOf[BoolType])
    assert(int.gt(int).isInstanceOf[Bool])
  }

  test("unary [gt] w/ int") {
    assertResult(int.gt(5))(int(5).gt())
    assertResult(int.gt(5))(int.gt(int(5)))
    assertResult(int.is(int.gt(int(4))))(int.is(gt(4)))
  }
}
