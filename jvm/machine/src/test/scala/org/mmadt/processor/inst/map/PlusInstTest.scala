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
import org.mmadt.language.obj.Obj._
import org.mmadt.language.obj.`type`.{IntType, RealType, __}
import org.mmadt.language.obj.op.map.PlusOp
import org.mmadt.language.obj.value.{IntValue, RealValue}
import org.mmadt.language.obj.{Int, Obj, Real}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor4}

class PlusInstTest extends FunSuite with TableDrivenPropertyChecks {
  test("[plus] value, type, strm, anon combinations") {
    val starts: TableFor4[Obj, Obj, Obj, String] =
      new TableFor4[Obj, Obj, Obj, String](("input", "type", "result", "kind"),
        //////// INT
        (int(2), __.plus(2), int(4), "value"), // value * value = value
        (int(2).q(10), __.plus(2), int(4).q(10), "value"), // value * value = value
        (int(2).q(10), __.plus(2).q(20), int(4).q(200), "value"), // value * value = value
        (int(2), __.plus(int(2).q(10)), int(4), "value"), // value * value = value
        (int(2), __.plus(int), int(4), "value"), // value * type = value
        (int(2), __.plus(__.plus(int)), int(6), "value"), // value * anon = value
        (int, __.plus(int(2)), int.plus(int(2)), "type"), // type * value = type
        (int.q(10), __.plus(int(2)), int.q(10).plus(int(2)), "type"), // type * value = type
        (int, __.plus(int), int.plus(int), "type"), // type * type = type
        (int(1, 2, 3), __.plus(2), int(3, 4, 5), "strm"), // strm * value = strm
        (int(1, 2, 3), __.plus(int(2).q(10)), int(3, 4, 5), "strm"), // strm * value = strm
        (int(1, 2, 3), int.q(3).plus(int(2)).q(10), int(int(3).q(10), int(4).q(10), int(5).q(10)), "strm"), // strm * value = strm
        (int(1, 2, 3), __.plus(int(2)).q(10), int(int(3).q(10), int(4).q(10), int(5).q(10)), "strm"), // strm * value = strm
        (int(1, 2, 3), __.plus(int), int(2, 4, 6), "strm"), // strm * type = strm
        (int(1, 2, 3), __.plus(__.plus(int)), int(3, 6, 9), "strm"), // strm * anon = strm
        //////// REAL
        (real(2.0), __.plus(2.0), real(4), "value"), // value * value = value
        (real(2.0), __.plus(real), real(4.0), "value"), // value * type = value
        (real(2.0), __.plus(__.plus(real)), real(6.0), "value"), // value * anon = value
        (real, __.plus(real(2.0)), real.plus(real(2.0)), "type"), // type * value = type
        (real, __.plus(real), real.plus(real), "type"), // type * type = type
        (real(1.0, 2.0, 3.0), __.plus(2.0), real(3.0, 4.0, 5.0), "strm"), // strm * value = strm
        (real(1.0, 2.0, 3.0), __.plus(real), real(2.0, 4.0, 6.0), "strm"), // strm * type = strm
        (real(1.0, 2.0, 3.0), __.plus(__.plus(real)), real(3.0, 6.0, 9.0), "strm"), // strm * anon = strm
      )
    forEvery(starts) { (input, atype, result, kind) => TestUtil.evaluate(input, atype, result, PlusOp(atype.trace.head._2.arg0).q(atype.trace.head._2.q)) }
  }
  ///////////////////////////////////////////////////////////////////////

  test("[plus] w/ int") {
    assertResult(int(4))(int(1).plus(int(3))) // value * value = value
    assert(int(1).plus(int(3)).isInstanceOf[IntValue])
    assert(int(1).plus(int(3)).isInstanceOf[Int])
    assertResult(int(2))(int(1).plus(int)) // value * type = value
    assert(int(1).plus(int).isInstanceOf[IntValue])
    assert(int(1).plus(int).isInstanceOf[Int])
    assertResult(int.plus(int(3)))(int.plus(int(3))) // type * value = type
    assert(int.plus(int(3)).isInstanceOf[IntType])
    assert(int.plus(int(3)).isInstanceOf[Int])
    assertResult(int.plus(int))(int.plus(int)) // type * type = type
    assert(int.plus(int).isInstanceOf[IntType])
    assert(int.plus(int).isInstanceOf[Int])
  }

  test("[plus] w/ real") {
    assertResult(real(4.0))(real(1).plus(real(3))) // value * value = value
    assert(real(1).plus(real(3)).isInstanceOf[RealValue])
    assert(real(1).plus(real(3)).isInstanceOf[Real])
    assertResult(real(2))(real(1).plus(real)) // value * type = value
    assert(real(1).plus(real).isInstanceOf[RealValue])
    assert(real(1).plus(real).isInstanceOf[Real])
    assertResult(real.plus(real(3)))(real.plus(real(3))) // type * value = type
    assert(real.plus(real(3)).isInstanceOf[RealType])
    assert(real.plus(real(3)).isInstanceOf[Real])
    assertResult(real.plus(real))(real.plus(real)) // type * type = type
    assert(real.plus(real).isInstanceOf[RealType])
    assert(real.plus(real).isInstanceOf[Real])
  }

  /*test("[plus] w/ serial and parallel poly") {
     val starts: TableFor3[Poly[Str], Poly[Str], Poly[Obj]] =
       new TableFor3[Poly[Str], Poly[Str], Poly[Obj]](("a", "b", "c"),
         (`|`("a", "b"), `|`("c", "d"), `|`("a", "b", "c", "d")),
         (`|`("a", "b"), `|`("c"), `|`("a", "b", "c")),
         //(coprod("a", "b"), coprod("c", "d"), prod(coprod[Str]("a", "b"), coprod[Str]("c", "d"))),
       )
     forEvery(starts) { (a, b, c) => {
       assertResult(c)(a.plus(b))
       //assertResult(c)(PlusOp[Prod[Str]](b).exec(a))
     }
     }
   }*/
}
