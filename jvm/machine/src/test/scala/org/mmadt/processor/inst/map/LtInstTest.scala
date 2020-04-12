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
import org.mmadt.language.obj.op.map.{AndOp, LtOp}
import org.mmadt.language.obj.value.{BoolValue, Value}
import org.mmadt.language.obj.{Bool, Obj}
import org.mmadt.storage.StorageFactory.{btrue, int, real, str}
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class LtInstTest extends FunSuite with TableDrivenPropertyChecks {
  private type LtType = Obj with LtOp[_ <: Type[_], _ <: Value[Obj]]
  private type LtValue = Value[Obj]
  test("[lt] lineage") {
    def maker(x: Obj, y: Value[Obj]): Obj = x.q(2).asInstanceOf[LtOp[Type[Obj], Value[Obj]]].lt(y).q(3).and(btrue).q(10)

    val starts: TableFor2[LtType, LtValue] =
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
      assertResult((obj.q(2), LtOp(arg).q(3)))(expr.lineage.head)
      assertResult((obj.q(2).asInstanceOf[LtOp[Type[Obj], Value[Obj]]].lt(arg).q(3), AndOp(btrue).q(10)))(expr.lineage.last)
    }
    }
  }
  ///////////////////////////////////////////////////////////////////////
  test("[lt] w/ int") {
    assertResult(btrue)(int(1).lt(int(3))) // value * value = value
    assert(int(1).lt(int(3)).isInstanceOf[BoolValue])
    assert(int(1).lt(int(3)).isInstanceOf[Bool])
    assertResult(int(1).lt(int))(int(1).lt(int)) // value * type = type
    assert(int(1).lt(int).isInstanceOf[BoolType])
    assert(int(1).lt(int).isInstanceOf[Bool])
    assertResult(int.lt(int(3)))(int.lt(int(3))) // type * value = type
    assert(int.lt(int(3)).isInstanceOf[BoolType])
    assert(int.lt(int(3)).isInstanceOf[Bool])
    assertResult(int.lt(int))(int.lt(int)) // type * type = type
    assert(int.lt(int).isInstanceOf[BoolType])
    assert(int.lt(int).isInstanceOf[Bool])
  }
}
