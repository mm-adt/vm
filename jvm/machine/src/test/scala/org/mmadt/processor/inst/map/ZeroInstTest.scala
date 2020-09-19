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

import org.mmadt.language.LanguageException
import org.mmadt.language.obj.Obj.{doubleToReal, intToInt, stringToStr, tupleToRecYES}
import org.mmadt.language.obj.`type`.__._
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, excepting, testSet, testing}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ZeroInstTest extends BaseInstTest(
  testSet("[zero] table test",
    comment("bool"),
    testing(true, zero, false, "true[zero]"),
    testing(false, zero, false, "false[zero]"),
    comment("int"),
    testing(2, zero, 0, "2[zero]"),
    testing(-2, zero, 0, "-2[zero]"),
    testing(int, zero, int.zero, "int[zero]"),
    testing(int, int.zero, int.zero, "int => int[zero]"),
    testing(int(1, 2, 3), plus(0).zero, 0.q(3), "[1,2,3][plus,0][zero]"),
    testing(int(1, 2), int.q(2).plus(1).q(10).zero, 0.q(20), "[1,2] => int{2}[plus,1]{10}[zero]"),
    comment("real"),
    testing(2.0, zero, 0.0, "2.0[zero]"),
    testing(-2.0, zero, 0.0, "-2.0[zero]"),
    testing(real, zero, real.zero, "real[zero]"),
    testing(real, zero, real.zero, "real => [zero]"),
    testing(real(-1.0, -2.0, -3.0), zero, 0.0.q(3), "[-1.0,-2.0,-3.0][zero]"),
    testing(real(-1.0, -2.0, -3.0), plus(1.0).q(10).zero, 0.0.q(30), "[-1.0,-2.0,-3.0][plus,1.0]{10}[zero]"),
    testing(real(-1.0, -2.0, -3.0), real.q(3).plus(1.0).q(20).zero, 0.0.q(60), "[-1.0,-2.0,-3.0] => real{3}[plus,1.0]{20}[zero]"),
    comment("str"),
    testing("a", zero, "", "'a'[zero]"),
    testing("b", str.zero, "", "'b' => str[zero]"),
    testing(str, zero, str.zero, "str[zero]"),
    testing(str("a", "b", "c"), zero, "".q(3), "['a','b','c'][zero]"),
    comment("lst"),
    testing("a" `,`, zero, lst(), "('a')[zero]"),
    testing("a" `,`, zero.q(4), lst().q(4), "('a')[zero]{4}"),
    testing("a".q(2) `,`, zero.q(4), lst().q(4), "('a'{2})[zero]{4}"),
    testing(("a" `,`).q(2), zero.q(4), lst().q(8), "('a'){2}[zero]{4}"),
    testing("a" `,` "b" `,` "c", zero, lst(), "('a','b','c')[zero]"),
    testing("a" `,` "b" `,` "c", lst.zero, lst(), "('a','b','c') => lst[zero]"),
    comment("rec"),
    testing(str("a") -> int(1), zero, rec(), "('a'->1)[zero]"),
    testing(str("a") -> int(1) `_,` str("b") -> int(2), zero, rec(), "('a'->1,'b'->2,'c'->3)[zero]"),
    testing(str("a") -> int(1) `_,` str("b") -> int(2) `_,` str("c") -> int(3), rec.zero, rec(), "('a'->1,'b'->2,'c'->3) => rec[zero]"),
    comment("exceptions"),
    // excepting('C,zero,LanguageException.unsupportedInstType('C,zero.inst),"C[zero]"),
  ))
