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
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class OneInstTest extends FunSuite with TableDrivenPropertyChecks {
  test("[one] value, type, strm") {
    val starts: TableFor2[Obj, Obj] =
      new TableFor2[Obj, Obj](("query", "result"),
        //////// INT
        (int(2).one(), int(1)),
        (int(2).one().q(10), int(1).q(10)),
        (int(2).q(10).one(), int(1).q(10)),
        (int(2).q(10).one().q(20), int(1).q(200)),
        (int(-2).one(), int(1)),
        (int.one(), int(1)),
        (int.one().q(10), int(1).q(10)),
        (int.q(10).one(), int(1).q(10)),
        (int.q(10).one().q(20), int(1).q(200)),
        (int(1, 2, 3).one(), int(1).q(3)),
        //////// REAL
        (real(2.0).one(), real(1.0)),
        (real(-2.0).one(), real(1.0)),
        (real.one(), real(1.0)),
        (real(-1.0, -2.0, -3.0).one(), real(1.0).q(3)),
        (real(-1.0, -2.0, -3.0).id().q(10).one(), real(1.0).q(30)),
        // (real(-1.0, -2.0, -3.0) ===> __.id().q(10).one(), real(1.0).q(30)),
      )
    forEvery(starts) { (query, result) => {
      assertResult(result)(query)
    }
    }
  }
}