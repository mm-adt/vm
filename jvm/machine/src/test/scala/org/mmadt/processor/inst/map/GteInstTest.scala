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
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory.{bfalse, bool, btrue, int, real}
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

class GteInstTest extends FunSuite with TableDrivenPropertyChecks {

  test("[gte] value, type, strm, anon combinations") {
    val starts: TableFor3[Obj, Obj, String] =
      new TableFor3[Obj, Obj, String](("query", "result", "type"),
        //////// INT
        (int(2).gte(1), btrue, "value"), // value * value = value
        (int(2).q(10).gte(1), btrue.q(10), "value"), // value * value = value
        (int(2).q(10).gte(1).q(20), btrue.q(200), "value"), // value * value = value
        (int(2).gte(int(1).q(10)), btrue, "value"), // value * value = value
        (int(2).gte(int), btrue, "value"), // value * type = value
        (int(2).gte(__.mult(int)), bfalse, "value"), // value * anon = value
        (int.gte(int(2)), int.gte(int(2)), "type"), // type * value = type
        (int.q(10).gte(int(2)), int.q(10).gte(2), "type"), // type * value = type
        (int.gte(int), int.gte(int), "type"), // type * type = type
        (int(1, 2, 3).gte(2), bool(false, true, true), "strm"), // strm * value = strm
        (int(1, 2, 3).gte(int(2).q(10)), bool(false, true, true), "strm"), // strm * value = strm
        (int(1, 2, 3).gte(int(2)).q(10), bool(bfalse.q(10), btrue.q(10), btrue.q(10)), "strm"), // strm * value = strm
        (int(1, 2, 3).gte(int), bool(true, true, true), "strm"), // strm * type = strm
        (int(1, 2, 3).gte(__.mult(int)), bool(true, false, false), "strm"), // strm * anon = strm
        //////// REAL
        (real(2.0).gte(1.0), btrue, "value"), // value * value = value
        (real(2.0).gte(real), btrue, "value"), // value * type = value
        (real(2.0).gte(__.mult(real)), bfalse, "value"), // value * anon = value
        (real.gte(real(2.0)), real.gte(2.0), "type"), // type * value = type
        (real.gte(real), real.gte(real), "type"), // type * type = type
        (real(1.0, 2.0, 3.0).gte(2.0), bool(false, true, true), "strm"), // strm * value = strm
        (real(1.0, 2.0, 3.0).gte(real), bool(true, true, true), "strm"), // strm * type = strm
        (real(1.0, 2.0, 3.0).gte(__.mult(real)), bool(true, false, false), "strm"), // strm * anon = strm
      )
    forEvery(starts) { (query, result, atype) => {
      assertResult(result)(new mmlangScriptEngineFactory().getScriptEngine.eval(s"${query}"))
      assertResult(result)(query)
      atype match {
        case "value" => assert(query.isInstanceOf[Value[_]])
        case "type" => assert(query.isInstanceOf[Type[_]])
        case "strm" => assert(query.isInstanceOf[Strm[_]])
      }
    }
    }
  }
}
