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

import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.map.AndOp
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor1, TableFor3}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class AndInstTest extends FunSuite with TableDrivenPropertyChecks {
  test("[and] value, type, strm, anon combinations") {
    val starts: TableFor3[Obj, Obj, String] =
      new TableFor3[Obj, Obj, String](("query", "result", "type"),
        (btrue.and(btrue), btrue, "value"), // value * value = value
        (btrue.q(10).and(btrue), btrue.q(10), "value"), // value * value = value
        (btrue.q(10).and(btrue).q(10), btrue.q(100), "value"), // value * value = value
        (btrue.and(btrue.q(10)), btrue, "value"), // value * value = value
        (btrue.and(bool), btrue, "value"), // value * type = value
        (btrue.q(10).and(bool), btrue.q(10), "value"), // value * type = value
        (btrue.q(10).and(bool).q(10), btrue.q(100), "value"), // value * type = value
        (btrue.and(bool.q(10)), btrue, "value"), // value * type = value
        (btrue.and(__.and(bool)), btrue, "value"), // value * anon = value
        (btrue.q(10).and(__.and(bool)), btrue.q(10), "value"), // value * anon = value
        (btrue.and(__.and(bool.q(10))), btrue, "value"), // value * anon = value
        (bool.and(btrue), bool.and(btrue), "type"), // type * value = type
        (bool.and(bool), bool.and(bool), "type"), // type * type = type
        (bool(true, true, false).and(bfalse), bool(false, false, false), "strm"), // strm * value = strm
        (bool(true, true, false).and(bfalse.q(10)), bool(false, false, false), "strm"), // strm * value = strm
        (bool(true, true, false).and(bool), bool(true, true, false), "strm"), // strm * type = strm
        //(bool(true, true, false).and(bool).q(10), bool(btrue.q(10), btrue.q(10), bfalse.q(10)), "strm"), // strm * type = strm
        (bool(true, true, false).and(bool.q(10)), bool(true, true, false), "strm"), // strm * type = strm
        (bool(true, true, false).and(__.and(bool)), bool(true, true, false), "strm"), // strm * anon = strm
        (bool(true, true, false).and(__.and(bool.q(10))), bool(true, true, false), "strm"), // strm * anon = strm
      )
    forEvery(starts) { (query, result, atype) => {
      assertResult(result)(query)
      atype match {
        case "value" => assert(query.isInstanceOf[Value[_]])
        case "type" => assert(query.isInstanceOf[Type[_]])
        case "strm" => assert(query.isInstanceOf[Strm[_]])
      }
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
        case value: Value[_] => assert(value.value == expr.asInstanceOf[Value[_]].value)
        case _ =>
      }
      assert(obj.q != expr.q)
      assertResult(2)(expr.lineage.length)
      assertResult((int(60), int(60)))(expr.q)
      assertResult((obj.q(2), AndOp(btrue).q(3)))(expr.lineage.head)
      assertResult((obj.q(2).and(btrue).q(3), AndOp(btrue).q(10)))(expr.lineage.last)
    }
    }
  }
}
