package org.mmadt.storage.obj
import org.mmadt.language.obj.value.{IntValue, StrValue}
import org.mmadt.storage.StorageFactory.{bfalse, bool, btrue, int, rec, str}
import org.scalatest.FunSuite

import scala.collection.immutable.ListMap

class ORecTest extends FunSuite {

  val X: (IntValue, StrValue) = int(1) -> str("a")
  val Y: (IntValue, StrValue) = int(2) -> str("b")
  val Z: (IntValue, StrValue) = int(3) -> str("c")

  test("rec value toString") {
    assertResult("[->]")(rec.toString)
  }

  test("rec values") {
    assertResult("[1->true]")(rec(int(1) -> btrue).toString)
    assertResult("[1->true,2->false]")(rec(int(1) -> btrue, int(2) -> bfalse).toString)
    assertResult("[1->true,2->false]")(rec(int(1) -> btrue).plus(rec(int(2) -> bfalse)).toString)
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

  test("rec value quantifiers") {
    assertResult(rec(X, Y).q(int(2)))(rec(X, Y).q(int(2)) ==> rec.q(int(2)))
    assertResult(rec(X, Y, Z).q(2))(rec(X, Y).q(int(2)) ==> rec.q(int(2)).plus(rec(Z)))
    assertResult(rec(X, Y, Z).q(2))(rec(X).q(int(2)) ==> rec.q(int(2)).plus(rec(Y)).plus(rec(Z).q(34)))
    assertResult(rec(X, Y, Z).q(4))(rec(X).q(int(2)) ==> rec.q(int(2)).plus(rec(Y)).plus(rec(Z).q(34)).q(2))
  }
}
