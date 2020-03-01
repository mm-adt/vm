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

import org.mmadt.storage.StorageFactory.int
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class OneInstTest extends FunSuite {
  test("[one] w/ int value"){
    assertResult(int(1))(int(0).one())
    assertResult(int(1))(int(1).one())
    assertResult(int(1))(int(1).plus(100).one())
    assertResult(int(1).q(10))(int(1).q(10).plus(100).one())
  }
  test("[one] w/ int type"){
    assertResult("int[one]")(int.one().toString)
    assertResult("int{10}[one]")(int.q(10).one().toString)
  }
}
