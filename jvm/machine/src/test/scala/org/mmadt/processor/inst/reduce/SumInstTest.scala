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
package org.mmadt.processor.inst.reduce

import org.mmadt.storage.StorageFactory.{*, +, int}
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class SumInstTest extends FunSuite {
  test("[sum] w/ int") {
    assertResult(int(2))(int(2).sum())
    assertResult(int(20))(int(2).q(10).sum())
    assertResult(int(12))(int(12) =>> int.sum())
    assertResult(int(0))(int(1) =>> int.is(int.gt(10)).sum())
    assertResult(int(0))(int(1, 2, 3) =>> int.q(*).is(int.gt(10)).sum())
    assertResult(int(6))(int(1, 2, 3).sum())
    assertResult(int(6))(int(1, 2, 3) =>> int.q(3).sum())
    assertResult(int(36))(int(1, 2, 3) =>> int.q(+).plus(10).sum())
    assertResult(int(133))(int(int(0).q(10), int(1).q(3)).plus(10).sum())
    assertResult(int(149))(int(int(0).q(10), int(1).q(3), 6).plus(10).sum())
    // assertResult(int(14))(int(int(0).q(10),int(1).q(3),6) ===> int.q(*).plus(10).count())
  }
}