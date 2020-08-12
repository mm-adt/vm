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
import org.mmadt.language.obj.op.map.LteOp
import org.mmadt.storage.StorageFactory.{bfalse, bool, btrue, int, real}
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

class LteInstTest extends FunSuite with TableDrivenPropertyChecks {

  test("[lt] value, type, strm, anon combinations") {
    val starts: TableFor3[Obj, Obj, String] =
      new TableFor3[Obj, Obj, String](("query", "result", "type"),
        //////// INT
        (int(2).lte(1), bfalse, "value"), // value * value = value
        (int(2).q(10).lte(1), bfalse.q(10), "value"), // value * value = value
        (int(2).q(10).lte(1).q(20), bfalse.q(200), "value"), // value * value = value
        (int(2).lte(int(1).q(10)), bfalse, "value"), // value * value = value
        (int(2).lte(int), btrue, "value"), // value * type = value
        (int(2).lte(__.mult(int)), btrue, "value"), // value * anon = value
        (int.lte(int(2)), int.lte(int(2)), "type"), // type * value = type
        (int.q(10).lte(int(2)), int.q(10).lte(int(2)), "type"), // type * value = type
        (int.lte(int), int.lte(int), "type"), // type * type = type
        (int(1, 2, 3).lte(2), bool(true, true, false), "strm"), // strm * value = strm
        (int(1, 2, 3).lte(int(2).q(10)), bool(true, true, false), "strm"), // strm * value = strm
        (int(1, 2, 3) ==> __.lte(int(2)).q(10), bool(btrue.q(10), btrue.q(10), bfalse.q(10)), "strm"), // strm * value = strm
        (int(1, 2, 3).lte(int), bool(true, true, true), "strm"), // strm * type = strm
        (int(1, 2, 3).lte(__.mult(int)), bool(true, true, true), "strm"), // strm * anon = strm
        //////// REAL
        (real(2.0).lte(1.0), bfalse, "value"), // value * value = value
        (real(2.0).lte(real), btrue, "value"), // value * type = value
        (real(2.0).lte(__.mult(real)), true, "value"), // value * anon = value
        (real.lte(real(2.0)), real.lte(2.0), "type"), // type * value = type
        (real.lte(real), real.lte(real), "type"), // type * type = type
        (real(1.0, 2.0, 3.0).lte(2.0), bool(true, true, false), "strm"), // strm * value = strm
        (real(1.0, 2.0, 3.0).lte(real), bool(true, true, true), "strm"), // strm * type = strm
        (real(1.0, 2.0, 3.0).lte(__.mult(real)), bool(true, true, true), "strm"), // strm * anon = strm
      )
    forEvery(starts) { (query, result, kind) => TestUtil.evaluate(query, __, result)
    }
  }

  test("[lte] exceptions") {
    assertResult(LanguageException.unsupportedInstType(bfalse, LteOp(btrue)).getMessage)(intercept[LanguageException](bfalse ==> __.lte(btrue)).getMessage)
  }
}
