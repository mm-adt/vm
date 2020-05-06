package org.mmadt.processor.inst.branch

import org.mmadt.storage.StorageFactory._
import org.mmadt.language.obj.Int
import org.scalatest.FunSuite
import org.scalatest.prop.TableDrivenPropertyChecks

class CombineInstTest extends FunSuite with TableDrivenPropertyChecks {

  test("basic [combine]") {
    assertResult(int(4)| 40)((int(2)| 4).combine(int.plus(2)| int.mult(10)))
  }

}
