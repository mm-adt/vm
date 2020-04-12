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
import org.mmadt.language.obj.op.map.{IdOp, ZeroOp}
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor1}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class IdInstTest extends FunSuite with TableDrivenPropertyChecks {
  test("[id] testing") {
    def idMaker(x: Obj with IdOp): Obj = x.q(2).id().q(3).id().q(10)

    val idExpressions: TableFor1[ZeroOp with Obj] =
      new TableFor1("obj",
        int,
        str,
        real,
        int(1),
        str("a"),
        real(10d))
    forEvery(idExpressions) { obj => {
      val expr = idMaker(obj)
      obj match {
        case value: Value[_] => assertResult(value.value)(expr.asInstanceOf[Value[_]].value)
        case _ =>
      }
      assertResult(2)(expr.lineage.length)
      assertResult((int(60), int(60)))(expr.q)
      assertResult((obj.q(2), IdOp().q(3)))(expr.lineage.head)
      assertResult((obj.q(2).id().q(3), IdOp().q(10)))(expr.lineage.last)
    }
    }
  }
  ///////////////////////////////////////////////////////////////////////
  test("[id] w/ int") {
    assertResult("int[id]")(int.id().toString)
    assertResult("int[id][id]")(int.id().id().toString)
    assertResult("int{6}<=int[id]{2}[id]{3}")(int.q(1).id().q(2).id().q(3).toString)
    assertResult("2{6}")(int(2).q(1).id().q(2).id().q(3).toString)
    assertResult(int(2))(int(2).id())
    assertResult(int(2))(int(2).id().id())
    assertResult(int(2))(int(2) ==> int.id().id())
    assert(int.id().id().domain() == int.id().range)
  }
}
