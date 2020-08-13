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

import org.mmadt.language.LanguageException
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.map.GtOp
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.storage.StorageFactory._
import org.scalatest.prop.TableFor3

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class GtInstTest extends BaseInstTest {
  override lazy val name = "[gt] value, type, strm, anon combinations"

  override val starts = new TableFor3[Obj, Obj, Obj](("lhs", "rhs", "result"),
        comment("===INT"),
        (int(2), __.gt(1), btrue), // value * value = value
        (int(2).q(10), __.gt(1), btrue.q(10)), // value * value = value
        (int(2).q(10), __.gt(1).q(20), btrue.q(200)), // value * value = value
        (int(2), __.gt(int(1).q(10)), btrue), // value * value = value
        (int(2), __.gt(int), bfalse), // value * type = value
        (int(2), __.gt(__.mult(int)), bfalse), // value * anon = value
        (int, __.gt(int(2)), int.gt(int(2))), // type * value = type
        (int.q(10), __.gt(int(2)), int.q(10).gt(int(2))), // type * value = type
        (int, __.gt(int), int.gt(int)), // type * type = type
        (int(1, 2, 3), __.gt(2), bool(false, false, true)), // strm * value = strm
        (int(1, 2, 3), __.gt(int(2).q(10)), bool(false, false, true)), // strm * value = strm
        (int(1, 2, 3), __.gt(int(2)).q(10), bool(bfalse.q(10), bfalse.q(10), btrue.q(10))), // strm * value = strm
        (int(1, 2, 3), __.gt(int(2)).q(10), bool(bfalse.q(20), btrue.q(10))), // strm * value = strm
        (int(1, 2, 3), __.gt(int(2)).q(10).id(), bool(bfalse.q(10), bfalse.q(10), btrue.q(10))), // strm * value = strm
        (int(1, 2, 3), __.gt(int(2)).q(10).id().q(5), bool(bfalse.q(50), bfalse.q(50), btrue.q(50))), // strm * value = strm
        (int(1, 2, 3), __.id().gt(int(2)).q(10).id().q(5), bool(bfalse.q(50), bfalse.q(50), btrue.q(50))), // strm * value = strm
        (int(1, 2, 3), __.gt(int(2)).id().q(10).id().q(5), bool(bfalse.q(50), bfalse.q(50), btrue.q(50))), // strm * value = strm
        (int(1, 2, 3), __.gt(int), bool(false, false, false)), // strm * type = strm
        (int(1, 2, 3), __.gt(__.mult(int)), bool(false, false, false)), // strm * anon = strm
        comment("===REAL"),
        (real(2.0), __.gt(1.0), btrue), // value * value = value
        (real(2.0), __.gt(real), bfalse), // value * type = value
        (real(2.0), __.gt(__.mult(real)), false), // value * anon = value
        (real, __.gt(real(2.0)), real.gt(2.0)), // type * value = type
        (real, __.gt(real), real.gt(real)), // type * type = type
        (real(1.0, 2.0, 3.0), __.gt(2.0).q(3), bool(bfalse.q(6), btrue.q(3))), // strm * value = strm
        (real(1.0, 2.0, 3.0), __.gt(2.0).id().q(3), bool(bfalse.q(6), btrue.q(3))), // strm * value = strm
        (real(1.0, 2.0, 3.0), __.gt(2.0), bool(false, false, true)), // strm * value = strm
        (real(1.0, 2.0, 3.0), __.gt(real), bool(false, false, false)), // strm * type = strm
        (real(1.0, 2.0, 3.0), __.gt(__.mult(real)), bool(false, false, false)), // strm * anon = strm
        comment("===STR"),
        (str("b"), __.gt("a"), btrue), // value * value = value
        (str("b").q(10), __.gt("a"), btrue.q(10)), // value * value = value
        (str("b").q(10), __.gt("a").q(20), btrue.q(200)), // value * value = value
        (str("b"), __.gt(str("a").q(10)), btrue), // value * value = value
        (str("b"), __.gt(str), bfalse), // value * type = value
        (str, __.gt("b"), str.gt("b")), // type * value = type
        (str.q(10), __.gt("b"), str.q(10).gt("b")), // type * value = type
        (str, __.gt(str), str.gt(str)), // type * type = type
        (str("a", "b", "c"), __.gt("b"), bool(false, false, true)), // strm * value = strm
        (str("a", "b", "c"), __.gt(str("b").q(10)), bool(false, false, true)), // strm * value = strm
        (str("a", "b", "c"), __.gt("b").q(10), bool(bfalse.q(10), bfalse.q(10), btrue.q(10))), // strm * value = strm
        (str("a", "b", "c"), __.gt(str), bool(false, false, false)) // strm * type = strm
      )

  test("[gt] exceptions") {
    assertResult(LanguageException.unsupportedInstType(bfalse, GtOp(btrue)).getMessage)(intercept[LanguageException](bfalse ==> __.gt(btrue)).getMessage)
  }

}
