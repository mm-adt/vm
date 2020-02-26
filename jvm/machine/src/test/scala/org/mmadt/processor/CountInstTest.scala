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

import org.mmadt.storage.obj._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class CountInstTest extends FunSuite {

  test("[count] w/ int"){
    assertResult(int(1))(int(2).count())
    assertResult(int(3))((int(1,2,3) ===> int.count()).next)
    assertResult(int(3))((int(1,2,3) ===> int.plus(int(10)).count()).next)
    assertResult(int(2))((int(0,1) ===> int.choose(int.is(int.gt(int(0))) -> int,int -> int).count()).next)
    assertResult(int(17))((int(int(0).q(int(10)),int(1).q(int(7))) ===> int.q(*).choose(int.q(*).is(int.q(*).gt(int(0))) -> int,int -> int).count()).next) // TODO: need smarter handling of strm compilations with quantifiers
    assertResult(int(13))((int(int(0).q(int(10)),int(1).q(int(3))) ===> int.q(*).plus(int(10)).count()).next)
  }

}
