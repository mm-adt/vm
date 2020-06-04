package org.mmadt.processor.inst.trace
import org.mmadt.language.mmlang.mmlangScriptEngineFactory
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.__
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}

class DefineInstTest extends FunSuite with TableDrivenPropertyChecks {

  test("[define] value, type, strm, anon combinations") {
    val starts: TableFor2[Obj, Obj] =
      new TableFor2[Obj, Obj](("query", "result"),
        (int(2).define("abc", int.is(int.gt(0))).a(__.from("abc")), btrue),
        (int(-2).define("abc", int.is(int.gt(0))).a(__.from("abc")), bfalse),
        ((int(1) `,` (int(1) `,` 1)).define("abc", __.-<(__.is(__.eqs(1)) | (int(1) `,` __.from("abc"))) >-).a(__.from("abc")), btrue),
        ((int(1) `,` (int(1) `,` 2)).define("abc", __.-<(__.is(__.eqs(1)) | (int(1) `,` __.from("abc"))) >-).a(__.from("abc")), bfalse),
        ((int(1) `,` (int(2) `,` 1)).define("abc", __.-<(__.is(__.eqs(1)) | (int(1) `,` __.from("abc"))) >-).a(__.from("abc")), bfalse),
        ((int(1) `,` (int(1) `,` 2)).define("abc", __.-<(__.is(__.a(int)) | (int(1) `,` __.from("abc"))) >-).a(__.from("abc")), btrue),
        ((int(1) `,` (int(1) `,` 2)).define("abc", __.`[`(__.is(__.a(int)) | (int `,` __.from("abc"))) `]`).a(__.from("abc")), btrue),
        ((int(1) `,` (int(1) `,` (int(2) `,` 3))).define("abc", __.`[`(__.is(__.a(int)) | (int `,` __.from("abc"))) `]`).a(__.from("abc")), btrue),
        ((int(1) `,` (int(1) `,` (int(2) `,` 3))).define("abc", __.`[`(__.is(__.lt(2)) | (int `,` __.from("abc"))) `]`).a(__.from("abc")), bfalse),
        ((int(1) `,` (int(1) `,` (int(2) `,` 3))).define("abc", __.`[`(__.is(__.lt(5)) | (int `,` __.from("abc"))) `]`).a(__.from("abc")), btrue)
      )
    forEvery(starts) { (query, result) => {
      assertResult(result)(new mmlangScriptEngineFactory().getScriptEngine.eval(s"${query}"))
      assertResult(result)(query)
    }
    }
  }
}
