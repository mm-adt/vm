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
import org.mmadt.language.obj.op.map.EqsOp
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor4}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class EqsInstTest extends FunSuite with TableDrivenPropertyChecks {

  test("[eq] value, type, strm, anon combinations") {
    val starts: TableFor4[Obj, Obj, Obj, String] =
      new TableFor4[Obj, Obj, Obj, String](("input", "type", "result", "kind"),
        //////// INT
        (int(2), int.eqs(1), bfalse, "value"), // value * value = value
        (int(2).q(10), __.eqs(1), bfalse.q(10), "value"), // value * value = value
        (int(2).q(10), __.eqs(1).q(20), bfalse.q(200), "value"), // value * value = value
        (int(2), __.eqs(int(1).q(10)), bfalse, "value"), // value * value = value
        (int(2), __.eqs(int), btrue, "value"), // value * type = value
        (int(2), __.eqs(__.mult(int)), bfalse, "value"), // value * anon = value
        (int, __.eqs(int(2)), int.eqs(int(2)), "type"), // type * value = type
        (int.q(10), __.eqs(int(2)), int.q(10).eqs(2), "type"), // type * value = type
        (int, __.eqs(int), int.eqs(int), "type"), // type * type = type
        (int(1, 2, 3), __.eqs(2), bool(false, true, false), "strm"), // strm * value = strm
        (int(1, 2, 3), __.eqs(int(2).q(10)), bool(false, true, false), "strm"), // strm * value = strm
        (int(1, 2, 3), __.eqs(int(2)).q(10), bool(bfalse.q(10), btrue.q(10), bfalse.q(10)), "strm"), // strm * value = strm
        (int(1, 2, 3), __.eqs(int), bool(true, true, true), "strm"), // strm * type = strm
        (int(1, 2, 3), __.eqs(__.mult(int)), bool(true, false, false), "strm"), // strm * anon = strm
        //////// REAL
        (real(2.0), __.eqs(1.0), bfalse, "value"), // value * value = value
        (real(2.0), __.eqs(real), btrue, "value"), // value * type = value
        (real(2.0), __.eqs(__.mult(real)), bfalse, "value"), // value * anon = value
        (real, __.eqs(real(2.0)), real.eqs(2.0), "type"), // type * value = type
        (real, __.eqs(real), real.eqs(real), "type"), // type * type = type
        (real(1.0, 2.0, 3.0), __.eqs(2.0), bool(false, true, false), "strm"), // strm * value = strm
        (real(1.0, 2.0, 3.0), __.eqs(real), bool(true, true, true), "strm"), // strm * type = strm
        (real(1.0, 2.0, 3.0), __.eqs(__.mult(real)), bool(true, false, false), "strm"), // strm * anon = strm
      )
    forEvery(starts) { (input, atype, result, kind) => {
      List(
        EqsOp(atype.lineage.head._2.arg0()).q(atype.lineage.head._2.q).exec(input),
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
}