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

import org.mmadt.language.obj.`type`.__
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil._
import org.mmadt.storage.StorageFactory._

class AsInstTest extends BaseInstTest(
  testSet("[as] w/ values",
    comment("bool"),
    testing(true, __.as(__), true),
    testing(true, __.as(str), "true"),
    testing(true, __.as(str.plus("dat")), "truedat"),
    testing(true, __.as(str.as("false")), "false"),
    comment("int"),
    testing(3, __.as(__), 3),
    testing(3, __.as(__.mult(3)), 9),
    testing(3, __.as(int), 3),
    testing(3, __.as(int.plus(3)), 6),
    testing(3, __.as(int.gt(10)), false),
    testing(3, __.as(__.plus(10)), 13),
    testing(3, __.as(str), "3"),
    testing(3, __.as(str.plus("a")), "3a"),
    testing(int, __.as(int.plus(1)), int.as(int.plus(1))),
    testing(int, __.as(real), int.as(real)),
    comment("real"),
    testing(4.0, __.as(__), 4.0),
    testing(4.0, __.as(real), 4.0),
    testing(4.0, __.as(real.plus(1.0)), 5.0),
    testing(4.0, real.gt(2.0), true),
    testing(4.0, __.as(__.mult(3.0)), 12.0),
    testing(4.0, __.as(int), 4),
    testing(4.0, __.as(int.plus(2)), 6),
    testing(real.mult(2.0), __.as(int.plus(10)), real.mult(2.0).as(int.plus(10))),
    comment("str"),
    testing("3", __.as(str.plus("a")), "3a"),
    testing("3", __.as(int), 3),
    testing("3", __.as(int.plus(10)), 13),
    testing("3", __.as(real), 3.0),
    testing("3", __.as(str), "3"),
    testing("true", __.as(bool), true),
    testing("false", __.as(bool), false),
    comment("lst"),
    testing((int(1) `;` 2 `;` 3), __.as(__), (int(1) `;` 2 `;` 3)),
    testing((int(1) `;` 2 `;` 3), __.as(str), "(1;2;3)"),
    testing((int(1) `,` 2 `,` 3), __.as((str `,` real `,` int)), (str("1") `,` 2.0 `,` 3)),
    testing((int(1) `,` 2 `,` 3), __.as((__.plus(1) `,` __.plus(2) `,` __.plus(3))), (int(2) `,` 4 `,` 6)),
    testing((int(1) `,` 2 `,` 3), __.as((int.plus(1) `,` int.plus(2) `,` int.plus(3))), (int(2) `,` 4 `,` 6)),
    testing((int(1) `,` 2 `,` 3), __.as((int(8) `,` 9 `,` 10)), (int(8) `,` 9 `,` 10)),
    testing((int(1) `,` 2 `,` 3), __.as(lst), (int(1) `,` 2 `,` 3)),
    testing((int `,` int.plus(7) `,` int), __.as((int.plus(1) `,` int.plus(2) `,` int.plus(3))), (int.plus(1) `,` int.plus(2) `,` int.plus(3)) <= (int `,` int.plus(7) `,` int).as((int.plus(1) `,` int.plus(2) `,` int.plus(3))))
  )) {
  /*((int(1) `,` 2 `,` 3), (int `,` int `,` int), (int(1) `,` 2 `,` 3)),
  ((int(1) `,` 2 `,` 3), (int `,` __.branch(int `|` real) `,` int), (int(1) `,` 2 `,` 3)),*/
  // ((int(1) `,` 2 `,` 3), (int `,` __.branch(str`|`real) `,` int), (int(1) `,` 2 `,` 3)),
  // rec
  //(rec(str("a") -> int(1), str("b") -> int(2), str("c") -> int(3)), rec[Str, Obj](str("a") -> __.plus(2), str("c") -> str.plus("3")), rec(str("a") -> int(3), str("c") -> str("33"))),
  //(rec(str("a") -> int(1), str("b") -> int(2), str("c") -> int(3)), rec[Str, Obj](str("a") -> __.plus(2), str -> int.plus(3)), rec(str("a") -> int(3), str("b") -> int(4,4,5))),
}