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
import org.mmadt.language.obj.op.map.{AndOp, GteOp}
import org.mmadt.language.obj.value.{BoolValue, Value}
import org.mmadt.language.obj.{Bool, Obj}
import org.mmadt.storage.StorageFactory.{bfalse, btrue, int, real, str}
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}

class GteInstTest extends FunSuite with TableDrivenPropertyChecks {
  private type GteType = Obj with GteOp[_ <: Type[_], _ <: Value[Obj]]
  private type GteValue = Value[Obj]
  test("[gte] lineage") {
    def maker(x: Obj, y: Value[Obj]): Obj = x.q(2).asInstanceOf[GteOp[Type[Obj], Value[Obj]]].gte(y).q(3).and(btrue).q(10)

    val starts: TableFor2[GteType, GteValue] =
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
      assertResult((obj.q(2), GteOp(arg).q(3)))(expr.lineage.head)
      assertResult((obj.q(2).asInstanceOf[GteOp[Type[Obj], Value[Obj]]].gte(arg).q(3), AndOp(btrue).q(10)))(expr.lineage.last)
    }
    }
  }
  ///////////////////////////////////////////////////////////////////////

  test("[gte] w/ int") {
    assertResult(bfalse)(int(1).gte(int(3))) // value * value = value
    assert(int(1).gte(int(3)).isInstanceOf[BoolValue])
    assert(int(1).gte(int(3)).isInstanceOf[Bool])
    assertResult(int(1).gte(int))(int(1).gte(int)) // value * type = type
    assert(int(1).gte(int).isInstanceOf[BoolType])
    assert(int(1).gte(int).isInstanceOf[BoolType])
    assertResult(int.gte(int(3)))(int.gte(int(3))) // type * value = type
    assert(int.gte(int(3)).isInstanceOf[BoolType])
    assert(int.gte(int(3)).isInstanceOf[BoolType])
    assertResult(int.gte(int))(int.gte(int)) // type * type = type
    assert(int.gte(int).isInstanceOf[BoolType])
    assert(int.gte(int).isInstanceOf[BoolType])
  }

  test("[gte] w/ real") {
    assertResult(bfalse)(real(1).gte(real(3))) // value * value = value
    assert(real(1).gte(real(3)).isInstanceOf[BoolValue])
    assert(real(1).gte(real(3)).isInstanceOf[Bool])
    assertResult(real(1).gte(real))(real(1).gte(real)) // value * type = type
    assert(real(1).gte(real).isInstanceOf[BoolType])
    assert(real(1).gte(real).isInstanceOf[BoolType])
    assertResult(real.gte(real(3)))(real.gte(real(3))) // type * value = type
    assert(real.gte(real(3)).isInstanceOf[BoolType])
    assert(real.gte(real(3)).isInstanceOf[BoolType])
    assertResult(real.gte(real))(real.gte(real)) // type * type = type
    assert(real.gte(real).isInstanceOf[BoolType])
    assert(real.gte(real).isInstanceOf[BoolType])
  }
}
