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
import org.mmadt.language.obj.Obj._
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.{Lst, Obj}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2, TableFor3}

class HeadInstTest extends FunSuite with TableDrivenPropertyChecks {

  test("[head] value, type, strm") {
    val starts: TableFor3[Obj, Obj, Obj] =
      new TableFor3[Obj, Obj, Obj](("lhs", "rhs", "result"),
        (int(1) `;` 2 `;` 3, lst.head, int(1)),
        (int(1) `;`, lst.head, int(1)),
        (strm(int(1) `;` 2 `;` 3, int(4) `;` 5), __.head, strm(1, 4)),
        (strm(int(1) `;` 2 `;` 3, int(1) `;` 5), __.head, 1.q(2)),
      )
    forEvery(starts) { (lhs, rhs, result) => TestUtil.evaluate(lhs, rhs, result)
    }
  }


  test("[head] anonymous type") {
    assertResult(str("a"))(("a" |) ==> __.head)
    assertResult(str("a"))(("a" | "b") ==> __.head)
    assertResult(str("a"))(("a" | "b" | "c") ==> __.head)
    //
    assertResult(str("a"))(("a" `;`) ==> __.head)
    assertResult(str("a"))(("a" `;` "b") ==> __.head)
    assertResult(str("a"))(("a" `;` "b" `;` "c") ==> __.head)
  }

  test("[head] w/ parallel poly") {
    val check: TableFor2[Lst[_], Obj] =
      new TableFor2(("parallel", "head"),
        (str("a") |, "a"),
        (str("a") | "b", "a"),
        (str("a") | "b" | "c", "a"),
        (str("d") | "b" | "c", "d"),
      )
    forEvery(check) { (left, right) => {
      assertResult(right)(left.head)
    }
    }
  }

  test("[head] w/ serial poly") {
    val check: TableFor2[Lst[_], Obj] =
      new TableFor2(("serial", "head"),
        (str("a") `;`, "a"),
        (str("a") `;` "b", "a"),
        (str("a") `;` "b" `;` "c", "a"),
        (str("d") `;` "b" `;` "c", "d"),
      )
    forEvery(check) { (left, right) => {
      assertResult(right)(left.head)
    }
    }
  }
}