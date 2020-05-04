package org.mmadt.storage.obj

import org.mmadt.language.mmlang.mmlangScriptEngineFactory
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.map.MultOp
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.{Int, Obj, Poly, Str}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2, TableFor4}

class OPolyTest extends FunSuite with TableDrivenPropertyChecks {

  test("basic poly") {
    assertResult(str("a"))(`|`[Str]("a", "b", "c").head())
    assertResult(`|`[Str]("b", "c"))(`|`[Str]("a", "b", "c").tail())

    assertResult(str("a"))(`;`[Str]("a", "b", "c").head())
    assertResult(`;`[Str]("b", "c"))(`;`[Str]("a", "b", "c").tail())
  }

  test("parallel expressions") {
    val starts: TableFor2[Obj, Obj] =
      new TableFor2[Obj, Obj](("expr", "result"),
        (int(1).-<(`|`(int, int)), `|`(int(1), int(1))),
        (int(1).-<(`|`(int, int.plus(2))), `|`(int(1), int(3))),
        (int(1).-<(`|`(int, int.plus(2).q(10))), `|`(int(1), int(3).q(10))),
        (int(1).q(5).-<(`|`(int, int.plus(2).q(10))), `|`(int(1).q(5), int(3).q(50))),
        (int(1).q(5).-<(`|`(int, int.plus(2).q(10))) >-, int(int(1).q(5), int(3).q(50))),
        // (int(int(1), int(100)).-<(coprod(int, int)) >-, int(int(1), int(1), int(100), int(100))),
        // (int(int(1).q(5), int(100)).-<(coprod(int, int.plus(2).q(10))) >-, int(int(1).q(5), int(3).q(50), int(100), int(102).q(10))),
        // (int(int(1), int(2)).-<(coprod(int, int -< (coprod(int, int)))), coprod(strm(List(int(1), int(2))), strm(List(coprod(int(1), int(1)), coprod(int(2), int(2)))))),
        //  (int(1) -< `|`(str, int), `|`(obj.q(0), int(1))),
        // (strm(List(int(1), str("a"))) -< `|`(str, int), strm(List(`|`(obj.q(0), int(1)), `|`(str("a"), obj.q(0))))),
      )
    forEvery(starts) { (query, result) => {
      println(s"${query}")
      assertResult(result)(new mmlangScriptEngineFactory().getScriptEngine.eval(s"${query}"))
      assertResult(result)(query)
    }
    }
  }


  /*test("parallel [tail][head] values") {
    val starts: TableFor2[Poly[Obj], List[Value[Obj]]] =
      new TableFor2[Poly[Obj], List[Value[Obj]]](("parallel", "projections"),
        (`|`(), List.empty),
        (`|`("a"), List(str("a"))),
        (`|`("a", "b"), List(str("a"), str("b"))),
        (`|`("a", "b", "c"), List(str("a"), str("b"), str("c"))),
        (`|`("a", `|`[Str]("b", "d"), "c"), List(str("a"), `|`[Str]("b", "d"), str("c"))),
      )
    forEvery(starts) { (alst, blist) => {
      assertResult(alst.groundList)(blist)
      assertResult(alst.groundList)(vlst[Value[Obj]](ground = blist).ground)
      if (blist.nonEmpty) {
        assertResult(alst.head())(blist.head)
        assertResult(alst.ground._2.head)(blist.head)
        assertResult(alst.tail().ground._2)(blist.tail)
        assertResult(alst.ground._2.tail)(blist.tail)
      }
    }
    }
  }*/

  test("parallel keys") {
    assertResult("[name->'marko'|age->29]")(`|`("name" -> str("marko"), "age" -> int(29)).toString)
  }

  test("parallel [get] values") {
    assertResult(str("a"))(`|`[Str]("a").get(0))
    assertResult(str("b"))(`|`[Str]("a", "b").get(1))
    assertResult(str("b"))(`|`[Str]("a", "b", "c").get(1))
    assertResult(`|`[Str]("b", "d"))(`|`[Obj]("a", `|`[Str]("b", "d"), "c").get(1))
    // assertResult(prod[Str]("b", "d"))(prod[Obj]("a", prod[Str]("b", "d"), "c").get(1,prod()).get(0))
  }

  test("parallel [get] types") {
    assertResult(str)(`|`[Str](str.plus("a"), str).get(0, str).range)
  }

  test("parallel structure") {
    val poly = int.mult(8).split(`|`[Obj](__.id(), __.plus(2), 3))
    assertResult("[int[id]|int[plus,2]|3]<=int[mult,8]-<[int[id]|int[plus,2]|3]")(poly.toString)
    assertResult(int.id())(poly.groundList(0))
    assertResult(int.plus(2))(poly.groundList(1))
    assertResult(int(3))(poly.groundList(2))
    assertResult(int)(poly.groundList(0).via._1)
    assertResult(int)(poly.groundList(1).via._1)
    assert(poly.groundList(2).root)
    assertResult(`|`[Int](int.id(), int.plus(2), int(3)))(poly.range)
  }

  test("parallel quantifier") {
    val poly = int.q(2).mult(8).split(`|`[Obj](__.id(), __.plus(2), 3))
    assertResult("[int{2}[id]|int{2}[plus,2]|3]<=int{2}[mult,8]-<[int{2}[id]|int{2}[plus,2]|3]")(poly.toString)
    assertResult(int.q(2).id())(poly.groundList(0))
    assertResult(int.q(2).plus(2))(poly.groundList(1))
    assertResult(int(3))(poly.groundList(2))
    assertResult(int.q(2))(poly.groundList(0).via._1)
    assertResult(int.q(2))(poly.groundList(1).via._1)
    assert(poly.groundList(2).root)
    assertResult(`|`[Int](int.q(2).id(), int.q(2).plus(2), int(3)))(poly.range)
  }

  test("parallel [split] quantification") {
    assertResult(int.q(0, 3))(int.mult(8).split(`|`(__.id(), __.plus(8).mult(2), int(56))).merge[Int].id().isolate)
    assertResult(int.q(0, 23))(int.mult(8).split(`|`(__.id().q(10, 20), __.plus(8).mult(2).q(2), int(56))).merge[Int].id().isolate)
    assertResult(int.q(0, 45))(int.q(2).mult(8).q(1).split(`|`(__.id().q(10, 20), __.plus(8).mult(2).q(2), int(56))).merge[Int].id().isolate)
    // assertResult(__)(int.q(2).mult(8).q(0).split(prod(__.id().q(10, 20), __.plus(8).mult(2).q(2), int(56))).merge[Obj]().id().isolate)
  }

  test("serial value/type checking") {
    val starts: TableFor2[Poly[Obj], Boolean] =
      new TableFor2[Poly[Obj], Boolean](("serial", "isValue"),
        (`;`(), true),
        (`;`("a", "b"), true),
        (`;`("a", "b", "c", "d"), true),
        //(`;`[Obj]("a", "b").mult(prod[Obj]("c", "d")), true),
        //(MultOp[Poly[Obj]](`;`[Obj]("c", "d")).exec(`;`[Obj]("a", "b")), true),
        (`;`(str, "b"), false),
      )
    forEvery(starts) { (serial, bool) => {
      assertResult(bool)(serial.isValue)
    }
    }
  }

  test("serial [put]") {
    val starts: TableFor4[Poly[Obj], Int, Obj, Poly[Obj]] =
      new TableFor4[Poly[Obj], Int, Obj, Poly[Obj]](("serial", "key", "value", "newProd"),
        (`;`(), 0, "a", `;`("a")),
        (`;`("b"), 0, "a", `;`("a", "b")),
        (`;`("a", "c"), 1, "b", `;`("a", "b", "c")),
        (`;`("a", "b"), 2, "c", `;`("a", "b", "c")),
        (`;`("a", "b"), 2, `;`[Str]("c", "d"), `;`("a", "b", `;`[Str]("c", "d"))),
        //
        (`;`(), 0, str, `;`[Obj](str).via(`;`(), PutOp[Int, Str](0, str))),
        (`;`(), int.is(int.gt(0)), "a", `;`().via(`;`(), PutOp[Int, Str](int.is(int.gt(0)), "a"))),
      )
    forEvery(starts) { (serial, key, value, newProduct) => {
      assertResult(newProduct)(serial.put(key, value))
      assertResult(newProduct)(PutOp(key, value).exec(serial))
    }
    }
  }

  test("serial poly value/type checking") {
    val starts: TableFor2[Poly[Obj], Boolean] =
      new TableFor2[Poly[Obj], Boolean](("serial", "isValue"),
        (`;`(), true),
        (`;`("a", "b"), true),
        (`;`("a", "b", "c", "d"), true),
        (`;`[Obj]("a", "b").mult(`;`[Obj]("c", "d")), true),
        (MultOp[Poly[Obj]](`;`[Obj]("c", "d")).exec(`;`[Obj]("a", "b")), true),
        (`;`(str, "b"), false),
      )
    forEvery(starts) { (serial, bool) => {
      assertResult(bool)(serial.isValue)
    }
    }
  }

  test("serial poly [put]") {
    val starts: TableFor4[Poly[Obj], Int, Obj, Poly[Obj]] =
      new TableFor4[Poly[Obj], Int, Obj, Poly[Obj]](("serial", "key", "value", "newProd"),
        (`;`(), 0, "a", `;`("a")),
        (`;`("b"), 0, "a", `;`("a", "b")),
        (`;`("a", "c"), 1, "b", `;`("a", "b", "c")),
        (`;`("a", "b"), 2, "c", `;`("a", "b", "c")),
        (`;`("a", "b"), 2, `;`[Str]("c", "d"), `;`("a", "b", `;`[Str]("c", "d"))),
        //
        (`;`(), 0, str, `;`[Obj](str).via(`;`(), PutOp[Int, Str](0, str))),
        (`;`(), int.is(int.gt(0)), "a", `;`().via(`;`(), PutOp[Int, Str](int.is(int.gt(0)), "a"))),
      )
    forEvery(starts) { (serial, key, value, newProduct) => {
      assertResult(newProduct)(serial.put(key, value))
      assertResult(newProduct)(PutOp(key, value).exec(serial))
    }
    }
  }

}
