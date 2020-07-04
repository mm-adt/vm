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

import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class GtInstTest extends FunSuite with TableDrivenPropertyChecks {

  test("[gt] value, type, strm, anon combinations") {
    val starts: TableFor3[Obj, Obj, String] =
      new TableFor3[Obj, Obj, String](("query", "result", "type"),
        //////// INT
        (int(2).gt(1), btrue, "value"), // value * value = value
        (int(2).q(10).gt(1), btrue.q(10), "value"), // value * value = value
        (int(2).q(10).gt(1).q(20), btrue.q(200), "value"), // value * value = value
        (int(2).gt(int(1).q(10)), btrue, "value"), // value * value = value
        (int(2).gt(int), bfalse, "value"), // value * type = value
        (int(2).gt(__.mult(int)), bfalse, "value"), // value * anon = value
        (int.gt(int(2)), int.gt(int(2)), "type"), // type * value = type
        (int.q(10).gt(int(2)), int.q(10).gt(int(2)), "type"), // type * value = type
        (int.gt(int), int.gt(int), "type"), // type * type = type
        (int(1, 2, 3).gt(2), bool(false, false, true), "strm"), // strm * value = strm
        (int(1, 2, 3).gt(int(2).q(10)), bool(false, false, true), "strm"), // strm * value = strm
        (int(1, 2, 3).gt(int(2)).q(10), bool(bfalse.q(10), bfalse.q(10), btrue.q(10)), "strm"), // strm * value = strm
        (int(1, 2, 3).gt(int(2)).q(10), bool(bfalse.q(20), btrue.q(10)), "strm"), // strm * value = strm
        (int(1, 2, 3).gt(int(2)).q(10).id(), bool(bfalse.q(10), bfalse.q(10), btrue.q(10)), "strm"), // strm * value = strm
        (int(1, 2, 3).gt(int(2)).q(10).id().q(5), bool(bfalse.q(50), bfalse.q(50), btrue.q(50)), "strm"), // strm * value = strm
        (int(1, 2, 3).id().gt(int(2)).q(10).id().q(5), bool(bfalse.q(50), bfalse.q(50), btrue.q(50)), "strm"), // strm * value = strm
        (int(1, 2, 3).gt(int(2)).id().q(10).id().q(5), bool(bfalse.q(50), bfalse.q(50), btrue.q(50)), "strm"), // strm * value = strm
        (int(1, 2, 3).gt(int), bool(false, false, false), "strm"), // strm * type = strm
        (int(1, 2, 3).gt(__.mult(int)), bool(false, false, false), "strm"), // strm * anon = strm
        //////// REAL
        (real(2.0).gt(1.0), btrue, "value"), // value * value = value
        (real(2.0).gt(real), bfalse, "value"), // value * type = value
        (real(2.0).gt(__.mult(real)), false, "value"), // value * anon = value
        (real.gt(real(2.0)), real.gt(2.0), "type"), // type * value = type
        (real.gt(real), real.gt(real), "type"), // type * type = type
        (real(1.0, 2.0, 3.0).gt(2.0).q(3), bool(bfalse.q(6), btrue.q(3)), "strm"), // strm * value = strm
        (real(1.0, 2.0, 3.0).gt(2.0).id().q(3), bool(bfalse.q(6), btrue.q(3)), "strm"), // strm * value = strm
        (real(1.0, 2.0, 3.0).gt(2.0), bool(false, false, true), "strm"), // strm * value = strm
        (real(1.0, 2.0, 3.0).gt(real), bool(false, false, false), "strm"), // strm * type = strm
        (real(1.0, 2.0, 3.0).gt(__.mult(real)), bool(false, false, false), "strm"), // strm * anon = strm
        //////// STR
        (str("b").gt("a"), btrue, "value"), // value * value = value
        (str("b").q(10).gt("a"), btrue.q(10), "value"), // value * value = value
        (str("b").q(10).gt("a").q(20), btrue.q(200), "value"), // value * value = value
        (str("b").gt(str("a").q(10)), btrue, "value"), // value * value = value
        (str("b").gt(str), bfalse, "value"), // value * type = value
        (str.gt("b"), str.gt("b"), "type"), // type * value = type
        (str.q(10).gt("b"), str.q(10).gt("b"), "type"), // type * value = type
        (str.gt(str), str.gt(str), "type"), // type * type = type
        (str("a", "b", "c").gt("b"), bool(false, false, true), "strm"), // strm * value = strm
        (str("a", "b", "c").gt(str("b").q(10)), bool(false, false, true), "strm"), // strm * value = strm
        (str("a", "b", "c") ==> __.gt("b").q(10), bool(bfalse.q(10), bfalse.q(10), btrue.q(10)), "strm"), // strm * value = strm
        (str("a", "b", "c").gt(str), bool(false, false, false), "strm"), // strm * type = strm
      )
    forEvery(starts) { (query, result, atype) => {
      //assertResult(result)(new mmlangScriptEngineFactory().getScriptEngine.eval(s"${query}"))
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
