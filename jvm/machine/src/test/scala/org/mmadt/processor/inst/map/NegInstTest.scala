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
import org.mmadt.language.obj.op.map.NegOp
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory.{int, real}
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor1}

class NegInstTest extends FunSuite with TableDrivenPropertyChecks {
  test("[neg] lineage") {
    def maker(x: Obj with NegOp): Obj = x.q(3).neg().q(2).neg().q(10)

    val starts: TableFor1[NegOp with Obj] =
      new TableFor1("obj",
        int,
        real,
        int(1),
        real(10d))
    forEvery(starts) { obj => {
      val expr = maker(obj)
      obj match {
        case value: Value[_] => assert(value.value == expr.asInstanceOf[Value[_]].value)
        case _ =>
      }
      assert(obj.q != expr.q)
      assertResult(2)(expr.lineage.length)
      assertResult((int(60), int(60)))(expr.q)
      assertResult((obj.q(3), NegOp().q(2)))(expr.lineage.head)
      assertResult((obj.q(3).neg().q(2), NegOp().q(10)))(expr.lineage.last)
    }
    }
  }
  ///////////////////////////////////////////////////////////////////////
  test("[neg] w/ int value") {
    assertResult(int(-1))(int(1).neg())
    assertResult(int(-2))(int(2).neg())
    assertResult(int(-781))(int(781).neg())
    assertResult(int(-101))(int(1).plus(100).neg())
    assertResult(int(-101).q(10))(int(1).q(10).plus(100).neg())
  }
  test("[neg] w/ int type") {
    assertResult("int[neg]")(int.neg().toString)
    assertResult("int{10}[neg]")(int.q(10).neg().toString)
    assertResult("int{20}<=int{10}[neg]{2}")(int.q(10).neg().q(2).toString)
  }
  test("[neg] w/ real value") {
    assertResult(real(-1))(real(1).neg())
    assertResult(real(-2))(real(2).neg())
    assertResult(real(-781))(real(781).neg())
    assertResult(real(-101))(real(1).plus(100d).neg())
    assertResult(real(-101).q(10))(real(1).q(10).plus(100d).neg())
  }
  test("[neg] w/ real type") {
    assertResult("real[neg]")(real.neg().toString)
    assertResult("real{10}[neg]")(real.q(10).neg().toString)
    assertResult("real{20}<=real{10}[neg]{2}")(real.q(10).neg().q(2).toString)
  }

}