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
import org.mmadt.language.obj.branch.Coprod
import org.mmadt.language.obj.op.map.PlusOp
import org.mmadt.language.obj.value.{IntValue, RealValue, Value}
import org.mmadt.language.obj.{Obj, Str}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2, TableFor3}

class PlusInstTest extends FunSuite with TableDrivenPropertyChecks {
  private type PlusObj = Obj with PlusOp[Type[Obj], Value[Obj]]
  private type PlusValue = Value[Obj] with PlusOp[Type[Obj], Value[Obj]]
  test("[plus] lineage") {
    def maker(x: PlusObj, y: PlusValue): Obj = x.q(2).plus(y).q(3).plus(y.plus(y)).q(10)

    val starts: TableFor2[Obj, Value[Obj]] =
      new TableFor2(("obj1", "obj2"),
        (int, int(2)),
        (int(4), int(2)),
        (real, real(342.0)),
        (real(3.3), real(1346.2)),
        (str, str("a")),
        (str("a"), str("b")),
        (rec, vrec(str("a") -> int(1), str("b") -> int(2))),
        (vrec(str("a") -> int(23), str("b") -> int(1)), vrec(str("a") -> int(1), str("b") -> int(2))))
    forEvery(starts) { (obj, arg) => {
      val expr = maker(obj.asInstanceOf[PlusObj], arg.asInstanceOf[PlusValue])
      obj match {
        case value: Value[_] => assert(value.value != expr.asInstanceOf[Value[_]].value)
        case _ =>
      }
      assert(obj.q != expr.q)
      assertResult(2)(expr.lineage.length)
      assertResult((int(60), int(60)))(expr.q)
      assertResult((obj.q(2), PlusOp(arg).q(3)))(expr.lineage.head)
      assertResult((obj.q(2).asInstanceOf[PlusObj].plus(arg).q(3), PlusOp(arg.asInstanceOf[PlusValue].plus(arg)).q(10)))(expr.lineage.last)
    }
    }
  }
  ///////////////////////////////////////////////////////////////////////

  test("[plus] w/ int") {
    assertResult(int(4))(int(1).plus(int(3))) // value * value = value
    assert(int(1).plus(int(3)).isInstanceOf[IntValue])
    assert(int(1).plus(int(3)).isInstanceOf[IntValue])
    assertResult(int(1).plus(int))(int(1).plus(int)) // value * type = type
    assert(int(1).plus(int).isInstanceOf[IntType])
    assert(int(1).plus(int).isInstanceOf[IntType])
    assertResult(int.plus(int(3)))(int.plus(int(3))) // type * value = type
    assert(int.plus(int(3)).isInstanceOf[IntType])
    assert(int.plus(int(3)).isInstanceOf[IntType])
    assertResult(int.plus(int))(int.plus(int)) // type * type = type
    assert(int.plus(int).isInstanceOf[IntType])
    assert(int.plus(int).isInstanceOf[IntType])
  }

  test("[plus] w/ real") {
    assertResult(real(4.0))(real(1).plus(real(3))) // value * value = value
    assert(real(1).plus(real(3)).isInstanceOf[RealValue])
    assert(real(1).plus(real(3)).isInstanceOf[RealValue])
    assertResult(real(1).plus(real))(real(1).plus(real)) // value * type = type
    assert(real(1).plus(real).isInstanceOf[RealType])
    assert(real(1).plus(real).isInstanceOf[RealType])
    assertResult(real.plus(real(3)))(real.plus(real(3))) // type * value = type
    assert(real.plus(real(3)).isInstanceOf[RealType])
    assert(real.plus(real(3)).isInstanceOf[RealType])
    assertResult(real.plus(real))(real.plus(real)) // type * type = type
    assert(real.plus(real).isInstanceOf[RealType])
    assert(real.plus(real).isInstanceOf[RealType])
  }
  test("[plus] w/ products and coproducts") {
    val starts: TableFor3[Coprod[Str], Coprod[Str], Coprod[Obj]] =
      new TableFor3[Coprod[Str], Coprod[Str], Coprod[Obj]](("a", "b", "c"),
        (coprod("a", "b"), coprod("c", "d"), coprod("a", "b", "c", "d")),
        (coprod("a", "b"), coprod("c"), coprod("a", "b", "c")),
        //(coprod("a", "b"), coprod("c", "d"), prod(coprod[Str]("a", "b"), coprod[Str]("c", "d"))),
      )
    forEvery(starts) { (a, b, c) => {
      assertResult(c)(a.plus(b))
      //assertResult(c)(PlusOp[Prod[Str]](b).exec(a))
    }
    }
  }
}
