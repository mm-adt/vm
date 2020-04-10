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

import org.mmadt.language.obj.`type`.IntType
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class AsInstTest extends FunSuite {
  test("int[as,rec]") {
    assertResult(vrec(str("age") -> int(5)))(int(5) ===> int.as(trec(str("age") -> int)))
    assertResult(vrec(str("X") -> int(5), str("Y") -> int(15)))(int(5) ===> int.to("x").plus(10).to("y").as(trec(str("X") -> int.from[IntType]("x"), str("Y") -> int.from[IntType]("y"))))
    assertResult(str("14hello"))(int(5) ===> int.plus(2).mult(2).as(str).plus("hello"))
  }
}
