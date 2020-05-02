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
class MapInstTest extends FunSuite with TableDrivenPropertyChecks {

  test("[map] value, type, strm, anon combinations") {
    val starts: TableFor3[Obj, Obj, String] =
      new TableFor3[Obj, Obj, String](("query", "result", "type"),
        //////// INT
        (int(2).map(1), int(1), "value"), // value * value = value
        (int(2).q(10).map(int(1)), int(1).q(10), "value"), // value * value = value
        (int(2).q(10).map(int(1)).q(20), int(1).q(200), "value"), // value * value = value
        (int(2).map(int(1).q(10)), int(1) /*.q(10)*/ , "value"), // value * value = value
        (int(2).map(int), int(2), "value"), // value * type = value
        (int(2).map(__.mult(int)), int(4), "value"), // value * anon = value
        (int.map(int(2)), int.map(int(2)), "type"), // type * value = type
        (int.q(10).map(int(2)), int.q(10).map(int(2)), "type"), // type * value = type
        (int.map(int), int.map(int), "type"), // type * type = type
        (int(1, 2, 3).map(2), int(2, 2, 2), "strm"), // strm * value = strm
        // (int(1, 2, 3).map(int(2).q(10)), int(int(2).q(10), int(2).q(10), int(2).q(10)), "strm"), // strm * value = strm
        (int(1, 2, 3) ===> __.map(int(2)).q(10), int(int(2).q(10), int(2).q(10), int(2).q(10)), "strm"), // strm * value = strm
        (int(1, 2, 3).map(int), int(1, 2, 3), "strm"), // strm * type = strm
        (int(1, 2, 3).map(int.mult(int)), int(1, 4, 9), "strm"), // strm * type = strm
        (int(1, 2, 3).map(__.mult(int)), int(1, 4, 9), "strm"), // strm * anon = strm
        //////// REAL
        (real(2.0).map(real(1.0)), real(1.0), "value"), // value * value = value
        (real(2.0).map(real), real(2.0), "value"), // value * type = value
        (real(2.0).map(__.mult(real)), real(4.0), "value"), // value * anon = value
        (real.map(real(2.0)), real.map(2.0), "type"), // type * value = type
        (real.map(real), real.map(real), "type"), // type * type = type
        (real(1.0, 2.0, 3.0).map(2.0), real(2.0, 2.0, 2.0), "strm"), // strm * value = strm
        (real(1.0, 2.0, 3.0).map(real), real(1.0, 2.0, 3.0), "strm"), // strm * type = strm
        (real(1.0, 2.0, 3.0).map(__.mult(real)), real(1.0, 4.0, 9.0), "strm"), // strm * anon = strm
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

  test("[map] w/ values") {
    assertResult(int(5))(int(1).plus(1).map(int(5)))
    assertResult(int(2))(int(1).plus(1).map(int))
    assertResult(int(20))(int(1).plus(1).map(int.mult(10)))
  }
  test("[map] w/ types") {
    assertResult("int[plus,1][map,int]")(int.plus(1).map(int).toString)
    assertResult("int[plus,1][map,int[mult,10]]")(int.plus(1).map(int.mult(10)).toString)
    assertResult(int(200))(int(18) ==> int.plus(1).map(int.mult(10)).plus(10))
    assertResult("int[plus,1][map,int[mult,10]]")(int.plus(1).map(int.mult(10)).toString)
    //
    assertResult(int(60))(int(5) ==> int.plus(1).map(int.mult(10)))

  }
}