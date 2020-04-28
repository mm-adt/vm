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
import org.mmadt.language.obj.`type`.__
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ZeroInstTest extends FunSuite with TableDrivenPropertyChecks {
  test("[zero] value, type, strm") {
    val starts: TableFor2[Obj, Obj] =
      new TableFor2[Obj, Obj](("query", "result"),
        //////// INT
        (int(2).zero(), int(0)),
        (int(-2).zero(), int(0)),
        (int.zero(), int(0)),
        (int(1, 2, 3).zero(), int(0).q(3)),
        (int(1, 2).plus(1).q(10).zero(), int(0).q(20)),
        //////// REAL
        (real(2.0).zero(), real(0.0)),
        (real(-2.0).zero(), real(0.0)),
        (real.zero(), real(0.0)),
        (real(-1.0, -2.0, -3.0).zero(), real(0.0).q(3)),
        (real(-1.0, -2.0, -3.0).plus(1.0).q(10).zero(), real(0.0).q(30)),
        // (real(-1.0, -2.0, -3.0) ===> __.plus(1.0).q(10).zero(), real(0.0).q(30)),
        //////// STR
        (str("a").zero(), str("")),
        (str("b").zero(), str("")),
        (str.zero(), str("")),
        (str("a", "b", "c").zero(), str("").q(3)),
        //////// PROD
        (prod(str("a")).zero(), prod()),
        //(prod(prod(str("a")), prod(str("b")), prod(str("c"))).zero(), prod().q(3)),
      )
    forEvery(starts) { (query, result) => {
      assertResult(result)(query)
    }
    }
  }
}
