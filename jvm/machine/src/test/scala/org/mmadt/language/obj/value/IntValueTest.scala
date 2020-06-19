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
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

class IntValueTest extends FunSuite {

  test("int value test") {
    // value ~ value
    assert(int(3).test(int(3)))
    assert(int(3).test(int(3).plus(10).plus(-5).plus(-5)))
    assert(!int(3).test(int(-3)))
    assert(!int(3).test(int(3).plus(10).plus(-5)))
    // value ~ type
    assert(int(3).test(int))
    assert(!int(3).test(str))
    assert(int(3).test(int.plus(2)))
    assert(int(3).test(str.map(int(3))))
    assert(!int(3).test(str.map(int)))
  }

}
