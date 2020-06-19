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

package org.mmadt.processor.obj.`type`.rewrite

import org.mmadt.language.obj.`type`.__
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class AlgebraRewriteTest extends FunSuite {

  test("int ring rewrites") {
    val intAlgebra = int
      .rewrite(int <= (int.mult(1) `,`))
      .rewrite(int <= (int.plus(0) `,`))
      .rewrite((int.zero() `,`) <= (int.mult(0) `,`))
      .rewrite(int <= (int.neg().neg() `,`))
      .rewrite((int.zero() `,`) <= (int.plus(int.neg()) `,`))
    assertResult(int)(int ==> intAlgebra)
    assertResult(int)(int.mult(1) ==> intAlgebra)
    assertResult(int)(int.plus(0) ==> intAlgebra)
    assertResult(int)(int.plus(0).mult(1) ==> intAlgebra)
    assertResult(int)(int.plus(0).mult(1).plus(0) ==> intAlgebra)
    assertResult(int.zero())(int.mult(0).plus(0) ==> intAlgebra)
    assertResult(int.zero())(int.plus(0).mult(0) ==> intAlgebra)
    assertResult(int.zero())(int.plus(int.neg()) ==> intAlgebra)
    assertResult(int.zero())(int.mult(int.plus(int.neg())) ==> intAlgebra)
  }

  test("variable matching") {
    val rewrites = int.rewrite((int.plus(__("x")) `,`) <= (int.mult(__.to("x")) `,`))
    assertResult(int.plus(2))(int.mult(2) ==> rewrites)
  }

  /*test("int ring rewrites") {
    val model = Algebra.ring(int)
    val compiler = Processor.compiler(model)
    val evaluator = Processor.iterator(model)
    println(model)
    assertResult(int)(compiler(int + int.zero()))
    assertResult(int)(compiler(int.plus(int.zero()) + int.zero()))
    assertResult(int)(compiler(int + int.zero()))
    assertResult(int)(compiler(-(-int) + int.zero()))
    assertResult(int.zero())(compiler(int + -int))
    assertResult(-int)(compiler(int * -int.one()))
    assertResult(int.one())(compiler(int.one().mult(int.one()).plus(int.zero()).one().one()))
    assertResult(int.zero())(compiler(int.neg().plus(int(0)).neg().mult(int(1)).plus(int(1)).plus(int(0)).plus(int(-1))))
    assertResult(int.from("x").plus(int.from("y")).mult(int.from("x").plus(int.from("z"))))(compiler(int.to("x").mult(int.to("y").plus(int.to("z")))))
    // assertResult(int.from[IntType]("x").plus(int.from[IntType]("y")).mult(int.from[IntType]("x").plus(int.from[IntType]("z"))))(compiler(int.mult(int.plus(int))))
    //
    assertResult(int(200))(evaluator(int(10), int * (int + int)))
    assertResult(int(200))(evaluator(int(10), compiler(int * (int + int))))
    //assertResult(int(200))(evaluator(int(10),compiler(int.to("x").mult(int.to("y").plus(int.to("z"))))))
  }

  test("int monoid elements for fold") {
    val model = Algebra.ring(int)
    //assertResult(int(0))(Traverser.standard(int(0))(model.get(int.zero()).get).obj())
    //assertResult(int(1))(Traverser.standard(int(0))(model.get(int.one()).get).obj())
  }*/

}
