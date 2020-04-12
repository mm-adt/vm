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
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class EqInstTest extends FunSuite {
  test("[eq] w/ int"){
    assertResult(bfalse)(int(1).eqs(int(3))) // value * value = value
    assertResult(btrue)(int(1).eqs(int(1)))
    assertResult(btrue)(int(1).eqs(1))
    assert(int(1).eqs(int(3)).isInstanceOf[BoolValue])
    assert(int(1).eqs(int(3)).isInstanceOf[Bool])
    assertResult(int(1).eqs(int))(int(1).eqs(int)) // value * type = type
    assert(int(1).eqs(int).isInstanceOf[BoolType])
    assert(int(1).eqs(int).isInstanceOf[Bool])
    assertResult(int.eqs(int(3)))(int.eqs(int(3))) // type * value = type
    assert(int.eqs(int(3)).isInstanceOf[BoolType])
    assert(int.eqs(int(3)).isInstanceOf[Bool])
    assertResult(int.eqs(int))(int.eqs(int)) // type * type = type
    assert(int.eqs(int).isInstanceOf[BoolType])
    assert(int.eqs(int).isInstanceOf[Bool])
  }
}