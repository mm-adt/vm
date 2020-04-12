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
import org.mmadt.language.obj.op.map.ZeroOp
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor1}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ZeroInstTest extends FunSuite with TableDrivenPropertyChecks {
  test("[zero] testing") {
    def zeroMaker(x: Obj with ZeroOp): Obj = x.zero().q(2).zero().q(10)

    val zeroExpressions: TableFor1[ZeroOp with Obj] =
      new TableFor1("obj",
        int,
        str,
        real,
        int(1),
        str("a"),
        real(10d))
    forEvery(zeroExpressions) { obj => {
      val expr = zeroMaker(obj)
      assertResult(2)(expr.lineage.length)
      assertResult((int(20), int(20)))(expr.q)
      assertResult((obj, ZeroOp().q(2)))(expr.lineage.head)
      assertResult((obj.zero().q(2), ZeroOp().q(10)))(expr.lineage.last)
    }
    }
  }
  ///////////////////////////////////////////////////////////////////////
  test("[zero] w/ int value") {
    assertResult(int(0))(int(0).zero())
    assertResult(int(0))(int(1).zero())
    assertResult(int(0))(int(781).zero())
    assertResult(int(0))(int(1).plus(100).zero())
    assertResult(int(0).q(10))(int(1).q(10).plus(100).zero())
  }
  test("[zero] w/ int type") {
    assertResult("int[zero]")(int.zero().toString)
    assertResult("int{10}[zero]")(int.q(10).zero().toString)
  }
  test("[zero] w/ real value") {
    assertResult(real(0))(real(0).zero())
    assertResult(real(0))(real(1).zero())
    assertResult(real(0))(real(781).zero())
    assertResult(real(0))(real(1).plus(100.435).zero())
    assertResult(real(0).q(10))(real(1).q(10).plus(100.135).zero())
  }
  test("[zero] w/ real type") {
    assertResult("real[zero]")(real.zero().toString)
    assertResult("real{10}[zero]")(real.q(10).zero().toString)
  }
  test("[zero] w/ str value") {
    assertResult(str(""))(str("").zero())
    assertResult(str(""))(str("notzero").zero())
    assertResult(str(""))(str("781").zero())
    assertResult(str(""))(str("1").plus("100").zero())
    assertResult(str("").q(10))(str("1").q(10).plus("100").zero())
  }
  test("[zero] w/ str type") {
    assertResult("str[zero]")(str.zero().toString)
    assertResult("str{10}[zero]")(str.q(10).zero().toString)
  }
}
