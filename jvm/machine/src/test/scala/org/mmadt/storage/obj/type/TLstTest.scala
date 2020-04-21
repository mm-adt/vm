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

package org.mmadt.storage.obj.`type`

import org.mmadt.language.obj.Obj
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.TableDrivenPropertyChecks

class TLstTest extends FunSuite with TableDrivenPropertyChecks {

  test("lst type w/ [head][tail]") {
    assertResult(obj)(lst.head().range)
    assertResult(obj.q(10))(lst.q(10).head().range)
    assertResult(str.q(1, 6))(tlst(str).q(1, 6).head().range)
    assertResult(str.q(1, 6))(tlst(str, int).q(1, 6).head().range)
   // assertResult(int.q(1, 6))(tlst(str, int).q(1, 6).tail().head().range)
  }

  test("lst type w/ lst value") {
    assert(vlst(str("a")).test(lst))
    assert(vlst(str("a")).test(tlst(str("a"))))
    assert(vlst(str("a"), str("b")).test(tlst(str("a"), str("b"))))
    assert(vlst(str("a"), str("b")).test(tlst(str("a"), str)))
    assert(vlst(str("a"), str("b")).test(lst[Obj].is(lst[Obj].get(int).a(str))))
    assert(!vlst(str("a")).test(tlst(str("b"))))
    assert(!vlst(str("a"), str("b")).test(tlst(str("a"), str("c"))))
    // println(tlst(int) ==> lst[Obj].is(lst[Obj].get(1).a(str))) // TODO
  }

}