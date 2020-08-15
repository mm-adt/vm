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
package org.mmadt.language.model

import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmTest extends FunSuite {
  val file1: String = str(getClass.getResource("/model/mm.mm").getPath).g

  test("mm-ADT int") {
    assertResult(int.plus(1))(int ==> int.model(file1).plus(1))
    assertResult(int)(int ==> int.model(file1).plus(0))
    assertResult(int)(int ==> int.model(file1).mult(1))
    assertResult(int(0))(int ==> int.model(file1).mult(0))
    assertResult(int.plus(7))(int ==> int.model(file1).plus(7).neg().neg().plus(0))
  }
}
