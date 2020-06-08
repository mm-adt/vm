package org.mmadt.processor.inst.trace

import org.mmadt.language.mmlang.mmlangScriptEngineFactory
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.trace.AsOp
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

class AsInstTest extends FunSuite with TableDrivenPropertyChecks {
  test("[as] w/ values") {
    val check: TableFor3[Obj, Obj, Obj] =
      new TableFor3[Obj, Obj, Obj](("lhs", "rhs", "result"),
        // bool
        (true, __, true),
        (true, str, "true"),
        (true, str.plus("dat"), "truedat"),
        (true, str.as("false"), "false"),
        // int
        (3, __, 3),
        (3, __.mult(3), 9),
        (3, int, 3),
        (3, int.plus(3), 6),
        (3, int.gt(10), false),
        (3, __.plus(10), 13),
        (3, str, "3"),
        (3, str.plus("a"), "3a"),
        // real
        (4.0, __, 4.0),
        (4.0, real, 4.0),
        (4.0, real.plus(1.0), 5.0),
        (4.0, real.gt(2.0), true),
        (4.0, __.mult(3.0), 12.0),
        (4.0, int, 4),
        (4.0, int.plus(2), 6),
        // str
        ("3", str.plus("a"), "3a"),
        ("3", int, 3),
        ("3", int.plus(10), 13),
        ("3", real, 3.0),
        ("3", str, "3"),
        ("true", bool, true),
        ("false", bool, false),
        // lst
        ((int(1) `,` 2 `,` 3), (__), (int(1) `,` 2 `,` 3)),
        ((int(1) `,` 2 `,` 3), str, "(1,2,3)"),
        ((int(1) `,` 2 `,` 3), (str `,` real), (str("1") `,` real(2.0))),
        ((int(1) `,` 2 `,` 3), (__.plus(1) `,` __.plus(2) `,` __.plus(3)), (int(2) `,` 4 `,` 6)),
        ((int(1) `,` 2 `,` 3), (int(8) `,` 9 `,` 10), (int(8) `,` 9 `,` 10)),
        // rec
        // (rec(str("a") -> int(10)), __.get("a").gt(3), btrue)
      )
    forEvery(check) { (left, right, result) => {
      if (!left.isInstanceOf[Strm[_]])
        assertResult(result)(new mmlangScriptEngineFactory().getScriptEngine.eval(s"${left}[as,${right}]"))
      //else
      //assertResult(result)(new mmlangScriptEngineFactory().getScriptEngine.eval(s"(${left})[a,${right}]"))
      //assertResult(result)(left.compute(asType(__.as(right))))
      assertResult(result)(left.as(right))
      assertResult(result)(AsOp(right).exec(left))
      assertResult(result)(left ===> left.range.as(right))
      assertResult(result)(left ===> (left.range ===> left.range.as(right)))
    }
    }

  }
}
/*
  test("int[as,rec]") {
    assertResult(rec(str("age") -> int(5)))(int(5) ===> int.as(rec(str("age") -> int)))
    assertResult(rec(str("X") -> int(5), str("Y") -> int(15)))(int(5) ===> int.to("x").plus(10).to("y").as(rec(str("X") -> int.from("x"), str("Y") -> int.from("y"))))
    assertResult(str("14hello"))(int(5) ===> int.plus(2).mult(2).as(str).plus("hello"))
  }
 */