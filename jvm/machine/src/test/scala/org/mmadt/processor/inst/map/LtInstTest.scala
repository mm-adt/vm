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

package org.mmadt.processor.inst.map

import org.mmadt.language.obj.Bool
import org.mmadt.language.obj.`type`.BoolType
import org.mmadt.language.obj.value.BoolValue
import org.mmadt.storage.StorageFactory.{btrue, int}
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class LtInstTest extends FunSuite {
  test("[gt] w/ int"){
    assertResult(btrue)(int(1).lt(int(3))) // value * value = value
    assert(int(1).lt(int(3)).isInstanceOf[BoolValue])
    assert(int(1).lt(int(3)).isInstanceOf[Bool])
    assertResult(int(1).lt(int))(int(1).lt(int)) // value * type = type
    assert(int(1).lt(int).isInstanceOf[BoolType])
    assert(int(1).lt(int).isInstanceOf[Bool])
    assertResult(int.lt(int(3)))(int.lt(int(3))) // type * value = type
    assert(int.lt(int(3)).isInstanceOf[BoolType])
    assert(int.lt(int(3)).isInstanceOf[Bool])
    assertResult(int.lt(int))(int.lt(int)) // type * type = type
    assert(int.lt(int).isInstanceOf[BoolType])
    assert(int.lt(int).isInstanceOf[Bool])
  }
}
