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

import org.mmadt.language.obj.value.{IntValue, StrValue}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

import scala.collection.immutable.ListMap

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VRecTest extends FunSuite {

  val X: (IntValue, StrValue) = int(1) -> str("a")
  val Y: (IntValue, StrValue) = int(2) -> str("b")
  val Z: (IntValue, StrValue) = int(3) -> str("c")

  test("rec value toString") {
    assertResult("[->]")(rec().toString)
  }

  test("rec values") {
    assertResult("[1->true]")(rec(int(1) -> btrue).toString)
    assertResult("[1->true;2->false]")(rec(int(1) -> btrue, int(2) -> bfalse).toString)
    assertResult("[1->true;2->false]")(rec(int(1) -> btrue).plus(rec(int(2) -> bfalse)).toString)
    assertResult(bfalse)(rec(int(1) -> btrue) ==> rec.plus(rec(int(2) -> bfalse)).get(int(2), bool))
    assertResult(rec(int(1) -> btrue, int(2) -> bfalse))(rec(int(1) -> btrue) ==> rec.plus(rec(int(2) -> bfalse)))
    assertResult(btrue)(rec(int(1) -> btrue, int(2) -> bfalse).get(int(1)))
    assertResult(bfalse)(rec(int(1) -> btrue, int(2) -> bfalse).get(int(2)))
    intercept[NoSuchElementException] {
      rec(int(1) -> btrue, int(2) -> bfalse).get(int(3))
    }
  }

  test("rec value via varargs construction") {
    // forwards keys
    assertResult(ListMap(X, Y))(rec(X, Y).gmap)
    assertResult(ListMap(X, Y))(rec(X).plus(rec(Y)).gmap)
    assertResult(ListMap(X, Y, Z))(rec(X, Y, Z).gmap)
    assertResult(ListMap(X, Y, Z))(rec(X).plus(rec(Y, Z)).gmap)
    assertResult(ListMap(X, Y, Z))(rec(X, Y).plus(rec(Z)).gmap)
    // backwards keys
    assertResult(ListMap(Y, X))(rec(Y, X).gmap)
    assertResult(ListMap(Y, X))(rec(Y).plus(rec(X)).gmap)
    assertResult(ListMap(Z, Y, X))(rec(Z, Y, X).gmap)
    assertResult(ListMap(Z, Y, X))(rec(Z).plus(rec(Y, X)).gmap)
    assertResult(ListMap(Z, Y, X))(rec(Z, Y).plus(rec(X)).gmap)
    // overwrite orderings
    assertResult(ListMap(X, Y, Z))(rec(X, Y).plus(rec(X, Z)).gmap) // TODO: determine overwrite order
  }

  test("rec value via map construction") {
    // forwards keys
    assertResult(ListMap(X, Y))(vrec(Map(X, Y)).gmap)
    assertResult(ListMap(X, Y))(vrec(Map(X)).plus(vrec(Map(Y))).gmap)
    assertResult(ListMap(X, Y, Z))(vrec(Map(X, Y, Z)).gmap)
    assertResult(ListMap(X, Y, Z))(vrec(Map(X)).plus(vrec(Map(Y, Z))).gmap)
    assertResult(ListMap(X, Y, Z))(vrec(Map(X, Y)).plus(vrec(Map(Z))).gmap)
    // backwards keys
    assertResult(ListMap(Y, X))(vrec(Map(Y, X)).gmap)
    assertResult(ListMap(Y, X))(vrec(Map(Y)).plus(vrec(Map(X))).gmap)
    assertResult(ListMap(Z, Y, X))(vrec(Map(Z, Y, X)).gmap)
    assertResult(ListMap(Z, Y, X))(vrec(Map(Z)).plus(vrec(Map(Y, X))).gmap)
    assertResult(ListMap(Z, Y, X))(vrec(Map(Z, Y)).plus(vrec(Map(X))).gmap)
    // overwrite orderings
    assertResult(ListMap(X, Y, Z))(vrec(Map(X, Y)).plus(vrec(Map(X, Z))).gmap) // TODO: determine overwrite order
  }

  test("rec value quantifiers") {
    assertResult(rec(X, Y).q(int(2)))(rec(X, Y).q(int(2)) ==> rec.q(int(2)))
    assertResult(rec(X, Y, Z).q(2))(rec(X, Y).q(int(2)) ==> rec.q(int(2)).plus(vrec(Z)))
    assertResult(rec(X, Y, Z).q(2))(rec(X).q(int(2)) ==> rec.q(int(2)).plus(vrec(Y)).plus(vrec(Z).q(34)))
    assertResult(rec(X, Y, Z).q(4))(rec(X).q(int(2)) ==> rec.q(int(2)).plus(vrec(Y)).plus(vrec(Z).q(34)).q(2))
  }
}
