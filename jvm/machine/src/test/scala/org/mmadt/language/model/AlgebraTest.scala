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

package org.mmadt.language.model

import org.mmadt.processor.Processor
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class AlgebraTest extends FunSuite {

  test("int ring rewrites"){
    val compiler = Processor.compiler(Algebra.ring)
    println(Algebra.ring)
    assertResult(int)(compiler(int + 0))
    assertResult(int)(compiler(int + 0 + 0 + 0))
    assertResult(int)(compiler(int + int.zero()))
    assertResult(int)(compiler(-(-int + 0)))
    assertResult(-int)(compiler(int * -1))
    assertResult(int.zero())(compiler(int + -int))
    // assertResult(int)(compiler(int.neg().plus(int(0)).neg().mult(int(1)).plus(int(1)).plus(int(0)).plus(int(-1))))
    // assertResult(int)(compiler(int.to("x").mult(int.to("y").plus(int.to("z")))))
  }

}
