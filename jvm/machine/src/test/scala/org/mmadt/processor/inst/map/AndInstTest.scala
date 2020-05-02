/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 *  mm-ADT is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU Affero General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 *  License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 *  You can be released from the requirements of the license by purchasing a
 *  commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.processor.inst.map

import org.mmadt.language.mmlang.mmlangScriptEngineFactory
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.map.AndOp
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Bool, Obj}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor1, TableFor4}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class AndInstTest extends FunSuite with TableDrivenPropertyChecks {
  test("[and] value, type, strm, anon combinations") {
    val starts: TableFor4[Obj, Obj, Obj, String] =
      new TableFor4[Obj, Obj, Obj, String](("input", "type", "result", "kind"),
        (btrue, __.and(btrue), btrue, "value"), // value * value = value
        (btrue.q(10), __.and(btrue), btrue.q(10), "value"), // value * value = value
        (btrue.q(10), __.and(btrue).q(10), btrue.q(100), "value"), // value * value = value
        (btrue, __.and(btrue.q(10)), btrue, "value"), // value * value = value
        (btrue, __.and(bool), btrue, "value"), // value * type = value
        (btrue.q(10), __.and(bool), btrue.q(10), "value"), // value * type = value
        (btrue.q(10), __.and(bool).q(10), btrue.q(100), "value"), // value * type = value
        (btrue, __.and(bool.q(10)), btrue, "value"), // value * type = value
        (btrue, __.and(__.and(bool)), btrue, "value"), // value * anon = value
        (btrue.q(10), __.and(__.and(bool)), btrue.q(10), "value"), // value * anon = value
        (btrue, __.and(__.and(bool.q(10))), btrue, "value"), // value * anon = value
        (bool, __.and(btrue), bool.and(btrue), "type"), // type * value = type
        (bool, __.and(bool), bool.and(bool), "type"), // type * type = type
        (bool(true, true, false), __.and(bfalse), bool(false, false, false), "strm"), // strm * value = strm
        (bool(true, true, false), __.and(bfalse.q(10)), bool(false, false, false), "strm"), // strm * value = strm
        (bool(true, true, false), __.and(bool), bool(true, true, false), "strm"), // strm * type = strm
        (bool(true, true, false), __.and(bool).q(10), bool(btrue.q(10), btrue.q(10), bfalse.q(10)), "strm"), // strm * type = strm
        (bool(true, true, false), __.and(bool.q(10)), bool(true, true, false), "strm"), // strm * type = strm
        (bool(true, true, false), __.and(__.and(bool)), bool(true, true, false), "strm"), // strm * anon = strm
        (bool(true, true, false), __.and(__.and(bool.q(10))), bool(true, true, false), "strm"), // strm * anon = strm
      )
    forEvery(starts) { (input, atype, result, kind) => {
     //println(s"${input} ${atype.toString.substring(4)}")
      List(
        //new mmlangScriptEngineFactory().getScriptEngine.eval(s"${input} ${atype.toString.substring(4)}"),
        AndOp(atype.trace.head._2.arg0()).q(atype.trace.head._2.q).exec(input.asInstanceOf[Bool]),
        input.compute(asType(atype)),
        input ===> (input.range ===> atype),
        input ===> atype,
        input ==> asType(atype)).foreach(x => {
        assertResult(result)(x)
        kind match {
          case "value" => assert(x.isInstanceOf[Value[_]])
          case "type" => assert(x.isInstanceOf[Type[_]])
          case "strm" => assert(x.isInstanceOf[Strm[_]])
        }
      })
    }
    }
  }

  test("[and] lineage") {
    def maker(x: Obj with AndOp): Obj = x.q(2).and(btrue).q(3).and(btrue).q(10)

    val starts: TableFor1[AndOp with Obj] =
      new TableFor1("obj",
        bool,
        btrue,
        bfalse)
    forEvery(starts) { obj => {
      val expr = maker(obj)
      obj match {
        case value: Value[_] => assert(value.ground == expr.asInstanceOf[Value[_]].ground)
        case _ =>
      }
      assert(obj.q != expr.q)
      assertResult(2)(expr.trace.length)
      assertResult((int(60), int(60)))(expr.q)
      assertResult((obj.q(2), AndOp(btrue).q(3)))(expr.trace.head)
      assertResult((obj.q(2).and(btrue).q(3), AndOp(btrue).q(10)))(expr.trace.last)
    }
    }
  }
}
