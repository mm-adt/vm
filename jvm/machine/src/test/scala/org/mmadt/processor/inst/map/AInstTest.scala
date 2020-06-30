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
import org.mmadt.language.obj.op.map.AOp
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Bool, Obj, Rec}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

class AInstTest extends FunSuite with TableDrivenPropertyChecks {

  val marko: Rec[StrValue, Obj] = rec(str("name") -> str("marko"), str("age") -> int(29))
  val vadas: Rec[StrValue, Obj] = rec(str("name") -> str("vadas"), str("age") -> int(27))
  val person: Rec[StrValue, Obj] = rec(str("name") -> str, str("age") -> int)
  val oldPerson: Rec[StrValue, Obj] = rec(str("name") -> str, str("age") -> int.is(int.gt(28)))
  val youngPerson: Rec[StrValue, Obj] = rec(str("name") -> str, str("age") -> __.is(__.lt(28)))
  val car: Rec[StrValue, Obj] = rec(str("name") -> str, str("year") -> int)

  test("[a] w/ values") {
    val check: TableFor3[Obj, Obj, Bool] =
      new TableFor3[Obj, Obj, Bool](("value", "type", "bool"),
        // bool
        (btrue, btrue, btrue),
        (btrue, btrue.q(1), btrue),
        (btrue, btrue.q(2), bfalse),
        (btrue, btrue.q(?), btrue),
        (btrue.q(2), btrue.q(?), bfalse.q(2)),
        (btrue.q(2), btrue.q(1, 3), btrue.q(2)),
        (btrue.q(2), btrue.q(2), btrue.q(2)),
        (btrue, bfalse, bfalse),
        (btrue, bool.is(bool.eqs(bfalse)), bfalse),
        (btrue, bool.is(bool.eqs(btrue)), btrue),
        (btrue, bool, btrue),
        (btrue, int, bfalse),
        (btrue, real, bfalse),
        (btrue, str, bfalse),
        (btrue, str.is(__.gt("a")), bfalse),
        (btrue, rec, bfalse),
        (bool, btrue, bool.a(btrue)),
        // int
        (int(20), int(20), btrue),
        (int(20), int(30), bfalse),
        (int(20), int.is(__.gt(10)), btrue),
        //(int.is(__.gt(10)), int.is(__.gt(9)), btrue),
        //(int.is(__.gt(10)), int.is(__.gt(1)), btrue),
        //(int.is(__.gt(10)),int.is(__.lt(1)),bfalse),
        (int(20), int.is(int.lt(0)), bfalse),
        (int(20), int.is(int.gt(0)), btrue),
        (int(20), int.mult(int.neg()).is(int.lt(0)), btrue),
        (int(20), int.is(__.lt(0)), bfalse),
        (int(20), int.is(__.gt(0)), btrue),
        (int(1), int.is(__.gt(0)), btrue),
        (int(1), int.is(__.lt(1)), bfalse),
        (int(20), int.mult(int.neg()).is(__.lt(0)), btrue),
        (int(20).q(2), bool, bfalse.q(2)),
        (int(20).q(3), int.q(1, 4), btrue.q(3)),
        (int(20).q(3), int, bfalse.q(3)),
        (int(20), real, bfalse),
        (int(20), str, bfalse),
        (int(20), rec, bfalse),
        (int, int(20), int.a(int(20))),
        (int, int, int.a(int)),
        (int.plus(3), int, int.plus(3).a(int)),
        (int(1, 2, 3), int, bool(true, true, true)),
        (int(1, 2, 3), int.is(int.gt(2)), bool(false, false, true)),
        (int(int(1).q(10), int(2).q(20), int(3).q(30)), int.q(*).is(__.gt(2)), bool(bfalse.q(10), bfalse.q(20), btrue.q(30))),
        (int(1, 2, 3), real, bool(false, false, false)),
        (int(0), __.plus(10).is(__.gt(10)), bfalse),
        (int(1), __.plus(10).is(__.gt(10)), btrue),
        // real
        (real(20.0), real(20.0), btrue),
        (real(20.0), real(30.0), bfalse),
        (real(20.0), real.is(real.lt(0.0)), bfalse),
        (real(20.0), real.is(real.gte(0.0)), btrue),
        (real(20.0), real.mult(real.neg()).is(real.lt(0.0)), btrue),
        (real(20.0), real.is(__.lt(0.0)), bfalse),
        (real(20.0), real.is(__.gte(0.0)), btrue),
        (real(20.0), real.mult(real.neg()).is(__.lt(0.0)), btrue),
        (real(20.0), bool, bfalse),
        (real(20.0), int, bfalse),
        (real(20.0), real, btrue),
        (real(20.0), str, bfalse),
        (real(20.0), rec, bfalse),
        (real, real(20.0), real.a(real(20.0))),
        // str
        (str("a"), str("a"), btrue),
        (str("a"), str("b"), bfalse),
        (str("a"), bool, bfalse),
        (str("a"), int, bfalse),
        (str("a"), real, bfalse),
        (str("a"), __.is(__.gt(str("b"))), bfalse),
        (str("a"), str.is(str.lt(str("b"))), btrue),
        (str("a"), str, btrue),
        (str("a"), rec, bfalse),
        (str, str("a"), str.a(str("a"))),
        (str.plus("a"), str("a"), str.plus("a").a(str("a"))),
        // rec
        (marko, marko, btrue),
        (marko, vadas, bfalse),
        (vadas, marko, bfalse),
        (marko, person, btrue),
        (vadas, person, btrue),
        (marko, oldPerson, btrue),
        (marko, youngPerson, bfalse),
        (marko, youngPerson.put("age", int), btrue),
        (vadas, oldPerson, bfalse),
        (vadas, oldPerson.put("age", int.is(int.gt(10))), btrue),
        (vadas, youngPerson, btrue),
        // (youngPerson, vadas, bfalse),
        // (oldPerson, vadas, bfalse),
        // (youngPerson, marko, bfalse),
        // (oldPerson, vadas, bfalse),
        (marko, car, bfalse),
        (vadas, car, bfalse),
        //  (person, car, bfalse),
        //  (person, person, btrue),
        //  (person, oldPerson, bfalse),
        //  (oldPerson,person,btrue),
        //  (car, car, btrue),
        (marko, bool, bfalse),
        (marko, int, bfalse),
        (marko, real, bfalse),
        (marko, str, bfalse),
        (marko, rec, btrue),
        (person, marko, person.a(marko)),
      )
    forEvery(check) { (left, right, result) => {
      //assertResult(result)(new mmlangScriptEngineFactory().getScriptEngine.eval(s"[${left}][a,${right}]"))
      assertResult(result)(left.compute(__.a(right)))
      assertResult(result)(left.a(right))
      assertResult(result)(AOp(right).exec(left))
      assertResult(result)(left ===> left.range.a(right))
      assertResult(result)(left ===> (left.range ===> left.range.a(right)))
    }
    }
  }

  test("[a] lineage") {
    val check: TableFor3[Obj, Obj, scala.Int] =
      new TableFor3(("computation", "result", "lineage length"),
        (int(20), int(20), 0),
        (int(20).plus(10).a(int).is(btrue), btrue, 3),
        (int(20).plus(10).a(real), bfalse, 2),
        (int(20).plus(10).id().a(int), btrue, 3),
        (int(20) ===> int.id().plus(int.plus(5).plus(5)).a(int.is(int.gt(20))).id(), btrue, 4),
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
