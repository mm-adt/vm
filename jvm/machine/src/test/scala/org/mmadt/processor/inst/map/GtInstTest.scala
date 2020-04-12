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

import org.mmadt.language.obj.`type`.{BoolType, IntType, Type}
import org.mmadt.language.obj.op.map.{AndOp, GtOp}
import org.mmadt.language.obj.value.{BoolValue, IntValue, Value}
import org.mmadt.language.obj.{Bool, Obj}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class GtInstTest extends FunSuite with TableDrivenPropertyChecks {
  private type GtType = Obj with GtOp[_<:Type[_], _<:Value[Obj]]
  private type GtValue = Value[Obj]
  test("[gt] lineage") {
    def maker(x: Obj, y: Value[Obj]): Obj = x.q(2).asInstanceOf[GtOp[Type[Obj],Value[Obj]]].gt(y).q(3).and(btrue).q(10)

    val starts: TableFor2[GtType, GtValue] =
      new TableFor2(("obj1", "obj2"),
        (int, int(2)),
        (int(4), int(2)),
        (real,real(342.0)),
        (real(3.3),real(1346.2)),
        (str,str("a")),
        (str("a"),str("b")))
    forEvery(starts) { (obj, arg) => {
      val expr = maker(obj, arg)
      obj match {
        case value: Value[_] => assert(value.value != expr.asInstanceOf[Value[_]].value)
        case _ =>
      }
      assert(obj.q != expr.q)
      assertResult(2)(expr.lineage.length)
      assertResult((int(60), int(60)))(expr.q)
      assertResult((obj.q(2), GtOp(arg).q(3)))(expr.lineage.head)
      assertResult((obj.q(2).asInstanceOf[GtOp[Type[Obj],Value[Obj]]].gt(arg).q(3), AndOp(btrue).q(10)))(expr.lineage.last)
    }
    }
  }
  ///////////////////////////////////////////////////////////////////////

  test("[gt] w/ int") {
    assertResult(bfalse)(int(1).gt(int(3))) // value * value = value
    assert(int(1).gt(int(3)).isInstanceOf[BoolValue])
    assert(int(1).gt(int(3)).isInstanceOf[Bool])
    assertResult(int(1).gt(int))(int(1).gt(int)) // value * type = type
    assert(int(1).gt(int).isInstanceOf[BoolType])
    assert(int(1).gt(int).isInstanceOf[BoolType])
    assertResult(int.gt(int(3)))(int.gt(int(3))) // type * value = type
    assert(int.gt(int(3)).isInstanceOf[BoolType])
    assert(int.gt(int(3)).isInstanceOf[BoolType])
    assertResult(int.gt(int))(int.gt(int)) // type * type = type
    assert(int.gt(int).isInstanceOf[BoolType])
    assert(int.gt(int).isInstanceOf[BoolType])
  }

  test("[gt] w/ real") {
    assertResult(bfalse)(real(1).gt(real(3))) // value * value = value
    assert(real(1).gt(real(3)).isInstanceOf[BoolValue])
    assert(real(1).gt(real(3)).isInstanceOf[Bool])
    assertResult(real(1).gt(real))(real(1).gt(real)) // value * type = type
    assert(real(1).gt(real).isInstanceOf[BoolType])
    assert(real(1).gt(real).isInstanceOf[BoolType])
    assertResult(real.gt(real(3)))(real.gt(real(3))) // type * value = type
    assert(real.gt(real(3)).isInstanceOf[BoolType])
    assert(real.gt(real(3)).isInstanceOf[BoolType])
    assertResult(real.gt(real))(real.gt(real)) // type * type = type
    assert(real.gt(real).isInstanceOf[BoolType])
    assert(real.gt(real).isInstanceOf[BoolType])
  }
}
