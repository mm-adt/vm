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

package org.mmadt.processor.inst.trace

import org.mmadt.TestUtil
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.trace.AsOp
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

class AsInstTest extends FunSuite with TableDrivenPropertyChecks {
  test("[as] w/ values") {
    val check: TableFor3[Obj, Obj, Obj] =
      new TableFor3[Obj, Obj, Obj](("lhs", "rhs", "result"),
        // bool
        (true, __, true),
        (true, str, "true"),
        (true, str.plus("dat"), "truedat"),
        (true, str.as("false"), "false"),
        // int
        (3, __, 3),
        (3, __.mult(3), 9),
        (3, int, 3),
        (3, int.plus(3), 6),
        (3, int.gt(10), false),
        (3, __.plus(10), 13),
        (3, str, "3"),
        (3, str.plus("a"), "3a"),
        (int, int.plus(1), int.as(int.plus(1))),
        (int, real, int.as(real)),
        // real
        (4.0, __, 4.0),
        (4.0, real, 4.0),
        (4.0, real.plus(1.0), 5.0),
        (4.0, real.gt(2.0), true),
        (4.0, __.mult(3.0), 12.0),
        (4.0, int, 4),
        (4.0, int.plus(2), 6),
        (real.mult(2.0), int.plus(10), real.mult(2.0).as(int.plus(10))),
        // str
        ("3", str.plus("a"), "3a"),
        ("3", int, 3),
        ("3", int.plus(10), 13),
        ("3", real, 3.0),
        ("3", str, "3"),
        ("true", bool, true),
        ("false", bool, false),
        // lst
        ((int(1) `,` 2 `,` 3), __, (int(1) `,` 2 `,` 3)),
        ((int(1) `,` 2 `,` 3), str, "(1,2,3)"),
        ((int(1) `,` 2 `,` 3), (str `,` real), (str("1") `,` real(2.0))),
        ((int(1) `,` 2 `,` 3), (__.plus(1) `,` __.plus(2) `,` __.plus(3)), (int(2) `,` 4 `,` 6)),
        ((int(1) `,` 2 `,` 3), (int.plus(1) `,` int.plus(2) `,` int.plus(3)), (int(2) `,` 4 `,` 6)),
        ((int(1) `,` 2 `,` 3), (int(8) `,` 9 `,` 10), (int(8) `,` 9 `,` 10)),
        ((int(1) `,` 2 `,` 3), lst, lst),
        ((int `,` int.plus(7) `,` int), (int.plus(1) `,` int.plus(2) `,` int.plus(3)), (int.plus(1) `,` int.plus(2) `,` int.plus(3)) <= (int `,` int.plus(7) `,` int).as((int.plus(1) `,` int.plus(2) `,` int.plus(3)))),
        // rec
        //(rec(str("a") -> int(1), str("b") -> int(2), str("c") -> int(3)), rec[Str, Obj](str("a") -> __.plus(2), str("c") -> str.plus("3")), rec(str("a") -> int(3), str("c") -> str("33"))),
        //(rec(str("a") -> int(1), str("b") -> int(2), str("c") -> int(3)), rec[Str, Obj](str("a") -> __.plus(2), str -> int.plus(3)), rec(str("a") -> int(3), str("b") -> int(4,4,5))),
      )
    forEvery(check) { (left, right, result) => TestUtil.evaluate(left, __.as(right), result, AsOp(right))
    }
  }
}