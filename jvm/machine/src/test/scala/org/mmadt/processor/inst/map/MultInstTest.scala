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

import org.mmadt.language.obj.`type`.{IntType, RealType, Type}
import org.mmadt.language.obj.op.map.{MultOp, PlusOp}
import org.mmadt.language.obj.value.{IntValue, RealValue, Value}
import org.mmadt.language.obj.{Int, Obj}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class MultInstTest extends FunSuite with TableDrivenPropertyChecks {
  private type MultObj = Obj with MultOp[Type[Obj], Value[Obj]]
  private type MultValue = Value[Obj] with MultOp[Type[Obj], Value[Obj]]
  test("[mult] lineage") {
    def maker(x: MultObj, y: MultValue): Obj = x.q(2).mult(y).q(3).mult(y.mult(y)).q(10)

    val starts: TableFor2[Obj, Value[Obj]] =
      new TableFor2(("obj1", "obj2"),
        (int, int(2)),
        (int(4), int(2)),
        (real, real(342.0)),
        (real(3.3), real(1346.2)))
    forEvery(starts) { (obj, arg) => {
      val expr = maker(obj.asInstanceOf[MultObj], arg.asInstanceOf[MultValue])
      obj match {
        case value: Value[_] => assert(value.value != expr.asInstanceOf[Value[_]].value)
        case _ =>
      }
      assert(obj.q != expr.q)
      assertResult(2)(expr.lineage.length)
      assertResult((int(60), int(60)))(expr.q)
      assertResult((obj.q(2), MultOp(arg).q(3)))(expr.lineage.head)
      assertResult((obj.q(2).asInstanceOf[MultObj].mult(arg).q(3), MultOp(arg.asInstanceOf[MultValue].mult(arg)).q(10)))(expr.lineage.last)
    }
    }
  }
  ///////////////////////////////////////////////////////////////////////
  test("[mult] w/ int") {
    assertResult(int(6))(int(2).mult(int(3))) // value * value = value
    assert(int(1).mult(int(3)).isInstanceOf[IntValue])
    assert(int(1).mult(int(3)).isInstanceOf[IntValue])
    assertResult(int(1).mult(int))(int(1).mult(int)) // value * type = type
    assert(int(1).mult(int).isInstanceOf[IntType])
    assert(int(1).mult(int).isInstanceOf[IntType])
    assertResult(int.mult(int(3)))(int.mult(int(3))) // type * value = type
    assert(int.mult(int(3)).isInstanceOf[IntType])
    assert(int.mult(int(3)).isInstanceOf[IntType])
    assertResult(int.mult(int))(int.mult(int)) // type * type = type
    assert(int.mult(int).isInstanceOf[IntType])
    assert(int.mult(int).isInstanceOf[IntType])
  }

  test("[mult] w/ real") {
    assertResult(real(6.0))(real(2).mult(real(3))) // value * value = value
    assert(real(1).mult(real(3)).isInstanceOf[RealValue])
    assert(real(1).mult(real(3)).isInstanceOf[RealValue])
    assertResult(real(1).mult(real))(real(1).mult(real)) // value * type = type
    assert(real(1).mult(real).isInstanceOf[RealType])
    assert(real(1).mult(real).isInstanceOf[RealType])
    assertResult(real.mult(real(3)))(real.mult(real(3))) // type * value = type
    assert(real.mult(real(3)).isInstanceOf[RealType])
    assert(real.mult(real(3)).isInstanceOf[RealType])
    assertResult(real.mult(real))(real.mult(real)) // type * type = type
    assert(real.mult(real).isInstanceOf[RealType])
    assert(real.mult(real).isInstanceOf[RealType])
  }
}
