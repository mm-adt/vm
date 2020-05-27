package org.mmadt.storage.obj
import org.mmadt.language.Tokens
import org.mmadt.language.obj.Str
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.value.{IntValue, StrValue}
import org.mmadt.storage.StorageFactory._
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
    assertResult(bfalse)(rec(int(1) -> btrue) ===> rec.plus(rec(int(2) -> bfalse)).get(int(2)))
    assertResult(rec(int(1) -> btrue, int(2) -> bfalse))(rec(int(1) -> btrue) ==> rec.plus(rec(int(2) -> bfalse)))
    assertResult(btrue)(rec(int(1) -> btrue, int(2) -> bfalse).get(int(1)))
    assertResult(bfalse)(rec(int(1) -> btrue, int(2) -> bfalse).get(int(2)))
    //intercept[LanguageException] {
    assertResult(zeroObj)(rec(int(1) -> btrue, int(2) -> bfalse).get(int(3)))
    //}
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

  test("rec choose branching") {
    assertResult(str("name", "my", "is"))(int(1, 2, 3).split(rec(Tokens.`|`, Map(int.is(int.eqs(2)) -> str("name"), int.is(int.gt(2)) -> str("is"), int -> str("my")))).merge[Str])
    assertResult(false)(rec(Tokens.`|`, Map(str("a") -> int(1), str("b") -> int(2))).equals(rec(Tokens.`|`, Map(str("b") -> int(2), str("a") -> int(1)))))
    assertResult(bfalse)(rec(Tokens.`|`, Map(str("a") -> int(1), str("b") -> int(2))).eqs(rec(Tokens.`|`, Map(str("b") -> int(2), str("a") -> int(1)))))
  }

  test("record value/type checking") {
    val extra = rec(str("name") -> str, __ -> str, __ -> int)
    val extraLess = rec(__ -> __)
    val markoLess = rec(str("name") -> str("marko"))
    val marko = rec(str("name") -> str("marko"), str("age") -> int(29))
    val markoMore = rec(str("name") -> str("marko"), str("age") -> int(29), str("alive") -> bfalse)
    val person = rec(str("name") -> str, str("age") -> int)
    val personLess = rec(str("age") -> int)
    val markoLessName = rec(str("name") -> str("marko")).named("person")
    val markoName = rec(str("name") -> str("marko"), str("age") -> int(29)).named("person")
    val markoMoreName = rec(str("name") -> str("marko"), str("age") -> int(29), str("alive") -> bfalse).named("person")
    val personName = rec(str("name") -> str, str("age") -> int).named("person")
    val personNameBackwards = rec(str("age") -> int, str("name") -> str).named("person")
    val personBackwards = rec(str("age") -> int, str("name") -> str)
    assert(marko.test(marko))
    assert(markoMore.test(markoMore))
    assert(markoLess.test(markoLess))
    assert(!markoLess.test(markoMore))
    assert(markoMore.test(markoLess))
    assert(markoName.test(marko))
    assert(markoMoreName.test(markoMore))
    assert(markoLessName.test(markoLess))
    assert(markoLess.test(rec))
    assert(marko.test(rec))
    assert(markoMore.test(rec))
    assert(markoLessName.test(rec))
    assert(markoName.test(rec))
    assert(markoMoreName.test(rec))
    assert(!markoLess.test(person))
    assert(marko.test(person))
    assert(markoMore.test(person))
    assert(!markoLessName.test(personName))
    assert(markoName.test(personName))
    assert(markoMoreName.test(personName))
    assert(!markoLessName.test(person))
    assert(markoName.test(person))
    assert(markoMoreName.test(person))
    assert(!markoLess.test(personName))
    assert(marko.test(personName))
    assert(markoMore.test(personName))
    assert(person.test(personName))
    assert(personName.test(personName))
    assert(personName.test(person))
    assert(person.test(personLess))
    assert(!personLess.test(person))
    assert(personLess.test(rec))
    assert(personName.test(personNameBackwards))
    assert(personNameBackwards.test(personName))
    assert(personName.test(personBackwards))
    assert(personBackwards.test(person))
    assert(marko.test(extra))
    assert(marko.test(extraLess))
    assert(!extra.test(marko))
    assert(!extraLess.test(marko))
    assert(!rec.test(personLess))
    assert(!rec.test(person))
    assert(rec.test(rec))
    assert(rec.test(rec))
  }
}
