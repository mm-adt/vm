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

package org.mmadt.processor

import org.mmadt.machine.obj.impl.obj.int
import org.mmadt.machine.obj.theory.obj.`type`.IntType
import org.mmadt.machine.obj.theory.obj.value.strm.IntStrm
import org.mmadt.processor.impl.FastProcessor
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class FastProcessorTest extends FunSuite {

  test("fast processor") {
    val f = new FastProcessor[IntStrm, IntType]()
    println(f.apply(int(1, 2, 2, 4, 3, 6, 7, 8), int.plus(1).mult(5).mult(10).is(int.gt(200))).toList)
  }
}
