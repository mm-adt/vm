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

import org.mmadt.machine.obj.impl.obj._
import org.mmadt.machine.obj.impl.traverser.RecursiveTraverser
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TraverserTest extends FunSuite {

  test("traverser state") {
    assertResult(Map(str("a") -> int(5)))(new RecursiveTraverser(int(3))(int.plus(2).to("a").mult(3)).state())
    assertResult(Map(str("a") -> int(5), str("b") -> int(15)))(new RecursiveTraverser(int(3))(int.plus(2).to("a").mult(3).to("b")).state())
  }

}