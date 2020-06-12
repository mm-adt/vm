package org.mmadt.processor.inst.map
import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.op.map.JuxtaOp
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}

class JuxtaInstTest extends FunSuite with TableDrivenPropertyChecks {

  test("[juxta] value, type, strm, anon combinations") {
    val starts: TableFor2[List[Obj], Obj] =
      new TableFor2[List[Obj], Obj](("query", "result"),
        // value/value
        (List(int(1).q(5)), int(1).q(5)),
        (List(int(1), int(2), int(3)), int(3)),
        (List(int(1), int(2).q(10), int(3)), int(3).q(10)),
        (List(int(1), int(2).q(10), int(3).q(2)), int(3).q(20)),
        // value/type
        (List[Int](int(1), int.plus(1)), int(2)),
        (List[Int](int(1), int.plus(10)), int(11)),
        (List[Int](int(1), int.plus(int)), int(2)),
        (List[Int](int(1), int.plus(int.plus(2))), int(4)),
        (List[Obj](int(1), int.plus(int.plus(2)).as(str), str.plus("a")), str("4a")),
        (List[Int](int(1), int.plus(1).q(0)), int(2).q(qZero)),
        // type/value
        (List[Int](int.plus(1), int(1)), int(1)),
        (List[Str](str, str("marko")), str("marko")),
        (List[Real](real.plus(1.0).q(10), real(13.0).q(2)), real(13.0).q(20)),
        // type/type
        (List(str), str),
        (List(str, str.id()), str.id()),
        (List(int, int.plus(1), int.plus(2)), int.plus(1).plus(2)),
      )
    forEvery(starts) { (left, right) => {
      println(left.map(_.toString).reduce((a, b) => a + Tokens.juxt_op + b))
      // assertResult(right)(new mmlangScriptEngineFactory().getScriptEngine.eval(s"${left.map(_.toString).reduce((a, b) => a + "=>" + b)}"))
      assertResult(right)(left.reduce((a, b) => a `=>` b))
      assertResult(right)(left.reduce((a, b) => JuxtaOp(b).exec(a)))
    }
    }
  }
}
