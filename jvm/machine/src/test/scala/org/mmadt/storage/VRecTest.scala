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

package org.mmadt.storage

import org.mmadt.storage.obj._
import org.scalatest.FunSuite

import scala.collection.immutable.ListMap

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VRecTest extends FunSuite {

  test("rec values"){
    assertResult("[1:true]")(rec(int(1) -> btrue).toString)
    assertResult("[1:true,2:false]")(rec(int(1) -> btrue,int(2) -> bfalse).toString)
    assertResult("[1:true,2:false]")(rec(int(1) -> btrue).plus(rec(int(2) -> bfalse)).toString)
    //println(rec.plus(rec(int(2) -> bfalse)).get(int(2), bool))
    assertResult(bfalse)(rec(int(1) -> btrue) ==> rec.plus(rec(int(2) -> bfalse)).get(int(2),bool))
    assertResult(rec(int(1) -> btrue,int(2) -> bfalse))(rec(int(1) -> btrue) ==> rec.plus(rec(int(2) -> bfalse)))
    assertResult(btrue)(rec(int(1) -> btrue,int(2) -> bfalse).get(int(1)))
    assertResult(bfalse)(rec(int(1) -> btrue,int(2) -> bfalse).get(int(2)))
    assertResult(int(6))(rec(int(1) -> int.plus(5),int(2) -> int.mult(100)).get(int(1)))
    assertResult(int(200))(rec(int(1) -> int.plus(5),int(2) -> int.mult(100)).get(int(2)))
    assertResult(int(200))(rec(int(1) -> int.plus(5),int(2) -> int.mult(100)) ==> rec.get(int(2),int))
    intercept[NoSuchElementException]{
      rec(int(1) -> btrue,int(2) -> bfalse).get(int(3))
    }
  }

  test("rec value via sequence construction"){
    val X = int(1) -> str("a")
    val Y = int(2) -> str("b")
    val Z = int(3) -> str("c")

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
    val X = int(1) -> str("a")
    val Y = int(2) -> str("b")
    val Z = int(3) -> str("c")

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

}
