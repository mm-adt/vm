/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.processor.inst.map

import org.mmadt.language.mmlang.mmlangScriptEngineFactory
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.Obj
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil._
import org.mmadt.storage.StorageFactory._
import org.scalatest.prop.TableFor3

class AInstTest extends BaseInstTest(

  testSet("[a] w/ values",
    comment("bool"),
    testing(btrue, __.a(btrue), btrue),
    testing(btrue, __.a(btrue.q(1)), btrue),
    testing(btrue, __.a(btrue.q(2)), bfalse),
    testing(btrue, __.a(btrue.q(?)), btrue),
    testing(btrue.q(2), __.a(btrue.q(?)), bfalse.q(2)),
    testing(btrue.q(2), __.a(btrue.q(1, 3)), btrue.q(2)),
    testing(btrue.q(2), __.a(btrue.q(2)), btrue.q(2)),
    testing(btrue, __.a(bfalse), bfalse),
    testing(btrue, __.a(bool.is(bool.eqs(bfalse))), bfalse),
    testing(btrue, __.a(bool.is(bool.eqs(btrue))), btrue),
    testing(btrue, __.a(bool), btrue),
    testing(btrue, __.a(int), bfalse),
    testing(btrue, __.a(real), bfalse),
    testing(btrue, __.a(str), bfalse),
    testing(btrue, __.a(str.is(__.gt("a"))), bfalse),
    testing(btrue, __.a(rec), bfalse, false),
    testing(bool, __.a(btrue), bool.a(btrue)),
    comment("int"),
    testing(int(20), __.a(int(20)), btrue),
    testing(int(20), __.a(int(30)), bfalse),
    testing(int(20), __.a(int.is(__.gt(10))), btrue),
    testing(int(20), __.a(int.is(int.lt(0))), bfalse),
    testing(int(20), __.a(int.is(int.gt(0))), btrue),
    testing(int(20), __.a(int.mult(int.neg).is(int.lt(0))), btrue),
    testing(int(20), __.a(int.is(__.lt(0))), bfalse),
    testing(int(20), __.a(int.is(__.gt(0))), btrue),
    testing(int(1), __.a(int.is(__.gt(0))), btrue),
    testing(int(1), __.a(int.is(__.lt(1))), bfalse),
    testing(int(20), __.a(int.mult(int.neg).is(__.lt(0))), btrue),
    testing(int(20).q(2), __.a(bool), bfalse.q(2)),
    testing(int(20).q(3), __.a(int.q(1, 4)), btrue.q(3)),
    testing(int(20).q(3), __.a(int), bfalse.q(3)),
    testing(int(20), __.a(real), bfalse),
    testing(int(20), __.a(str), bfalse),
    testing(int(20), __.a(rec), bfalse),
    testing(int, __.a(int(20)), int.a(int(20))),
    testing(int, __.a(int), int.a(int)),
    testing(int.plus(3), __.a(int), int.plus(3).a(int)),
    testing(int(1, 2, 3), __.a(int), bool(true, true, true)),
    testing(int(1, 2, 3), __.a(int.is(int.gt(2))), bool(false, false, true)),
    testing(int(int(1).q(10), int(2).q(20), int(3).q(30)), __.a(int.q(*).is(__.gt(2))), bool(bfalse.q(10), bfalse.q(20), btrue.q(30))),
    testing(int(1, 2, 3), __.a(real), bool(false, false, false)),
    testing(int(0), __.a(__.plus(10).is(__.gt(10))), bfalse),
    testing(int(1), __.a(__.plus(10).is(__.gt(10))), btrue),
    comment("real"),
    testing(real(20.0), __.a(real(20.0)), btrue),
    testing(real(20.0), __.a(real(30.0)), bfalse),
    testing(real(20.0), __.a(real.is(real.lt(0.0))), bfalse),
    testing(real(20.0), __.a(real.is(real.gte(0.0))), btrue),
    testing(real(20.0), __.a(real.mult(real.neg).is(real.lt(0.0))), btrue),
    testing(real(20.0), __.a(real.is(__.lt(0.0))), bfalse),
    testing(real(20.0), __.a(real.is(__.gte(0.0))), btrue),
    testing(real(20.0), __.a(real.mult(real.neg).is(__.lt(0.0))), btrue),
    testing(real(20.0), __.a(bool), bfalse),
    testing(real(20.0), __.a(int), bfalse),
    testing(real(20.0), __.a(real), btrue),
    testing(real(20.0), __.a(str), bfalse),
    testing(real(20.0), __.a(rec), bfalse),
    testing(real, __.a(real(20.0)), real.a(real(20.0))),
    comment("str"),
    testing(str("a"), __.a(str("a")), btrue),
    testing(str("a"), __.a(str("b")), bfalse),
    testing(str("a"), __.a(bool), bfalse),
    testing(str("a"), __.a(int), bfalse),
    testing(str("a"), __.a(real), bfalse),
    testing(str("a"), __.a(__.is(__.gt(str("b")))), bfalse),
    testing(str("a"), __.a(str.is(str.lt(str("b")))), btrue),
    testing(str("a"), __.a(str), btrue),
    testing(str("a"), __.a(rec), bfalse),
    testing(str, __.a(str("a")), str.a(str("a"))),
    testing(str.plus("a"), __.a(str("a")), str.plus("a").a(str("a"))),
    comment("rec"),
    testing(marko, __.a(marko), btrue),
    testing(marko, __.a(vadas), bfalse),
    testing(vadas, __.a(marko), bfalse),
    testing(marko, __.a(person), btrue),
    testing(vadas, __.a(person), btrue),
    testing(marko, __.a(oldPerson), btrue),
    testing(marko, __.a(youngPerson), bfalse),
    testing(marko, __.a(youngPerson.put("age", int)), bfalse),
    testing(vadas, __.a(oldPerson), bfalse),
    testing(vadas, __.a(oldPerson.put("age", int.is(int.gt(10)))), bfalse),
    testing(vadas, __.a(youngPerson), btrue),
    testing(marko, __.a(car), bfalse),
    testing(vadas, __.a(car), bfalse),
    testing(marko, __.a(bool), bfalse),
    testing(marko, __.a(int), bfalse),
    testing(marko, __.a(real), bfalse),
    testing(marko, __.a(str), bfalse),
    testing(marko, __.a(rec), btrue),
    testing(person, __.a(marko), person.a(marko)),
    testing(alst, __.a(str("a")), bfalse)
  )) {

  //(int.is(__.gt(10)), int.is(__.gt(9)), btrue),
  //(int.is(__.gt(10)), int.is(__.gt(1)), btrue),
  //(int.is(__.gt(10)),int.is(__.lt(1)),bfalse),
  // (youngPerson, vadas, bfalse),
  // (oldPerson, vadas, bfalse),
  // (youngPerson, marko, bfalse),
  // (oldPerson, vadas, bfalse),
  //  (person, car, bfalse),
  //  (person, person, btrue),
  //  (person, oldPerson, bfalse),
  //  (oldPerson,person,btrue),
  //  (car, car, btrue),

  test("[a] lineage") {
    val check: TableFor3[Obj, Obj, scala.Int] =
      new TableFor3(("computation", "result", "lineage length"),
        (int(20), int(20), 0),
        (int(20).plus(10).a(int).is(btrue), btrue, 3),
        (int(20).plus(10).a(real), bfalse, 2),
        (int(20).plus(10).id.a(int), btrue, 3),
        (int(20) ==> int.id.plus(int.plus(5).plus(5)).a(int.is(int.gt(20))).id, btrue, 4),
        (btrue, btrue, 0),
      )
    forEvery(check) { (expr, result, length) => {
      assertResult(result)(new mmlangScriptEngineFactory().getScriptEngine.eval(s"${expr}"))
      assertResult(result)(expr)
      assertResult(length)(expr.trace.length)
    }
    }
  }

  test("[a] play tests") {
    //println(marko.get("age",int).lt(0))
    //println((int.plus(3) ===> int.a(int)))//.compute(asType(__.a(int))))
    assertResult(btrue)(btrue.compute(asType(__.a(btrue))))
    assertResult(btrue)(btrue.a(btrue))
    assertResult(bfalse)(btrue.a(bfalse))
    assertResult(bfalse)(btrue.a(btrue.q(19)))
    assertResult(bfalse.q(10))(btrue.q(10).a(btrue.q(19)))
  }
}
