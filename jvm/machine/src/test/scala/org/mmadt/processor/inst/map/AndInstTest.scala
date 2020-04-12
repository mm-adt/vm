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
import org.mmadt.language.obj.op.map.AndOp
import org.mmadt.language.obj.value.{BoolValue, Value}
import org.mmadt.language.obj.{Bool, Obj}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor1}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class AndInstTest extends FunSuite with TableDrivenPropertyChecks {
  test("[and] testing") {
    def maker(x: Obj with AndOp): Obj = x.q(2).and(btrue).q(3).and(btrue).q(10)

    val starts: TableFor1[AndOp with Obj] =
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
      assertResult((obj.q(2), AndOp(btrue).q(3)))(expr.lineage.head)
      assertResult((obj.q(2).and(btrue).q(3), AndOp(btrue).q(10)))(expr.lineage.last)
    }
    }
  }
  ///////////////////////////////////////////////////////////////////////

  test("[and] w/ bool") {
    assertResult(btrue)(btrue.and(btrue)) // value * value = value
    assert(btrue.and(btrue).isInstanceOf[BoolValue])
    assert(btrue.and(btrue).isInstanceOf[Bool])
    assertResult(btrue.and(bool))(btrue.and(bool)) // value * type = type
    assert(btrue.and(bool).isInstanceOf[BoolType])
    assert(btrue.and(bool).isInstanceOf[Bool])
    assertResult(bool.and(btrue))(bool.and(btrue)) // type * value = type
    assert(bool.and(btrue).isInstanceOf[BoolType])
    assert(bool.and(btrue).isInstanceOf[Bool])
    assertResult(bool.and(bool))(bool.and(bool)) // type * type = type
    assert(bool.and(bool).isInstanceOf[BoolType])
    assert(bool.and(bool).isInstanceOf[Bool])
  }
}
