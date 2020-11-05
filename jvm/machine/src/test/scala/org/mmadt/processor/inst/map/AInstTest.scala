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
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.Obj.{booleanToBool, intToInt}
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp.{MM, MMX, NONE}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil._
import org.mmadt.storage.StorageFactory._
import org.scalatest.prop.TableFor3

class AInstTest extends BaseInstTest(
  testSet("[a] table test", List(MM, MMX),
    comment("bool"),
    testing(true, a(true), true, "true[a,true]"),
    testing(true, a(true.q(1)), true, "true[a,true{1}]"),
    testing(true, a(true.q(2)), false, "true[a,true{2}]"),
    testing(true, a(true.q(?)), true, "true[a,true{?}]"),
    testing(true.q(2), a(true.q(?)), false.q(2), "true{2}[a,true{?}]"),
    testing(true.q(2), a(true.q(1, 3)), true.q(2), "true{2}[a,true{1,3}]"),
    testing(true.q(2), a(true.q(2)), true.q(2), "true{2}[a,true{2}]"),
    testing(true, a(false), false, "true[a,false]"),
    testing(true, a(bool.is(bool.eqs(false))), false),
    testing(true, a(bool.is(bool.eqs(true))), true),
    testing(true, a(bool), true, "true[a,bool]"),
    testing(true, a(int), false, "true[a,int]"),
    testing(true, a(real), false, "true[a,real]"),
    testing(true, a(str), false, "true[a,str]"),
    testing(true, a(str.is(gt("a"))), false, "true[a,str[is>'a']]"),
    testing(true, a(rec), false, "true[a,rec]"),
    testing(bool, a(true), bool.a(true), "bool[a,true]"),
    comment("int"),
    testing(20, a(20), true, "20[a,20]"),
    testing(20, a(30), false, "20[a,30]"),
    testing(20, a(int.is(gt(10))), true, "20[a,[is>10]]"),
    testing(20, a(int.is(int.lt(0))), false, "20[a,[is<0]]"),
    testing(20, a(int.is(int.gt(0))), true, "20[a,[is,[gt,0]]]"),
    testing(20.q(2), int.q(2).a(int.q(2).mult(int.neg).is(int.lt(0))), true.q(2), "20{2} => int{2}[a,int{2}[mult,int[neg]][is,int[lt,0]]]"),
    testing(20, a(int.is(lt(0))), false),
    testing(20, a(int.is(gt(0))), true),
    testing(1, a(int.is(gt(0))), true),
    testing(1, int.a(int.is(lt(1))), false, "1=>int[a,int[is<1]]"),
    testing(20, a(int.mult(int.neg).is(lt(0))), true),
    testing(20.q(2), a(bool), false.q(2)),
    testing(20.q(3), a(int.q(1, 4)), true.q(3)),
    testing(20.q(3), a(int), false.q(3)),
    testing(20, a(real), false, "20[a,real]"),
    testing(20, a(str), false, "20[a,str]"),
    testing(20, a(rec), false, "20[a,rec]"),
    testing(int, a(20), int.a(20), "int[a,20]"),
    testing(int, a(int), int.a(int), "int[a,int]"),
    testing(int.plus(3), a(int), int.plus(3).a(int)),
    testing(int(1, 2, 3), a(int), bool(true, true, true)),
    testing(int(1, 2, 3), a(int.is(int.gt(2))), bool(false, false, true)),
    testing(int(1.q(10), 2.q(20), 3.q(30)), a(int.q(*).is(gt(2))), bool(false.q(10), false.q(20), true.q(30))),
    testing(int(1, 2, 3), a(real), bool(false, false, false)),
    testing(0, a(plus(10).is(gt(10))), false),
    testing(1, a(plus(10).is(gt(10))), true),
    comment("real"),
    testing(20.0, a(20.0), true),
    testing(20.0, a(30.0), false),
    testing(20.0, a(real.is(real.lt(0.0))), false),
    testing(20.0, a(real.is(real.gte(0.0))), true),
    testing(20.0, a(real.mult(real.neg).is(real.lt(0.0))), true),
    testing(20.0, a(real.is(lt(0.0))), false),
    testing(20.0, a(real.is(gte(0.0))), true),
    testing(20.0, a(real.mult(real.neg).is(lt(0.0))), true),
    testing(20.0, a(bool), false),
    testing(20.0, a(int), false),
    testing(20.0, a(real), true),
    testing(20.0, a(str), false),
    testing(20.0, a(rec), false),
    testing(real, a(20.0), real.a(20.0)),
    comment("str"),
    testing("a", a("a"), true),
    testing("a", a("b"), false),
    testing("a", a(bool), false),
    testing("a", a(int), false),
    testing("a", a(real), false),
    testing("a", a(is(gt("b"))), false),
    testing("a", a(str.is(str.lt("b"))), true),
    testing("a", a(str), true),
    testing("a", a(rec), false),
    testing(str, a("a"), str.a("a")),
    testing(str.plus("a"), a("a"), str.plus("a").a("a")),
    comment("rec"),
    testing(marko, a(marko), true),
    testing(marko, a(vadas), false),
    testing(vadas, a(marko), false),
    testing(marko, a(person), true),
    testing(vadas, a(person), true),
    testing(marko, a(oldPerson), true),
    testing(marko, a(youngPerson), false),
    testing(marko, a(youngPerson.put("age", int)), false),
    testing(vadas, a(oldPerson), false),
    testing(vadas, a(oldPerson.put("age", int.is(int.gt(10)))), false),
    testing(vadas, a(youngPerson), true),
    testing(marko, a(car), false),
    testing(vadas, a(car), false),
    testing(marko, a(bool), false),
    testing(marko, a(int), false),
    testing(marko, a(real), false),
    testing(marko, a(str), false),
    testing(marko, a(rec), true),
    testing(person, a(marko), person.a(marko)),
    testing(alst, a("a"), false),
  )) {

  test("[a] lineage") {
    val check:TableFor3[Obj, Obj, scala.Int] =

      new TableFor3(("computation", "result", "lineage length"),
        (20, 20, 0),
        (20.plus(10).a(int).is(true), true, 3),
        (20.plus(10).a(real), false, 2),
        (20.plus(10).id.a(int), true, 3),
        (20 =>> int.id.plus(int.plus(5).plus(5)).a(int.is(int.gt(20))).id, true, 4),
        (true, true, 0),
      )
    forEvery(check) { (expr, result, length) => {
      assertResult(result)(new mmlangScriptEngineFactory().getScriptEngine.eval(s"${expr}"))
      assertResult(result)(expr)
      assertResult(length)(expr.trace.length)
    }
    }
  }

}
