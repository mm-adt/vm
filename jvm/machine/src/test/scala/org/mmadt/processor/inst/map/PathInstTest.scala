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
import org.mmadt.language.obj.op.trace.PathOp
import org.mmadt.language.obj.op.trace.PathOp.VERTICES
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

class PathInstTest extends FunSuite with TableDrivenPropertyChecks {
  test("[path] value, type, strm") {
    val starts: TableFor3[Obj, Obj, Obj] =
      new TableFor3[Obj, Obj, Obj](("input", "type", "result"),
        (str("a"), __.plus("b").plus("c").path(VERTICES), str("a") `;` "ab" `;` "abc"),
        (str("a"), __.plus("b").plus(__.plus("c").plus("d")).plus("e").path(VERTICES), str("a") `;` "ab" `;` "ababcd" `;` "ababcde"),
        (str("a"), __.plus("b").plus(__.plus("c").plus("d")).plus("e").path(VERTICES).get(1).path(VERTICES), str("a") `;` "ab" `;` "ababcd" `;` "ababcde"),
        (int(1, 2, 3), __.plus(1).path(VERTICES), strm(List(int(1) `;` 2, int(2) `;` 3, int(3) `;` 4))),
        (int(1, 2, 3), __.plus(1).plus(2).path(VERTICES), strm(List(int(1) `;` 2 `;` 4, int(2) `;` 3 `;` 5, int(3) `;` 4 `;` 6))),
      )
    forEvery(starts) { (input, atype, result) => TestUtil.evaluate(input, atype, result, PathOp((__ `;` zeroObj)).q(atype.trace.head._2.q))
    }
  }
  test("[path] w/ int value") {
    assertResult(int(0) `;` 1 `;` 3 `;` 6 `;` 10)(int(0).plus(1).plus(2).plus(3).plus(4).path(VERTICES))
  }

}

