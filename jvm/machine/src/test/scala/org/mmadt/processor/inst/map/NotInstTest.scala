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
import org.mmadt.language.obj.Bool
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.map.NotOp
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}

class NotInstTest extends FunSuite with TableDrivenPropertyChecks {

  test("[not] value, type, strm, anon combinations") {
    val starts: TableFor2[Bool, Bool] =
      new TableFor2[Bool, Bool](("query", "result"),
        (btrue, bfalse),
        (btrue.q(19), bfalse.q(19)),
        (bfalse, btrue),
        (int(5).gt(10), btrue),
        (int(5).gt(10).q(10), btrue.q(10)),
        (int.gt(10), int.gt(10).not(bool)),
        (int.gt(10).q(0), bool.q(qZero)),
        (int(13).q(2).and(int.gt(10), int.lt(15)).q(10), bfalse.q(20)),
      )
    forEvery(starts) { (left, right) => TestUtil.evaluate(left, __.not(__), right, NotOp(__)) }
  }
}
