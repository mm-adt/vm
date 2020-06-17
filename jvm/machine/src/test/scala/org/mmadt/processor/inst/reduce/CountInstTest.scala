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

package org.mmadt.processor.inst.reduce

import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class CountInstTest extends FunSuite {
  test("[count] w/ int") {
    assertResult(int(1))(int(2).count())
    assertResult(int(10))(int(2).q(10).count())
    assertResult(int(0))(int(1) ===> int.is(int.gt(10)).count())
    assertResult(int(0))(int(1, 2, 3) ===> int.q(*).is(int.gt(10)).count())
    assertResult(int(3))(int(1, 2, 3).count())
    assertResult(int(3))(int(1, 2, 3) ===> int.q(3).count())
    assertResult(int(3))(int(1, 2, 3) ===> int.q(+).plus(10).count())
    // assertResult(int(2))((int(0,1) ===> int.q(*)-<(rec((int.is(int.gt(int(0))) -> int), (int -> int)).>-.count()))
    //assertResult(int(17))((int(int(0).q(int(10)),int(1).q(int(7))) ===> int.q(*).choose(int.q(*).is(int.q(*).gt(int(0))) -> int,int -> int).count())) // TODO: need smarter handling of strm compilations with quantifiers
    assertResult(int(13))(int(int(0).q(10), int(1).q(3)).plus(10).count())
    //  assertResult(int(13))(int(int(0).q(10),int(1).q(3)) ===> int.q(*).plus(10).count())
    assertResult(int(14))(int(int(0).q(10), int(1).q(3), 6).plus(10).count())
    // assertResult(int(14))(int(int(0).q(10),int(1).q(3),6) ===> int.q(*).plus(10).count())
  }
}
