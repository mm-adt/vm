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

import org.mmadt.language.obj.`type`.BoolType
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VBoolTest extends FunSuite {
  test("bool value"){
    assert(btrue.value())
    assertResult(btrue)(btrue || bfalse)
    assertResult(bfalse)(btrue && bfalse)
    assertResult(btrue)(int(4) ==> (int.plus(3).mult(int) ==> int.plus(2).gt(5)).asInstanceOf[BoolType])
  }
  test("bool value quantifiers"){
    assertResult(btrue.q(int(2)))(btrue.q(int(2)) ==> bool.q(int(2)))
    assertResult(btrue.q(int(2)))(btrue.q(int(2)) ==> bool.q(int(2)).and(btrue))
    assertResult(btrue.q(int(2)))(btrue.q(int(2)) ==> bool.q(int(2)).and(btrue).or(bfalse.q(int(34))))
    // assertResult(btrue.q(int(4)))(btrue.q(int(2)) ==> bool.q(int(2)).and(btrue).or(bfalse.q(int(34))).q(int(2)))
    // assertResult(int(14).q(4))(int(3).q(int(2)) ==> int.q(int(2)).plus(int(4)).q(2).mult(int(2).q(34)).q(3))
  }
}
