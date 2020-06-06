package org.mmadt.processor.inst.trace
import org.mmadt.language.mmlang.mmlangScriptEngineFactory
import org.mmadt.language.obj.{Bool, Obj}
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}

class DefineInstTest extends FunSuite with TableDrivenPropertyChecks {

  test("[define] value, type, strm, anon combinations") {
    val starts: TableFor2[Obj, Obj] =
      new TableFor2[Obj, Obj](("query", "result"),
        (int(2).define("nat", int.is(int.gt(0))).a(__.named("nat")), btrue),
        (int(-2).define("nat", int.is(int.gt(0))).a(__.named("nat")), bfalse),
        (int(-2).define("nat", int.is(int.gt(0))).a(__.named("nat").plus(100)), bfalse),

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

  test("[define]") {
    println(int.define("nat",int.is(int.gt(0))).a(__.named("nat")))
    println(int(-10).define("nat",int.is(int.gt(0))).a(__.named("nat").plus(100)))
    println(__.named("nat").plus(100).domain)
    println(int(-10).compute(int.define("nat",int.is(int.gt(0))).a(__.named("nat")).asInstanceOf[Type[Bool]]))

    // 1<x>[plus,2]-<(x[plus,2],x)>-

    println(int.define("x",int.plus(10).mult(20)).plus(2)-<(__.named("x").plus(100)`,`__.named("x"))>-)
   println(new mmlangScriptEngineFactory().getScriptEngine.eval("1[a,[int|str]]"))
   println(str.a(__.-<(real `|` int)>-))
    // println(__.to("x").plus(2)-<(__.named("x").plus(2)`,`__.named("x"))>-)

  }
}
