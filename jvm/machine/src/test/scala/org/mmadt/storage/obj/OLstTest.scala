package org.mmadt.storage.obj

import org.mmadt.language.mmlang.mmlangScriptEngineFactory
import org.mmadt.language.obj.Obj._
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Int, Lst, Obj}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2, TableFor4}

class OLstTest extends FunSuite with TableDrivenPropertyChecks {

  test("lst test") {
    assert(("a" | "b").q(0).test(str.q(0)))
    //
    assert(("a" | "b").test("a" | "b"))
    //assert(("a" | "b").test("a" |))
    assert(!("a" |).test("a" | "b"))
    //
    assert(("a" | ("b" | "c")).test("a" | ("b" | "c")))
    //assert(("a" | ("b" | "c")).test("a" | ("b" |)))
    assert(!("a" | ("b" |)).test("a" | ("b" | "c")))
  }

  test("basic poly") {
    assertResult(str("a"))(("a" | "b" | "c").head())
    assertResult("b" | "c")(("a" | "b" | "c").tail())

    assertResult(str("a"))(("a" `;` "b" `;` "c").head())
    assertResult("b" `;` "c")(("a" `;` "b" `;` "c").tail())
  }

  test("parallel expressions") {
    val starts: TableFor2[Obj, Obj] =
      new TableFor2[Obj, Obj](("expr", "result"),
        (int(1).-<(int `,` int), int(1) `,` int(1)),
        (int(1).-<(int `,` int.plus(2)), int(1) `,` int(3)),
        (int(1).-<(int `,` int.plus(2).q(10)), int(1) `,` int(3).q(10)),
        (int(1).q(5).-<(int `,` int.plus(2).q(10)), int(1).q(5) `,` int(3).q(50)),
        (int(1).q(5).-<(int `,` int.plus(2).q(10)) >-, int(int(1).q(5), int(3).q(50))),
        (int(int(1), int(100)).-<(int | int) >-, int(int(1), int(100))),
        (int(int(1), int(100)).-<(int `,` int) >-, int(1, 1, 100, 100)),
        (int(int(1), int(100)).-<(int `,` int) >-, int(int(1).q(2), int(100).q(2))),
        (int(int(1).q(5), int(100)).-<(int `,` int.plus(2).q(10)) >-, int(int(1).q(5), int(3).q(50), int(100), int(102).q(10))),
        (int(int(1).q(5), int(100)).-<(int | int.plus(2).q(10)) >-, int(int(1).q(5), int(100))),
        //(int(int(1), int(2)).-<(int | (int -< (int | int))), strm(List(int(1)|, int(2)|))),
        //(int(int(1), int(2)).-<(int `,` (int -< (int | int))), strm[Obj](List(int(1), int(1) |, int(2), int(2) |))),
        (int(1) -< (str | int), zeroObj | int(1)),
        //(strm(List(int(1), str("a"))).-<(str | int), strm(List(zeroObj | int(1), str("a") | zeroObj))),
      )
    forEvery(starts) { (query, result) => {
      //assertResult(result)(new mmlangScriptEngineFactory().getScriptEngine.eval(s"${query}"))
      assertResult(result)(query)
    }
    }
  }


  test("parallel [tail][head][last] values") {
    val starts: TableFor2[Lst[Obj], List[Obj]] =
      new TableFor2[Lst[Obj], List[Obj]](("parallel", "projections"),
        (|, List.empty),
        ("a" |, List(str("a"))),
        ("a" | "b", List(str("a"), str("b"))),
        ("a" | "b" | "c", List(str("a"), str("b"), str("c"))),
        ("a" | ("b" | "d") | "c", List(str("a"), "b" | "d", str("c"))),
      )
    forEvery(starts) { (alst, blist) => {
      assertResult(alst.glist)(blist)
      if (blist.nonEmpty) {
        assertResult(alst.last())(blist.last)
        assertResult(alst.head())(blist.head)
        assertResult(alst.g._2.head)(blist.head)
        assertResult(alst.g._2.last)(blist.last)
        assertResult(alst.tail().g._2)(blist.tail)
        assertResult(alst.g._2.tail)(blist.tail)
      }
    }
    }
  }

  test("scala type constructor") {
    assertResult("('a'|'b')")(("a" | "b").toString())
  }

  test("parallel [get] values") {
    assertResult(str("a"))((str("a") |).get(0))
    assertResult(str("b"))((str("a") | "b").get(1))
    assertResult(str("b"))((str("a") | "b" | "c").get(1))
    assertResult("b" | "d")(("a" | ("b" | "d") | "c").get(1))
  }

  test("parallel [get] types") {
    assertResult(str)((str.plus("a") | str).get(0, str).range)
  }

  test("parallel structure") {
    val poly: Lst[Obj] = int.mult(8).split(__.id() | __.plus(2) | 3).asInstanceOf[Lst[Obj]]
    assertResult("(int[id]|int[plus,2]|3)<=int[mult,8]-<(int[id]|int[plus,2]|3)")(poly.toString)
    assertResult(int.id())(poly.glist.head)
    assertResult(int.plus(2))(poly.glist(1))
    assertResult(int(3))(poly.glist(2))
    assertResult(int)(poly.glist.head.via._1)
    assertResult(int)(poly.glist(1).via._1)
    assert(poly.glist(2).root)
    assertResult(int.id() | int.plus(2) | int(3))(poly.range)
  }

  test("parallel quantifier") {
    val poly: Lst[Obj] = int.q(2).mult(8).split(__.id() | __.plus(2) | 3).asInstanceOf[Lst[Obj]]
    assertResult("(int{2}[id]|int{2}[plus,2]|3)<=int{2}[mult,8]-<(int{2}[id]|int{2}[plus,2]|3)")(poly.toString)
    assertResult(int.q(2).id())(poly.glist.head)
    assertResult(int.q(2).plus(2))(poly.glist(1))
    assertResult(int(3))(poly.glist(2))
    assertResult(int.q(2))(poly.glist.head.via._1)
    assertResult(int.q(2))(poly.glist(1).via._1)
    assert(poly.glist(2).root)
    assertResult(int.q(2).id() | int.q(2).plus(2) | int(3))(poly.range)
  }

  test("parallel [split] quantification") {
    assertResult(int)(int.mult(8).split(__.id() | __.plus(8).mult(2) | int(56)).merge[Int].id().isolate)
    assertResult(int.q(1, 20))(int.mult(8).split(__.id().q(10, 20) | __.plus(8).mult(2).q(2) | int(56)).merge[Int].id().isolate)
    assertResult(int.q(1, 40))(int.q(2).mult(8).q(1).split(__.id().q(10, 20) | __.plus(8).mult(2).q(2) | int(56)).merge[Int].id().isolate)
    assertResult(int(56))(int.q(2).mult(8).q(0).split(__.id().q(10, 20) | __.plus(8).mult(2).q(2) | int(56)).merge[Obj].id().isolate)
  }

  test("serial value/type checking") {
    val starts: TableFor2[Lst[Obj], Boolean] =
      new TableFor2[Lst[Obj], Boolean](("serial", "isValue"),
        (`;`, true),
        ("a" `;` "b", true),
        ("a" `;` "b" `;` "c" `;` "d", true),
        (str `;` "b", false),
      )
    forEvery(starts) { (serial, bool) => {
      assertResult(bool)(serial.isValue)
    }
    }
  }

  test("serial [put]") {
    val starts: TableFor4[Lst[Obj], Int, Obj, Lst[Obj]] =
      new TableFor4[Lst[Obj], Int, Obj, Lst[Obj]](("serial", "key", "value", "newProd"),
        (`;`, 0, "a", "a" `;`),
        ("b" `;`, 0, "a", "a" `;` "b"),
        ("a" `;` "c", 1, "b", "a" `;` "b" `;` "c"),
        ("a" `;` "b", 2, "c", "a" `;` "b" `;` "c"),
        //(str("a")/"b", 2, str("c")/ "d", str("a")/ "b"/ (str("c")/ "d")),
        //
        //(`/x`, 0, str, (str /).via(/, PutOp[Int, Str](0, str))),
        //(/, int.is(int.gt(0)), "a", /[Obj].via(/, PutOp[Int, Str](int.is(int.gt(0)), "a"))),
      )
    forEvery(starts) { (serial, key, value, newProduct) => {
      assertResult(newProduct)(serial.put(key, value))
      assertResult(newProduct)(PutOp(key, value).exec(serial))
    }
    }
  }

}
