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

package org.mmadt.processor.inst.filter

import org.mmadt.language.LanguageException
import org.mmadt.language.mmlang.mmlangScriptEngineFactory
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor1, TableFor3}

class IsInstTest extends FunSuite with TableDrivenPropertyChecks {
  test("[is] value, type, strm, anon combinations") {
    val starts: TableFor3[Obj, Obj, String] =
      new TableFor3[Obj, Obj, String](("query", "result", "type"),
        //////// INT
        (int(2).is(true), int(2), "value"), // value * value = value
        (int(2).q(10).is(true), int(2).q(10), "value"), // value * value = value
        (int(2).q(10).is(true).q(20), int(2).q(200), "value"), // value * value = value
        (int(2).is(btrue.q(10)), int(2), "value"), // value * value = value
        (int(2).is(bool), int(2).is(bool), "value"), // value * type = value
        (int(2).is(__.gt(int)), int(2).q(qZero), "value"), // value * anon = value
        (int(2).is(__.gte(int)), int(2), "value"), // value * anon = value
        (int(2).q(10).is(__.gte(int)), int(2).q(10), "value"), // value * anon = value
        (int(2).is(__.gte(int)).q(10), int(2).q(10), "value"), // value * anon = value
        (int(2).q(10).is(__.gte(int)).q(20), int(2).q(200), "value"), // value * anon = value
        (int.is(btrue), int.is(btrue), "type"), // type * value = type
        (int.q(10).is(btrue), int.q(10).is(btrue), "type"), // type * value = type
        (int(1, 2, 3).is(btrue), int(1, 2, 3), "strm"), // strm * value = strm
        (int(1, 2, 3).is(bfalse), strm, "strm"), // strm * value = strm
        (int(1, 2, 3).is(int.gt(int(2).q(10))), strm(List(int(3))), "strm"), // strm * value = strm
        (int(1, 2, 3).is(int.gte(int(2))).q(10), int(int(2).q(10), int(3).q(10)), "strm"), // strm * value = strm
        (int(1, 2, 3).is(int.gt(int)), strm, "strm"), // strm * type = strm
        (int(1, 2, 3).is(__.gte(__.mult(int))), strm(List(int(1))), "strm"), // strm * anon = strm
        //////// REAL
        (real(2.0).is(btrue), 2.0, "value"), // value * value = value
        (real(2.0).is(bfalse), real(2.0).q(qZero), "value"), // value * value = value
        (real(2.0).is(real.gt(real.mult(real))), real(2.0).q(qZero), "value"), // value * type = value
        (real(2.0).is(__.gt(__.mult(real))), real(2.0).q(qZero), "value"), // value * anon = value
        (real.is(real.gt(real(2.0))), real.is(real.gt(2.0)), "type"), // type * value = type
        (real.is(__.gt(real(2.0))), real.is(real.gt(2.0)), "type"), // type * anon = type
        (real.is(bool), real.is(bool), "type"), // type * type = type
        (real(1.0, 2.0, 3.0).is(real.gt(2.0)), strm(List(real(3.0))), "strm"), // strm * value = strm
        (real(1.0, 2.0, 3.0).is(real.gt(real)), strm, "strm"), // strm * type = strm
        (real(1.0, 2.0, 3.0).is(__.gt(__.mult(real))), strm, "strm"), // strm * anon = strm
        (real(1.0, 2.0, 3.0).is(__.lte(__.mult(real))), real(1.0, 2.0, 3.0), "strm"), // strm * anon = strm

      )
    forEvery(starts) { (expr, result, atype) => {
      assertResult(result)(new mmlangScriptEngineFactory().getScriptEngine.eval(s"${expr}"))
      assertResult(result)(expr)
      atype match {
        case "value" => assert(expr.isInstanceOf[Value[_]])
        case "type" => assert(expr.isInstanceOf[Type[_]])
        case "strm" => assert(expr.isInstanceOf[Strm[_]])
      }
    }
    }
  }

  test("[is] lineage") {
    def maker(x: Obj): Obj = x.q(2).is(btrue).q(3).is(btrue).q(10)

    val starts: TableFor1[Obj] =
      new TableFor1("obj",
        bool,
        int,
        real,
        str,
        rec,
        btrue,
        bfalse,
        int(10),
        real(23.0),
        str("a"),
        trec(str("a") -> int, str("b") -> bool),
        vrec(str("a") -> int(1), str("b") -> int(2)))
    forEvery(starts) { obj => {
      val expr = maker(obj)
      obj match {
        case value: Value[_] => assert(value.value == expr.asInstanceOf[Value[_]].value)
        case _ =>
      }
      assert(obj.q != expr.q)
      assertResult(2)(expr.lineage.length)
      assertResult((int(60), int(60)))(expr.q)
      assertResult((obj.q(2), IsOp(btrue).q(3)))(expr.lineage.head)
      assertResult((obj.q(2).is(btrue).q(3), IsOp(btrue).q(10)))(expr.lineage.last)
    }
    }
  }

  test("[is] w/ int") {
    assertResult(int(15).q(48))(int(5).q(2) ==> int.q(0, 48) <= int.q(2).plus(10).q(2).id().q(4).is(int.gt(2)).q(3))
    assertResult(int(15).q(48))(int(5).q(2) ==> int.q(2).plus(10).q(2).id().q(4).is(int.gt(2)).q(3))
    assertResult(int(15).q(48))(int(5).q(2) ==> int.q(2).plus(10).q(2).id().q(4).is(int.q(16).gt(2)).q(3))
    assertResult(int(15).q(48))(int(5).q(2) ==> int.q(2).plus(10).q(2).id().q(4).is(bool.q(16) <= int.q(16).gt(2)).q(3))
    assertThrows[LanguageException] {
      assertResult(int(15).q(48))(int(5).q(2) ==> int.plus(10).q(2).id().q(4).is(bool.q(16) <= int.q(16).gt(2)).q(3))
    }
  }
}
