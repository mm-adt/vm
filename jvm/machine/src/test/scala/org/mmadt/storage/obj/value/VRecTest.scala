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
import org.mmadt.storage.obj._
import org.scalatest.FunSuite

import scala.collection.immutable.ListMap

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VRecTest extends FunSuite {

  val X:(IntValue,StrValue) = int(1) -> str("a")
  val Y:(IntValue,StrValue) = int(2) -> str("b")
  val Z:(IntValue,StrValue) = int(3) -> str("c")

  test("rec values"){
    assertResult("[1->true]")(rec(int(1) -> btrue).toString)
    assertResult("[1->true,2->false]")(rec(int(1) -> btrue,int(2) -> bfalse).toString)
    assertResult("[1->true,2->false]")(rec(int(1) -> btrue).plus(rec(int(2) -> bfalse)).toString)
    assertResult(bfalse)(rec(int(1) -> btrue) ==> rec.plus(rec(int(2) -> bfalse)).get(int(2),bool))
    assertResult(rec(int(1) -> btrue,int(2) -> bfalse))(rec(int(1) -> btrue) ==> rec.plus(rec(int(2) -> bfalse)))
    assertResult(btrue)(rec(int(1) -> btrue,int(2) -> bfalse).get(int(1)))
    assertResult(bfalse)(rec(int(1) -> btrue,int(2) -> bfalse).get(int(2)))
    // TODO: MOVE THESE TO A RECTYPE TEST AS RECVALUES CAN'T HAVE TYPES
    //  assertResult(int.plus(5))(rec(int(1) -> int.plus(5),int(2) -> int.mult(100)).get(int(1)))
    //  assertResult(int.mult(100))(rec(int(1) -> int.plus(5),int(2) -> int.mult(100)).get(int(2)))
    //  assertResult(int.mult(100))(rec(int(1) -> int.plus(5),int(2) -> int.mult(100)) ==> rec.get(int(2),int))
    intercept[NoSuchElementException]{
      rec(int(1) -> btrue,int(2) -> bfalse).get(int(3))
    }
  }

  test("rec value via varargs construction"){
    // forwards keys
    assertResult(ListMap(X,Y))(rec(X,Y).value)
    assertResult(ListMap(X,Y))(rec(X).plus(rec(Y)).value)
    assertResult(ListMap(X,Y,Z))(rec(X,Y,Z).value)
    assertResult(ListMap(X,Y,Z))(rec(X).plus(rec(Y,Z)).value)
    assertResult(ListMap(X,Y,Z))(rec(X,Y).plus(rec(Z)).value)
    // backwards keys
    assertResult(ListMap(Y,X))(rec(Y,X).value)
    assertResult(ListMap(Y,X))(rec(Y).plus(rec(X)).value)
    assertResult(ListMap(Z,Y,X))(rec(Z,Y,X).value)
    assertResult(ListMap(Z,Y,X))(rec(Z).plus(rec(Y,X)).value)
    assertResult(ListMap(Z,Y,X))(rec(Z,Y).plus(rec(X)).value)
    // overwrite orderings
    assertResult(ListMap(X,Y,Z))(rec(X,Y).plus(rec(X,Z)).value) // TODO: determine overwrite order
  }

  test("rec value via map construction"){
    // forwards keys
    assertResult(ListMap(X,Y))(rec(Map(X,Y)).value)
    assertResult(ListMap(X,Y))(rec(Map(X)).plus(rec(Map(Y))).value)
    assertResult(ListMap(X,Y,Z))(rec(Map(X,Y,Z)).value)
    assertResult(ListMap(X,Y,Z))(rec(Map(X)).plus(rec(Map(Y,Z))).value)
    assertResult(ListMap(X,Y,Z))(rec(Map(X,Y)).plus(rec(Map(Z))).value)
    // backwards keys
    assertResult(ListMap(Y,X))(rec(Map(Y,X)).value)
    assertResult(ListMap(Y,X))(rec(Map(Y)).plus(rec(Map(X))).value)
    assertResult(ListMap(Z,Y,X))(rec(Map(Z,Y,X)).value)
    assertResult(ListMap(Z,Y,X))(rec(Map(Z)).plus(rec(Map(Y,X))).value)
    assertResult(ListMap(Z,Y,X))(rec(Map(Z,Y)).plus(rec(Map(X))).value)
    // overwrite orderings
    assertResult(ListMap(X,Y,Z))(rec(Map(X,Y)).plus(rec(Map(X,Z))).value) // TODO: determine overwrite order
  }

  test("rec value quantifiers"){
    assertResult(rec(X,Y).q(int(2)))(rec(X,Y).q(int(2)) ==> rec.q(int(2)))
    assertResult(rec(X,Y,Z).q(2))(rec(X,Y).q(int(2)) ==> rec.q(int(2)).plus(rec(Z)))
    assertResult(rec(X,Y,Z).q(2))(rec(X).q(int(2)) ==> rec.q(int(2)).plus(rec(Y)).plus(rec(Z).q(34)))
    assertResult(rec(X,Y,Z).q(4))(rec(X).q(int(2)) ==> rec.q(int(2)).plus(rec(Y)).plus(rec(Z).q(34)).q(2))
    // assertResult(int(14).q(4))(int(3).q(int(2)) ==> int.q(int(2)).plus(int(4)).q(2).mult(int(2).q(34)).q(3))
  }
}
