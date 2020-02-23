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

package org.mmadt.storage.obj.`type`

import org.mmadt.language.obj.op.{GtOp,PlusOp}
import org.mmadt.storage.obj.int
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class __Test extends FunSuite {

  test("__ type"){
    val x = __(PlusOp(int(4)),PlusOp(int.plus(int)),GtOp(__(PlusOp(int(2)))))
    assertResult(int.plus(int(4)).plus(int.plus(int)).gt(int.plus(int(2))))(x(int))
    //println(x)
    //println(int(5) ==> x(int))
    //println(int(5) ==> int.plus(int(4)).plus(int.plus(int)).plus(int.plus(int(2))))
  }

}
