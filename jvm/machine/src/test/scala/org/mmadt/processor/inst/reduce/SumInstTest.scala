package org.mmadt.processor.inst.reduce

import org.mmadt.storage.StorageFactory.{*, +, int}
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class SumInstTest  extends FunSuite {
  test("[sum] w/ int") {
    assertResult(int(2))(int(2).sum())
    assertResult(int(20))(int(2).q(10).sum())
    assertResult(int(12))(int(12) ==> int.sum())
    assertResult(int(0))(int(1) ==> int.is(int.gt(10)).sum())
    assertResult(int(0))(int(1, 2, 3) ==> int.q(*).is(int.gt(10)).sum())
    assertResult(int(6))(int(1, 2, 3).sum())
    assertResult(int(6))(int(1, 2, 3) ==> int.q(3).sum())
    assertResult(int(36))(int(1, 2, 3) ==> int.q(+).plus(10).sum())
    assertResult(int(133))(int(int(0).q(10), int(1).q(3)).plus(10).sum())
    assertResult(int(149))(int(int(0).q(10), int(1).q(3), 6).plus(10).sum())
    // assertResult(int(14))(int(int(0).q(10),int(1).q(3),6) ===> int.q(*).plus(10).count())
  }
}