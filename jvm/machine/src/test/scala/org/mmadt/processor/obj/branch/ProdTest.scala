package org.mmadt.processor.obj.branch

import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.branch.Brch
import org.mmadt.language.obj.op.map.MultBOp
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}

class ProdTest extends FunSuite with TableDrivenPropertyChecks {

  test("product value/type checking") {
    val starts: TableFor2[Brch[Obj], Boolean] =
      new TableFor2[Brch[Obj], Boolean](("prod", "isValue"),
        (prod(), true),
        (prod("a", "b"), true),
        (prod("a", "b", "c", "d"), true),
        (prod[Obj]("a", "b").mult(prod("c", "d")), true),
        (MultBOp[Obj](prod("c", "d")).exec(prod("a", "b")), true),
        (prod(str, "b"), false),
      )
    forEvery(starts) { (prod, bool) => {
      assertResult(bool)(prod.isValue)
    }
    }
  }

}
