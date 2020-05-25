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
