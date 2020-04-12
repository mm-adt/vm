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

package org.mmadt.storage.obj.value

import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VStrTest extends FunSuite {
  test("str value") {
    assertResult(str("mar"))(str("m").plus("a").plus("r"))
    assertResult(str("mar"))(str("m") + "a" + "r")
    assertResult(btrue)(str("marko").eqs(str("marko")))
    assertResult(btrue)(str("marko").eqs("marko"))
    assertResult(btrue)(str("m").gt(str("a")))
    assertResult(btrue)(str("m").gt("a"))
    assertResult(bfalse)(str("m").gt("r"))
  }
  test("str value quantifiers") {
    assertResult(str("marko").q(2))(str("marko").q(2) ==> str.q(2))
    assertResult(str("marko").q(2))(str("mar").q(2) ==> str.q(2).plus(str("ko")))
    assertResult(str("marko").q(2))(str("mar").q(2) ==> str.q(2).plus(str("k")).plus(str("o").q(34)))
    assertResult(str("marko").q(4))(str("mar").q(2) ==> str.q(2).plus(str("k")).plus(str("o").q(34)).q(2))
    assertResult(str("marko").q(200))(str("mar").q(2) ==> str.q(2).plus(str("k")).q(10).plus(str("o").q(34)).q(10))
  }
  test("str compute") {
    assertResult(str("marko"))(str("m") ==> str.plus("a").plus("r").plus("k").plus("o"))
    assertResult(str("marko"))(str("m") ==> str.plus(str("a").plus(str("r").plus(str("k").plus("o")))))
    assertResult(str("mmamarmarkmarko"))(str("m") ==>
      str.to("a").plus("a")
        .to("b").plus("r")
        .to("c").plus("k")
        .to("d").plus("o")
        .to("e")
        .map(str.from("a")
          .plus(str.from("b"))
          .plus(str.from("c"))
          .plus(str.from("d"))
          .plus(str.from("e"))))
    assertResult(str("marko"))(str("m") ===> str.plus("a").plus("r").plus("k").plus("o").path().get(5))
    assertResult(str("marko"))(str("m") ===> str.plus("a").plus("r").plus("k").plus("o").path().get(3, str).plus("k").plus("o").path().get(9))
  }
}