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

package org.mmadt.language.obj.`type`

import org.mmadt.language.mmlang.mmlangScriptEngineFactory
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.{LstType, __}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

class LstTypeTest extends FunSuite with TableDrivenPropertyChecks {
  test("parallel expressions") {
    val starts: TableFor3[Obj, LstType[Obj], Obj] =
      new TableFor3[Obj, LstType[Obj], Obj](("lhs", "rhs", "result"),
        (int(1), lst(int `,` int), int(1).q(2)),
        (int(1), lst(int `,` int.plus(2)), int(1, 3)),
        (int(1), lst(int `,` int.plus(2).q(10)), int(1, int(3).q(10))),
        (int(1).q(5), lst(int `,` int.plus(2).q(10)), int(int(1).q(5), int(3).q(50))),
        (int(int(1), int(100)), lst(int | int), int(int(1), int(100))),
        (int(int(1), int(100)), lst(int `,` int), int(1, 1, 100, 100)),
        (int(int(1), int(100)), lst(int `,` int), int(int(1).q(2), int(100).q(2))),
        (int(int(1).q(5), int(100)), lst(int `,` int.plus(2).q(10)), int(int(1).q(5), int(3).q(50), int(100), int(102).q(10))),
        (int(int(1).q(5), int(100)), lst(int | int.plus(2).q(10)), int(int(1).q(5), int(100))),
        (int(1, 2), lst(int | lst(int | int)), int(1, 2)), // TODO: this is not computing the lst as a type
        (int(1, 2), lst(lst(int | int) | int), int(1, 2)), // TODO: this is not computing the lst as a type
        // (int(1, 2), lst(lst(int | int) | lst(int | int)), int(1, 2)),
        //(int(int(1), int(2)).-<(int `,` (int -< (int | int))), strm[Obj](List(int(1), int(1) |, int(2), int(2) |))),
        (int(1), lst(str | int), int(1)),
        //(strm(List(int(1), str("a"))).-<(str | int), strm(List(zeroObj | int(1), str("a") | zeroObj))),
      )
    forEvery(starts) { (lhs, rhs, result) => {
      assertResult(result)(new mmlangScriptEngineFactory().getScriptEngine.eval(s"[${lhs}]${rhs}"))
      assertResult(result)(rhs.exec(lhs))
      assertResult(result)(lhs.compute(__.via(__, rhs)))
    }
    }
  }
}
