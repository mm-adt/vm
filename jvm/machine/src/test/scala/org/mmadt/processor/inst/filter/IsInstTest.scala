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

import org.mmadt.language.obj.`type`.BoolType
import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.value.{BoolValue, Value}
import org.mmadt.language.obj.{Bool, Obj}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor1}

class IsInstTest extends FunSuite with TableDrivenPropertyChecks {
  test("[is] testing") {
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
  ///////////////////////////////////////////////////////////////////////

  test("[is] w/ bool") {
    assertResult(btrue)(btrue.is(btrue)) // value * value = value
    assert(btrue.is(btrue).isInstanceOf[BoolValue])
    assert(btrue.is(btrue).isInstanceOf[Bool])
    assertResult(btrue.is(bool))(btrue.is(bool)) // value * type = type
    //    assert(btrue.is(bool).isInstanceOf[BoolType]) TODO: this is because the value becomes a type (unique to [is])
    //    assert(btrue.is(bool).isInstanceOf[BoolType])
    assertResult(bool.is(btrue))(bool.is(btrue)) // type * value = type
    assert(bool.is(btrue).isInstanceOf[BoolType])
    assert(bool.is(btrue).isInstanceOf[BoolType])
    assertResult(bool.is(bool))(bool.is(bool)) // type * type = type
    assert(bool.is(bool).isInstanceOf[BoolType])
    assert(bool.is(bool).isInstanceOf[BoolType])
  }
}
