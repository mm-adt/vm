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

import org.mmadt.language.LanguageException
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp.MMX
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil._
import org.mmadt.storage.StorageFactory._

class AsInstTest extends BaseInstTest(
  testSet("[as] w/ values", List(MMX),
    comment("bool"),
    testing(true, as(__), true, "true[as,_]"),
    IGNORING("eval-[3-5]")(true, as(str), "true", "true[as,str]"),
    //IGNORING("eval-[3-5]")(true, as(str.plus("dat")), "truedat", "true[as,str[plus,'dat']]"),
    //testing(true, as(str.as(str("false"))), "false"),
    comment("int"),
    //excepting(3, as('C), LanguageException.labelNotFound(3, "C"), "3[as,C]"),
    // excepting(3, juxta('C), LanguageException.labelNotFound(3, "C"), "3 => C"),
    // excepting('C(3), __,  LanguageException.typeNotInModel('C(3), 'C, Tokens.rec), "C:3"),
    testing(3, as(__), 3, "3[as,_]"),
    testing(3, as(mult(3)), 9, "3[as,[mult,3]]"),
    testing(3, as(int), 3, "3[as,int]"),
    testing(3, as(int.plus(3)), 6, "3[as,int+3]"),
    testing(3, int.as(int.gt(10)), false, "3 => int[as,int>10]"),
    testing(3, as(__.plus(10)), 13),
    IGNORING("eval-[3-5]")(3, as(str), "3"),
    //IGNORING("eval-[3-5]","query-2")(3, as(str.plus("a")), "3a"),
    testing(int, as(int.plus(1)), int.plus(1)),
    //testing(int, as(real), real<=int),
    comment("real"),
    testing(4.0, as(__), 4.0),
    testing(4.0, as(real), 4.0),
    testing(4.0, as(real.plus(1.0)), 5.0),
    testing(4.0, real.gt(2.0), true),
    testing(4.0, as(__.mult(3.0)), 12.0),
    //IGNORING("eval-[3-5]")(4.0, as(int), 4),
    //IGNORING("eval-[3-5]")(4.0, as(int.plus(2)), 6),
    //testing(real.mult(2.0), as(int.plus(10)), real.mult(2.0).as(int.plus(10))),
    comment("str"),
    //testing("3", as(str.plus("a")), "3a", "3[as,str+'a']"),
    //testing("3", as(3), 3, "3[as,3]"),
    //testing("3", as(int), 3, "3[as,int]"),
    //testing(3, int.as(plus(10)), 13, "3 => int[as,[plus,10]]"),
    //testing("3", as(real), 3.0, "3[as,real]"),
    testing("3", as(str), "3", "'3'[as,str]"),
    //testing("true", as(bool), true),
    //testing("false", as(bool), false),
    comment("lst"),
    testing((int(1) `;` 2 `;` 3), as(__), (int(1) `;` 2 `;` 3)),
    //testing((int(1) `;` 2 `;` 3), as(str), "(1;2;3)"),
    //testing((int(1) `,` 2 `,` 3), as((str `,` real `,` int)), (str("1") `,` 2.0 `,` 3)),
    //testing((int(1) `,` 2 `,` 3), as((__.plus(1) `,` __.plus(2) `,` __.plus(3))), (int(2) `,` 4 `,` 6)),
    //testing((int(1) `,` 2 `,` 3), (int`;`int`;`int)`=>` (int.plus(1) `,` int.plus(2) `,` int.plus(3)), (int(2) `,` 4 `,` 6)),
    testing((int(1) `,` 2 `,` 3), as((int(8) `,` 9 `,` 10)), (int(8) `,` 9 `,` 10)),
    testing((int(1) `,` 2 `,` 3), as(lst), (int(1) `,` 2 `,` 3)),
   // testing((int `,` int.plus(7) `,` int), as((int.plus(1) `,` int.plus(2) `,` int.plus(3))), (int.plus(1) `,` int.plus(2) `,` int.plus(3)) <= (int `,` int.plus(7) `,` int).as((int.plus(1) `,` int.plus(2) `,` int.plus(3))))
  )) {
  /*((int(1) `,` 2 `,` 3), (int `,` int `,` int), (int(1) `,` 2 `,` 3)),
  ((int(1) `,` 2 `,` 3), (int `,` __.branch(int `|` real) `,` int), (int(1) `,` 2 `,` 3)),*/
  // ((int(1) `,` 2 `,` 3), (int `,` __.branch(str`|`real) `,` int), (int(1) `,` 2 `,` 3)),
  // rec
  //(rec(str("a") -> int(1), str("b") -> int(2), str("c") -> int(3)), rec[Str, Obj](str("a") -> __.plus(2), str("c") -> str.plus("3")), rec(str("a") -> int(3), str("c") -> str("33"))),
  //(rec(str("a") -> int(1), str("b") -> int(2), str("c") -> int(3)), rec[Str, Obj](str("a") -> __.plus(2), str -> int.plus(3)), rec(str("a") -> int(3), str("b") -> int(4,4,5))),
}