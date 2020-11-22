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
package org.mmadt.language.model

import org.mmadt.language.obj.Obj.{intToInt, stringToStr, symbolToToken}
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp.MM
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.storage.StorageFactory.{str, _}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmTest extends BaseInstTest(
  testSet("mm model table test", MM,
    comment("int"),
    testing(int, int.plus(0).plus(plus(0).plus(5)).plus(0), int.plus(int.plus(5)), "int+0[plus,+0+5]+0"),
    testing(int, mult(0), 0, "int*0"),
    testing(int, plus(0), int, "int+0"),
    testing(int, plus(2).mult(1), int.plus(2), "int[plus,2][mult,1]"),
    testing(int, neg.neg.plus(1).neg.neg, int.plus(1), "int[neg][neg][plus,1][neg][neg]"),
    testing(int, int.one.q(5), 1.q(5), "int[one]{5}"),
    // testing(int.q(5), one, 1.q(5), "int{5} => [one]"),
    // testing(int.q(5), int.q(5).one, 1.q(5), "int{5} => int{5}[one]"),
    testing(int, int.zero.q(40), 0.q(40), "int[zero]{40}"),
    //testing(int.q(10), int.q(10).mult(zero).q(2).plus(10).q(3), 10.q(60), "int{10}[mult,[zero]]{2}[plus,10]{3}"),
    testing(int, int.mult(one).plus(10), int.plus(10), "int[mult,[one]][plus,10]"),
    comment("str"),
    testing(str, plus(""), str, "str[plus,'']"),
    testing("marko".q(5), str.q(5).plus(" ").q(2).plus("rodriguez".q(100)), "marko rodriguez".q(10), "'marko'{5} => str{5}[plus,' ']{2}[plus,'rodriguez'{100}]"),
    comment("poly"),
    testing((1 `,` 2), a('poly), true, "(1,2)[a,poly]"),
    testing(5, a('poly), false, "5[a,poly]"),
  ))
