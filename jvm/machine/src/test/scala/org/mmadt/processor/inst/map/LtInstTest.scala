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

import org.mmadt.TestUtil
import org.mmadt.language.LanguageException
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.map.LtOp
import org.mmadt.storage.StorageFactory.{bfalse, bool, btrue, int, real}
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class LtInstTest extends FunSuite with TableDrivenPropertyChecks {

  test("[lt] value, type, strm, anon combinations") {
    val starts: TableFor3[Obj, Obj, String] =
      new TableFor3[Obj, Obj, String](("query", "result", "type"),
        //////// INT
        (int(2).lt(1), bfalse, "value"), // value * value = value
        (int(2).q(10).lt(1), bfalse.q(10), "value"), // value * value = value
        (int(2).q(10).lt(1).q(20), bfalse.q(200), "value"), // value * value = value
        (int(2).lt(int(1).q(10)), bfalse, "value"), // value * value = value
        (int(2).lt(int), bfalse, "value"), // value * type = value
        (int(2).lt(__.mult(int)), btrue, "value"), // value * anon = value
        (int.lt(int(2)), int.lt(int(2)), "type"), // type * value = type
        (int.q(10).lt(int(2)), int.q(10).lt(int(2)), "type"), // type * value = type
        (int.lt(int), int.lt(int), "type"), // type * type = type
        (int(1, 2, 3).lt(2), bool(true, false, false), "strm"), // strm * value = strm
        (int(1, 2, 3).lt(int(2).q(10)), bool(true, false, false), "strm"), // strm * value = strm
        (int(1, 2, 3) ==> __.lt(int(2)).q(10), bool(btrue.q(10), bfalse.q(10), bfalse.q(10)), "strm"), // strm * value = strm
        (int(1, 2, 3).lt(int), bool(false, false, false), "strm"), // strm * type = strm
        (int(1, 2, 3).lt(__.mult(int)), bool(false, true, true), "strm"), // strm * anon = strm
        //////// REAL
        (real(2.0).lt(1.0), bfalse, "value"), // value * value = value
        (real(2.0).lt(real), bfalse, "value"), // value * type = value
        (real(2.0).lt(__.mult(real)), true, "value"), // value * anon = value
        (real.lt(real(2.0)), real.lt(2.0), "type"), // type * value = type
        (real.lt(real), real.lt(real), "type"), // type * type = type
        (real(1.0, 2.0, 3.0).lt(2.0), bool(true, false, false), "strm"), // strm * value = strm
        (real(1.0, 2.0, 3.0).lt(real), bool(false, false, false), "strm"), // strm * type = strm
        (real(1.0, 2.0, 3.0).lt(__.mult(real)), bool(false, true, true), "strm"), // strm * anon = strm
      )
    forEvery(starts) { (query, result, kind) => TestUtil.evaluate(query, __, result)
    }
  }

  test("[lt] exceptions") {
    assertResult(LanguageException.unsupportedInstType(bfalse, LtOp(btrue)).getMessage)(intercept[LanguageException](bfalse ==> __.lt(btrue)).getMessage)
  }
}
