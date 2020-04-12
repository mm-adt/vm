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

import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.op.map.OneOp
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor1}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class OneInstTest extends FunSuite with TableDrivenPropertyChecks {
  test("[one] testing") {
    def maker(x: Obj with OneOp): Obj = x.q(2).one().q(3).one().q(10)

    val starts: TableFor1[OneOp with Obj] =
      new TableFor1("obj",
        int,
        real,
        int(24),
        real(10d))
    forEvery(starts) { obj => {
      val expr = maker(obj)
      obj match {
        case value: Value[_] => assert(value.value != expr.asInstanceOf[Value[_]].value)
        case _ =>
      }
      assert(obj.q != expr.q)
      assertResult(2)(expr.lineage.length)
      assertResult((int(60), int(60)))(expr.q)
      assertResult((obj.q(2), OneOp().q(3)))(expr.lineage.head)
      assertResult((obj.q(2).one().q(3), OneOp().q(10)))(expr.lineage.last)
    }
    }
  }
  test("[one] w/ int value") {
    assertResult(int(1))(int(0).one())
    assertResult(int(1))(int(1).one())
    assertResult(int(1))(int(1).plus(100).one())
    assertResult(int(1).q(10))(int(1).q(10).plus(100).one())
  }
  test("[one] w/ int type") {
    assertResult("int[one]")(int.one().toString)
    assertResult("int{10}[one]")(int.q(10).one().toString)
  }
  test("[one] w/ real value") {
    assertResult(real(1.0))(real(0.0).one())
    assertResult(real(1.0))(real(1.0).one())
    assertResult(real(1.0))(real(1.0).plus(100.0).one())
    assertResult(real(1.0).q(10))(real(1.0).q(10).plus(100.0).one())
  }
  test("[one] w/ real type") {
    assertResult("real[one]")(real.one().toString)
    assertResult("real{10}[one]")(real.q(10).one().toString)
  }
}
