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

package org.mmadt.language

import org.mmadt.machine.obj.impl.obj.int
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TypeInferenceTest extends FunSuite {


  test("type inference") {
    assertResult("int{0,3}<=int{3}[mult,5][is,bool{3}<=int{3}[gt,int{3}[plus,10]]]")((int.q(3) ==> (int.mult(5).is(int.gt(int.plus(10))))).toString)
  }

  test("model inference") {
    assertResult(int.plus(int))(int ==> int.model("ex").mult(2))
    assertResult(int(4))(int(2) ==> (int ==> int.mult(2)))
    assertResult(int(4))(int(2) ==> (int ==> int.model("ex").mult(int(2))))
    //println(model.get(int,int.mult(2)).nonEmpty)
  }


}