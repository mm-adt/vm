package org.mmadt.processor.inst.branch

import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

class GivenInstTest extends FunSuite with TableDrivenPropertyChecks {


  test("[given] value, type, strm") {
    val check: TableFor3[Obj, Type[Obj], Obj] =
      new TableFor3[Obj, Type[Obj], Obj](("input", "type", "result"),
        (int(1), int.-<((int.plus(50).is(__.gt(0)) --> int.plus(20)) | (str --> str.plus("a"))), int(21) | zeroObj),
        (int(1), int.-<((int.plus(50).is(__.gt(0)) --> int.plus(20)) | (int.plus(-10).is(__.lt(0)) --> int.plus(100))), int(21) | 101),
      )
    forEvery(check) { (input, atype, result) => {
      assertResult(result)(input.compute(atype))
      assertResult(result)(input ==> atype)
      assertResult(result)(input ===> atype)
      assertResult(result)(input ===> (input.range ==> atype))
    }
    }
  }
}