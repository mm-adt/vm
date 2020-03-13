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

import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class MapInstTest extends FunSuite {
  test("[map] w/ values"){
    assertResult(int(5))(int(1).plus(1).map(int(5)))
    assertResult(int(2))(int(1).plus(1).map(int))
    assertResult(int(20))(int(1).plus(1).map(int.mult(10)))
  }
  test("[map] w/ types"){
    assertResult("int[plus,1][map,int]")(int.plus(1).map(int).toString)
    assertResult("int[plus,1][map,int[mult,10]]")(int.plus(1).map(int.mult(10)).toString)
    assertResult(int(200))(int(18) ==> int.plus(1).map(int.mult(10)).plus(10))
    assertResult("int[plus,1][map,int[mult,10]]")(int.plus(1).map(int.mult(10)).toString)
    //
    assertResult(int(60))(int(5) ==> int.plus(1).map(int.mult(10)))
  }
}