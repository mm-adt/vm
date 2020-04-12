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

import org.mmadt.language.obj.`type`.BoolType
import org.mmadt.language.obj.op.map.OrOp
import org.mmadt.language.obj.value.{BoolValue, Value}
import org.mmadt.language.obj.{Bool, Obj}
import org.mmadt.storage.StorageFactory.{bfalse, bool, btrue, int}
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor1}

class OrInstTest extends FunSuite with TableDrivenPropertyChecks {
  test("[or] testing") {
    def maker(x: Obj with OrOp): Obj = x.q(2).or(bfalse).q(3).or(bfalse).q(10)

    val starts: TableFor1[OrOp with Obj] =
      new TableFor1("obj",
        bool,
        btrue,
        bfalse)
    forEvery(starts) { obj => {
      val expr = maker(obj)
      obj match {
        case value: Value[_] => assert(value.value == expr.asInstanceOf[Value[_]].value)
        case _ =>
      }
      assert(obj.q != expr.q)
      assertResult(2)(expr.lineage.length)
      assertResult((int(60), int(60)))(expr.q)
      assertResult((obj.q(2), OrOp(bfalse).q(3)))(expr.lineage.head)
      assertResult((obj.q(2).or(bfalse).q(3), OrOp(bfalse).q(10)))(expr.lineage.last)
    }
    }
  }
  ///////////////////////////////////////////////////////////////////////

  test("[or] w/ bool") {
    assertResult(btrue)(btrue.or(btrue)) // value * value = value
    assert(btrue.or(btrue).isInstanceOf[BoolValue])
    assert(btrue.or(btrue).isInstanceOf[Bool])
    assertResult(btrue.or(bool))(btrue.or(bool)) // value * type = type
    assert(btrue.or(bool).isInstanceOf[BoolType])
    assert(btrue.or(bool).isInstanceOf[BoolType])
    assertResult(bool.or(btrue))(bool.or(btrue)) // type * value = type
    assert(bool.or(btrue).isInstanceOf[BoolType])
    assert(bool.or(btrue).isInstanceOf[BoolType])
    assertResult(bool.or(bool))(bool.or(bool)) // type * type = type
    assert(bool.or(bool).isInstanceOf[BoolType])
    assert(bool.or(bool).isInstanceOf[BoolType])
  }
}
