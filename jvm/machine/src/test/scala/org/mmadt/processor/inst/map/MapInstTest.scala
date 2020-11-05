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

import org.mmadt.language.obj.Obj.intToInt
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp.{MM, NONE}
import org.mmadt.processor.inst.BaseInstTest
import org.mmadt.processor.inst.TestSetUtil.{comment, testSet, testing}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class MapInstTest extends BaseInstTest(
  testSet("[map] table testing", List.empty, // MM
    comment("int"),
    testing(2, map(1), 1, "2[map,1]"),
    testing(2.q(10), int.q(10).map(1), 1.q(10), "2{10} => int{10}[map,1]"),
    testing(2.q(10), int.q(10).map(int(1)).q(20), 1.q(200), "2{10}=>int{10}[map,1]{20}"),
    testing(2, map(1.q(10)), 1.q(10)),
    testing(2, map(int), 2),
    testing(2.q(3), int.q(3).map(int.id.q(5)), 2.q(15), "2{3} => int{3}[map,int[id]{5}]"),
    testing(2.q(3), map(int.id.q(5)), 2.q(15), "2{3}[map,int[id]{5}]"),
    testing(2.q(3), map(int.q(5)), 2.q(15), "2{3}[map,int{5}]"),
    //testing(2.q(3), map(-<(__ `;` __)) >-, 2.q(6), "2{3}[map,-<(_;_)]>-"),
    //testing(2.q(3), map(id.q(10).-<(__ `;` __)) >-, 2.q(60)),
    testing(2, map(mult(int)), int(4)),
    testing(int, map(2), int.map(2)),
    testing(int.q(10), map(2), int.q(10).map(2)),
    testing(int, map(int), int.map(int)),
    testing(int(1, 2, 3), map(2), int(2, 2, 2)),
    testing(int(1, 2, 3), map(2.q(10)), int(2.q(10), 2.q(10), 2.q(10))),
    testing(int(1, 2, 3), int.q(3).map(int(2)).q(10), int(2.q(10), 2.q(10), 2.q(10)), "[1,2,3]=>int{3}[map,2]{10}"),
    testing(int(1, 2, 3), map(int), int(1, 2, 3)),
    //testing(int(1, 2, 3), map(int.mult(int)), int(1, 4, 9)),
    //testing(int(1, 2, 3), int.q(3).map(mult(int)), int(1, 4, 9)),
    testing(int(1, 2, 3).q(3), map(id.q(10).-<(int(7) `,` int(7))) >-, int(7).q(180)),
    testing(int(1, 2, 3).q(3), map(id.q(10).-<(int(7) `,` int(7)).q(10)) >-, int(7).q(1800)),
    comment("real"),
    testing(2.0, real.map(1.0), 1.0, "2.0 => real[map,1.0]"),
    testing(2.0, map(real), 2.0, "2.0[map,real]"),
    testing(2.0, map(mult(real)), 4.0, "2.0[map,[mult,real]]"),
    testing(real, map(2.0), real.map(2.0), "real[map,2.0]"),
    testing(real, map(real), real.map(real), "real[map,real]"),
    testing(real(1.0, 2.0, 3.0), real.q(3).map(2.0), real(2.0, 2.0, 2.0), "[1.0,2.0,3.0]=>real{3}[map,2.0]"),
    testing(real(1.0, 2.0, 3.0), map(real), real(1.0, 2.0, 3.0), "[1.0,2.0,3.0][map,real]"),
    //testing(real(1.0, 2.0, 3.0), real.q(3).map(real.mult(real)), real(1.0, 4.0, 9.0), "[1.0,2.0,3.0]=>real{3}[map,*real]"),
  )) {

  test("[map] w/ values") {
    assertResult(int(5))(1.plus(1).map(int(5)))
    assertResult(int(2))(1.plus(1).map(int))
    assertResult(int(20))(1.plus(1).map(int.mult(10)))
  }

  test("[map] w/ types") {
    assertResult("int[plus,1][map,int]")(int.plus(1).map(int).toString)
    assertResult("int[plus,1][map,int[mult,10]]")(int.plus(1).map(int.mult(10)).toString)
    assertResult(int(200))(int(18) =>> int.plus(1).map(int.mult(10)).plus(10))
    assertResult("int[plus,1][map,int[mult,10]]")(int.plus(1).map(int.mult(10)).toString)
    //
    assertResult(int(60))(int(5) =>> int.plus(1).map(int.mult(10)))
  }
}