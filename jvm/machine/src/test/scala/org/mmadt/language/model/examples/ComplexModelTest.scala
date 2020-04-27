/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 *  mm-ADT is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU Affero General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 *  License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 *  You can be released from the requirements of the license by purchasing a
 *  commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language.model.examples

import org.mmadt.language.model.Model
import org.mmadt.language.obj.`type`.RecType
import org.mmadt.language.obj.value.{RealValue, StrValue}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ComplexModelTest extends FunSuite {

  private type Complex = RecType[StrValue, RealValue]
  val complex: Model = Model.simple()

  complex.put(trec(str("a") -> real, str("b") -> real), trec(str("a") -> real, str("b") -> real).named("cmplx"))
  val cmplx: Complex = complex("cmplx")
  complex.put(cmplx.plus(cmplx), trec(str("a") -> real(1.0), str("b") -> real(2.0)).named("cmplx"))
  println(complex)

  test("model atomic types") {
    //
    //val compilation = complex.apply(trec(str("a") -> real,str("b") -> real).named("cmplx").plus(trec(str("a") -> real(0.5),str("b") -> real(0.6)).named("cmplx")).is(true))
    //   println(compilation)
    //    println(complex(vrec(str("a") -> real(0.1),str("b") -> real(0.12))))
    //  println(vrec(str("a") -> real(0.1),str("b") -> real(0.12)) ==> (compilation,complex))
  }
}