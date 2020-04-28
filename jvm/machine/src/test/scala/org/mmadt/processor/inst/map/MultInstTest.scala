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
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class MultInstTest extends FunSuite with TableDrivenPropertyChecks {
  test("[mult] value, type, strm, anon combinations") {
    val starts: TableFor3[Obj, Obj, String] =
      new TableFor3[Obj, Obj, String](("query", "result", "type"),
        //////// INT
        (int(2).mult(2), int(4), "value"), // value * value = value
        (int(2).q(10).mult(2), int(4).q(10), "value"), // value * value = value
        (int(2).q(10).mult(2).q(20), int(4).q(200), "value"), // value * value = value
        (int(2).mult(int(2).q(10)), int(4), "value"), // value * value = value
        (int(2).mult(int), int(4), "value"), // value * type = value
        (int(2).mult(__.mult(int)), int(8), "value"), // value * anon = value
        (int.mult(int(2)), int.mult(int(2)), "type"), // type * value = type
        (int.q(10).mult(int(2)), int.q(10).mult(int(2)), "type"), // type * value = type
        (int.mult(int), int.mult(int), "type"), // type * type = type
        (int(1, 2, 3).mult(2), int(2, 4, 6), "strm"), // strm * value = strm
        (int(1, 2, 3).mult(int(2).q(10)), int(2, 4, 6), "strm"), // strm * value = strm
        (int(1, 2, 3).mult(int(2)).q(10), int(int(2).q(10), int(4).q(10), int(6).q(10)), "strm"), // strm * value = strm
        (int(1, 2, 3).mult(int), int(1, 4, 9), "strm"), // strm * type = strm
        (int(1, 2, 3).mult(__.mult(int)), int(1, 8, 27), "strm"), // strm * anon = strm
        //////// REAL
        (real(2.0).mult(2.0), real(4), "value"), // value * value = value
        (real(2.0).mult(real), real(4.0), "value"), // value * type = value
        (real(2.0).mult(__.mult(real)), real(8.0), "value"), // value * anon = value
        (real.mult(real(2.0)), real.mult(real(2.0)), "type"), // type * value = type
        (real.mult(real), real.mult(real), "type"), // type * type = type
        (real(1.0, 2.0, 3.0).mult(2.0), real(2.0, 4.0, 6.0), "strm"), // strm * value = strm
        (real(1.0, 2.0, 3.0).mult(real), real(1.0, 4.0, 9.0), "strm"), // strm * type = strm
        (real(1.0, 2.0, 3.0).mult(__.mult(real)), real(1.0, 8.0, 27.0), "strm"), // strm * anon = strm
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
