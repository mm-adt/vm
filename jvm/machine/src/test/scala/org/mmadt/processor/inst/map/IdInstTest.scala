/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.processor.inst.map

import org.mmadt.TestUtil
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.__
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class IdInstTest extends FunSuite with TableDrivenPropertyChecks {
  test("[id] value, type, strm") {
    val starts: TableFor3[Obj, Obj, Obj] =
      new TableFor3[Obj, Obj, Obj](("lhs", "rhs", "result"),
        //////// INT
        (int(2), int.id(), int(2)),
        (int(-2), int.id(), int(-2)),
        (int, int.id(), int.id()),
        (int(1, 2, 3), int.q(3).id(), int(1, 2, 3)),
        //////// REAL
        (real(2.0), __.id(), real(2.0)),
        (real(2.0), real.id().q(10), real(2.0).q(10)),
        (real(2.0).q(5), real.q(5).id().q(10), real(2.0).q(50)),
        (real(-2.0), real.one(), real(1.0)),
        (real, __.id(), real.id()),
        (real(1.0, 2.0, 3.0), real.q(3).id(), real(1.0, 2.0, 3.0)),
        (real(1.0, 2.0, 3.0), __.id().q(10), real(real(1.0).q(10), real(2.0).q(10), real(3.0).q(10))),
        (real(1.0, 2.0, 3.0), real.q(3).id().q(10).id(), real(real(1.0).q(10), real(2.0).q(10), real(3.0).q(10))),
        (real(1.0, 2.0, 3.0), __.id().q(10).id().q(5), real(real(1.0).q(50), real(2.0).q(50), real(3.0).q(50))),
        //////// STR
        (str("a"), str.id(), str("a")),
        (str.id(), str.id(), str.id().id()),
        (str("a", "b", "c"), str.q(3).id(), str("a", "b", "c")),
      )
    forEvery(starts) { (lhs, rhs, result) => TestUtil.evaluate(lhs, rhs, result)
    }
  }
}