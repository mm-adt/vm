package org.mmadt.storage.obj
import org.mmadt.storage.StorageFactory.int
import org.scalatest.FunSuite

class OInstTest extends FunSuite {
  test("type instructions") {
    assert(int.plus(1).mult(2).test(int.plus(1).mult(2)))
    assert(int(10).test(int.from("x", int)))
    assert(int.plus(10).test(int.plus(int.from("x", int))))
    assert(int.plus(10).test(int.plus(int.from("x", int).plus(2))))
    assert(!int.plus(10).test(int.plus(int.from("x", int).plus(2)).mult(20)))
  }
}
