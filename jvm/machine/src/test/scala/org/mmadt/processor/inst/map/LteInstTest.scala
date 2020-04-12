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

import org.mmadt.language.obj.`type`.{BoolType, Type}
import org.mmadt.language.obj.op.map.{AndOp, LteOp}
import org.mmadt.language.obj.value.{BoolValue, Value}
import org.mmadt.language.obj.{Bool, Obj}
import org.mmadt.storage.StorageFactory.{btrue, int, real, str}
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}

class LteInstTest extends FunSuite with TableDrivenPropertyChecks {
  private type LteType = Obj with LteOp[_ <: Type[_], _ <: Value[Obj]]
  private type LteValue = Value[Obj]
  test("[lte] lineage") {
    def maker(x: Obj, y: Value[Obj]): Obj = x.q(2).asInstanceOf[LteOp[Type[Obj], Value[Obj]]].lte(y).q(3).and(btrue).q(10)

    val starts: TableFor2[LteType, LteValue] =
      new TableFor2(("obj1", "obj2"),
        (int, int(2)),
        (int(4), int(2)),
        (real, real(342.0)),
        (real(3.3), real(1346.2)),
        (str, str("a")),
        (str("a"), str("b")))
    forEvery(starts) { (obj, arg) => {
      val expr = maker(obj, arg)
      obj match {
        case value: Value[_] => assert(value.value != expr.asInstanceOf[Value[_]].value)
        case _ =>
      }
      assert(obj.q != expr.q)
      assertResult(2)(expr.lineage.length)
      assertResult((int(60), int(60)))(expr.q)
      assertResult((obj.q(2), LteOp(arg).q(3)))(expr.lineage.head)
      assertResult((obj.q(2).asInstanceOf[LteOp[Type[Obj], Value[Obj]]].lte(arg).q(3), AndOp(btrue).q(10)))(expr.lineage.last)
    }
    }
  }
  ///////////////////////////////////////////////////////////////////////
  test("[lt] w/ int") {
    assertResult(btrue)(int(1).lte(int(3))) // value * value = value
    assert(int(1).lte(int(3)).isInstanceOf[BoolValue])
    assert(int(1).lte(int(3)).isInstanceOf[Bool])
    assertResult(int(1).lte(int))(int(1).lte(int)) // value * type = type
    assert(int(1).lte(int).isInstanceOf[BoolType])
    assert(int(1).lte(int).isInstanceOf[Bool])
    assertResult(int.lte(int(3)))(int.lte(int(3))) // type * value = type
    assert(int.lte(int(3)).isInstanceOf[BoolType])
    assert(int.lte(int(3)).isInstanceOf[Bool])
    assertResult(int.lte(int))(int.lte(int)) // type * type = type
    assert(int.lte(int).isInstanceOf[BoolType])
    assert(int.lte(int).isInstanceOf[Bool])
  }
}