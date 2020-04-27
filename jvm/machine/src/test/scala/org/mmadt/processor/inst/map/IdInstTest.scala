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
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class IdInstTest extends FunSuite with TableDrivenPropertyChecks {
  test("[id] value, type, strm") {
    val starts: TableFor3[Obj, Obj, String] =
      new TableFor3[Obj, Obj, String](("query", "result", "type"),
        //////// INT
        (int(2).id(), int(2), "value"),
        (int(-2).id(), int(-2), "value"),
        (int.id(), int.id(), "type"),
        (int(1, 2, 3).id(), int(1, 2, 3), "strm"),
        //////// REAL
        (real(2.0).id(), real(2.0), "value"),
        (real(-2.0).one(), real(1.0), "value"),
        (real.id(), real.id(), "type"),
        (real(1.0, 2.0, 3.0).id(), real(1.0, 2.0, 3.0), "strm"),
        //////// STR
        (str("a").id(), str("a"), "value"),
        (str.id(), str.id(), "type"),
        (str("a", "b", "c").id(), str("a", "b", "c"), "strm"),
      )
    forEvery(starts) { (query, result, atype) => {
      assertResult(result)(query)
      atype match {
        case "value" => assert(query.isInstanceOf[Value[_]])
        case "type" => assert(query.isInstanceOf[Type[_]])
        case "strm" => assert(query.isInstanceOf[Strm[_]])
      }
    }
    }
  }
}