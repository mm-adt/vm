package org.mmadt.processor.inst.branch

import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

class GivenInstTest extends FunSuite with TableDrivenPropertyChecks {


  test("[given] value, type, strm") {
    val check: TableFor3[Obj, Obj, Obj] =
      new TableFor3[Obj, Obj, Obj](("input", "type", "result"),
        (int(1), int.-<((int.plus(50).is(__.gt(0)) --> int.plus(20)) `,` (str --> str.plus("a"))), int(21) `,` zeroObj),
        (int(1), int.-<((int.plus(50).is(__.gt(0)) --> int.plus(20)) | (str --> str.plus("a"))), int(21) | zeroObj),
        (int(1), int.-<((int.plus(50).is(__.gt(0)) --> int.plus(20)) `,` (int.plus(-10).is(__.lt(0)) --> int.plus(100))), int(21) `,` 101),
        (int(1), int.-<((int.plus(50).is(__.gt(0)) --> int.plus(20)) | (int.plus(-10).is(__.lt(0)) --> int.plus(100))), int(21) | zeroObj),
        (int(1), int.-<((int.plus(50).is(__.lt(0)) --> int.plus(20)) `,` (int.plus(-10).is(__.lt(0)) --> int.plus(100))), zeroObj `,` 101),
        (int(-1), int.plus(2).-<(int.is(int > 5) --> int(34) | int.is(int === 1) --> int.plus(2) | int --> int(20)), zeroObj | 3 | zeroObj),
        (int(10, int(50).q(2), 60), int.q(4).-<(bool --> btrue | int --> int + 1), strm(List(zeroObj | int(11), zeroObj | int(51).q(2), zeroObj | int(61)))),
        (int(10, int(50).q(2), 60), int.q(4).-<(bool --> btrue | int --> int + 1).>-, int(int(11), int(51).q(2), int(61))),
      )
    forEvery(check) { (input, atype, result) => {
      assertResult(result)(input.compute(atype.asInstanceOf[Type[Obj]]))
      assertResult(result)(input ==> atype.asInstanceOf[Type[Obj]])
      assertResult(result)(input ===> atype)
      assertResult(result)(input ===> (input.range ==> atype.asInstanceOf[Type[Obj]]))
      assertResult(result)(input ===> (input.range ===> atype))
    }
    }
  }
}