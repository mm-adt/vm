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

import org.mmadt.language.obj.`type`.__
import org.mmadt.storage.StorageFactory.int
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class IntInstTest extends FunSuite {
  test("[int] w/ __"){
    assertResult("int<=[int]")(__.int().toString)
    assertResult("int<=[int][plus,2]")(__.int().plus(2).toString)
    assertResult("bool<=[int][plus,2][gt,10]")(__.int().plus(2).gt(10).toString)
    assertResult(int.plus(6))(int(5) ==> __.int().plus(6))
    assertResult(int.plus(6))(int(5) ===> __.int().plus(6))
  }
}
