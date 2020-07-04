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

package org.mmadt.storage.obj.value.strm.util

import org.mmadt.language.obj.IntQ
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

class MultiSetTest extends FunSuite {

  def qmaker(a: Int, b: Int): IntQ = (a, b)
  def qmaker(a: Int): IntQ = (a, a)

  test("multiset put") {
    assertResult(1L)(MultiSet.put(int(2)).objSize)
    assertResult(qOne)(MultiSet.put(int(2)).qSize)
    //
    assertResult(1L)(MultiSet.put(int(2)).put(int(2)).objSize)
    assertResult(qmaker(2))(MultiSet.put(int(2)).put(int(2)).qSize)
    //
    assertResult(1L)(MultiSet.put(int(2)).put(int(2)).put(int(2)).objSize)
    assertResult(qmaker(3))(MultiSet.put(int(2)).put(int(2)).put(int(2)).qSize)
    //
    assertResult(1L)(MultiSet.put(int(2)).put(int(2)).put(int(2).q(1, 2)).objSize)
    assertResult(qmaker(3, 4))(MultiSet.put(int(2)).put(int(2)).put(int(2).q(1, 2)).qSize)
    //
    assertResult(2L)(MultiSet.put(btrue).put(btrue.q(10)).put(bfalse.q(1, 2)).objSize)
    assertResult(qmaker(12, 13))(MultiSet.put(btrue).put(btrue.q(10)).put(bfalse.q(1, 2)).qSize)
    //
    assertResult(2L)(MultiSet.put(btrue).put(btrue.q(10)).put(bfalse.q(1, 2)).put(btrue.q(20)).objSize)
    assertResult(qmaker(32, 33))(MultiSet.put(btrue).put(btrue.q(10)).put(bfalse.q(1, 2)).put(btrue.q(20)).qSize)
  }

  test("multiset seq") {
    assertResult(2L)(MultiSet.put(int(2), int(3)).objSize)
    assertResult(qmaker(2))(MultiSet.put(int(2), int(3)).qSize)
    //
    assertResult(2L)(MultiSet.put(int(2), int(3), int(3).q(10)).objSize)
    assertResult(qmaker(12))(MultiSet.put(int(2), int(3), int(3).q(10)).qSize)
  }

  test("multiset w/ inst") {
    println(int(1, 1, 1).q(20))
    assertResult(int(int(12).q(40), int(13).q(40), int(14).q(40)))(int(1, 2, 3).q(20).plus(10).q(2).plus(1))
    assertResult(int(int(12).q(4), int(13).q(2)))(int(1, 1, 2).plus(10).q(2).plus(1))
    assertResult(int(int(12).q(40), int(13).q(40)))(int(1, 1, 2).q(20).plus(10).q(2).plus(1))
  }
}
