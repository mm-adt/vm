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

import org.mmadt.machine.obj.impl.obj._
import org.mmadt.machine.obj.theory.obj.Bool
import org.mmadt.machine.obj.theory.obj.`type`.BoolType
import org.mmadt.machine.obj.theory.obj.value.BoolValue
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class AndOrTest extends FunSuite {

  test("[and] w/ bool") {
    assertResult(btrue)(btrue.and(btrue)) // value * value = value
    assert(btrue.and(btrue).isInstanceOf[BoolValue])
    assert(btrue.and(btrue).isInstanceOf[Bool])
    assertResult(btrue.and(bool))(btrue.and(bool)) // value * type = type
    assert(btrue.and(bool).isInstanceOf[BoolType])
    assert(btrue.and(bool).isInstanceOf[Bool])
    assertResult(bool.and(btrue))(bool.and(btrue)) // type * value = type
    assert(bool.and(btrue).isInstanceOf[BoolType])
    assert(bool.and(btrue).isInstanceOf[Bool])
    assertResult(bool.and(bool))(bool.and(bool)) // type * type = type
    assert(bool.and(bool).isInstanceOf[BoolType])
    assert(bool.and(bool).isInstanceOf[Bool])
  }
}
