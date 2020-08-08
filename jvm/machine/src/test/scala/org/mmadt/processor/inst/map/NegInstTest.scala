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
import org.mmadt.language.obj.op.map.NegOp
import org.mmadt.storage.StorageFactory.{int, real, str}
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

class NegInstTest extends FunSuite with TableDrivenPropertyChecks {
  test("[neg] value, type, strm") {
    val starts: TableFor3[Obj, Obj, String] =
      new TableFor3[Obj, Obj, String](("query", "result", "type"),
        //////// INT
        (int(2).neg(), int(-2), "value"),
        (int(2).q(2).neg(), int(-2).q(2), "value"),
        (int(-2).neg(), int(2), "value"),
        (int(-2).neg().q(4).neg().q(2), int(-2).q(8), "value"),
        (int.neg(), int.neg(), "type"),
        (int(-1, -2, -3).neg(), int(1, 2, 3), "strm"),
        //////// REAL
        (real(2.0).neg(), real(-2.0), "value"),
        (real(-2.0).neg(), real(2.0), "value"),
        (real.neg(), real.neg(), "type"),
        (real(-1.0, -2.0, -3.0).neg(), real(1.0, 2.0, 3.0), "strm"),
        (real(-1.0, -2.0, -3.0).neg().q(10), real(real(1.0).q(10), real(2.0).q(10), real(3.0).q(10)), "strm"),
      )
    forEvery(starts) { (query, result, kind) => TestUtil.evaluate(query, __, result, compile = false)
    }
  }

  test("[neg] exceptions") {
    assertResult(LanguageException.unsupportedInstType(str("a"), NegOp()).getMessage)(intercept[LanguageException](str("a") ==> __.neg()).getMessage)
  }
}