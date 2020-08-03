package org.mmadt.language.model

import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmTest extends FunSuite {
  val file1: String = str(getClass.getResource("/model/mm.mm").getPath).g

  test("mm-ADT int") {
    assertResult(int.plus(1))(int ==> int.model(file1).plus(1))
    assertResult(int)(int ==> int.model(file1).plus(0))
    assertResult(int)(int ==> int.model(file1).mult(1))
    assertResult(int(0))(int ==> int.model(file1).mult(0))
    assertResult(int.plus(7))(int ==> int.model(file1).plus(7).neg().neg().plus(0))
  }
}
