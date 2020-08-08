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
import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.map.ZeroOp
import org.mmadt.language.obj.{Obj, Str}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ZeroInstTest extends FunSuite with TableDrivenPropertyChecks {
  test("[zero] value, type, strm") {
    val starts: TableFor3[Obj, Obj, Obj] =
      new TableFor3[Obj, Obj, Obj](("input", "type", "result"),
        //////// INT
        (int(2), __.zero(), int(0)),
        (int(-2), __.zero(), int(0)),
        (int, __.zero(), int(0)),
        (int(1, 2, 3), __.plus(0).zero(), int(0).q(3)),
        (int(1, 2), __.plus(1).q(10).zero(), int(0).q(20)),
        //////// REAL
        (real(2.0), __.zero(), real(0.0)),
        (real(-2.0), __.zero(), real(0.0)),
        (real, __.zero(), real(0.0)),
        (real(-1.0, -2.0, -3.0), __.zero(), real(0.0).q(3)),
        (real(-1.0, -2.0, -3.0), __.plus(1.0).q(10).zero(), real(0.0).q(30)),
        (real(-1.0, -2.0, -3.0), __.plus(1.0).q(20).zero(), real(0.0).q(60)),
        //////// STR
        (str("a"), __.zero(), str("")),
        (str("b"), __.zero(), str("")),
        (str, __.zero(), str("")),
        (str("a", "b", "c"), __.zero(), str("").q(3)),
        //////// POLY
        (lst[Str](g = (Tokens.`,`, List(str("a")))), __.zero(), lst(g = (Tokens.`,`, Nil))),
        (lst[Str](g = (Tokens.`,`, List(str("a"), str("b"), str("c")))), __.zero(), lst(g = (Tokens.`,`, Nil))),
        //(prod(prod(str("a")), prod(str("b")), prod(str("c"))).zero(), prod().q(3)),
      )
    forEvery(starts) { (input, atype, result) => TestUtil.evaluate(input, atype, result, ZeroOp().q(atype.trace.head._2.q), compile = false)
    }
  }
}
