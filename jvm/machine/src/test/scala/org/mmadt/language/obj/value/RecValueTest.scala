package org.mmadt.language.obj.value

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Rec
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.TableDrivenPropertyChecks

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class RecValueTest extends FunSuite with TableDrivenPropertyChecks {

  test("rec value [split]/[merge]") {
    val crec: Rec[StrValue, IntValue] = rec(g = (Tokens.`,`, Map(str("a") -> int(1), str("b") -> int(2), str("c") -> int(3))))
    val prec: Rec[StrValue, IntValue] = rec(g = (Tokens.`|`, Map(str("a") -> int(1), str("b") -> int(2), str("c") -> int(3))))
    val srec: Rec[StrValue, IntValue] = rec(g = (Tokens.`;`, Map(str("a") -> int(1), str("b") -> int(2), str("c") -> int(3))))

    assertResult(int(1, 2, 3))(crec.merge)
    assertResult(int(1))(prec.merge)
    assertResult(int(3))(srec.merge)

    assertResult(int(1, 2, 3))(int(10).split(crec).merge)
    assertResult(int(1))(int(10).split(prec).merge)
    assertResult(int(3))(int(10).split(srec).merge)
  }

}
