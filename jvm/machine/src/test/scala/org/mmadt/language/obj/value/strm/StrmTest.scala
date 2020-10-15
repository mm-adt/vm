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

package org.mmadt.language.obj.value.strm

import org.mmadt.language.obj.Int
import org.mmadt.language.obj.Obj.intToInt
import org.mmadt.storage.StorageFactory.{strm, zeroObj}
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class StrmTest extends FunSuite {

  test("strm from stream") {
    val astrm = strm[Int](Stream[Int](1, 2, 3, 4))
    assertResult(strm[Int](11, 12, 13, 14))(astrm.plus(10))
    assertResult(strm[Int](10.q(2), 20, 30.q(3), 40))(strm[Int](Stream[Int](10, 10, 20, 30, 30, 30, 40)))
    assertResult(zeroObj)(strm(Stream()))
  }
}
