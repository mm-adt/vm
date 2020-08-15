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

package org.mmadt.processor.inst.branch

import org.mmadt.TestUtil
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.branch.CombineOp
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

class CombineInstTest extends FunSuite with TableDrivenPropertyChecks {
  test("[combine] value, type, strm") {
    val starts: TableFor3[Obj, Obj, Obj] =
      new TableFor3[Obj, Obj, Obj](("lhs", "rhs", "result"),
        (int(1) `;` 2 `;` 3, lst.combine(int.plus(1) `;` int.plus(2) `;` int.plus(3)), int(2) `;` 4 `;` 6),
        (int(1) `;` 2 `;` 3, lst.combine(int.plus(1) `;` int.plus(2)), int(2) `;` 4 `;` 4),
        (int(1) `;` 2 `;` 3, lst.combine(int.plus(1) `;`), int(2) `;` 3 `;` 4),
        (int(1) `;` 2 `;` 3, lst.combine(lst), zeroObj `;`),
        (int(1) `;` (int(2) `,` 3) `;` 4, lst.combine(int.plus(1) `;` lst[Int].>-.count() `;` int.plus(10)), int(2) `;` 2 `;` 14),
        ///
        (int(2) | 4, lst.combine(int.plus(2) `;` int.mult(10)), int(4) `;` 40),
        (int(2) `;` 4, lst.combine(int.plus(2) `;` int.mult(10)), int(4) `;` 40),
        (int(2) `;` 4, lst.combine(int.plus(2) | int.mult(10)), int(4) | zeroObj),
        (int(4) | zeroObj, __.combine(int.plus(2) | int.mult(10)), int(6) | zeroObj)
      )
    forEvery(starts) { (lhs, rhs, result) => TestUtil.evaluate(lhs, rhs, result, CombineOp(rhs.via._2.arg0[Lst[Obj]]))
    }
  }
}
