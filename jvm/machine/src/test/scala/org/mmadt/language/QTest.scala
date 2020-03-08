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

package org.mmadt.language

import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class QTest extends FunSuite {

  test("value quantifiers toString"){
    assertResult("3")(int(3).toString)
    assertResult("3{0}")(int(3).q(0).toString)
    assertResult("3{1,2}")(int(3).q(int(1),int(2)).toString)
    assertResult("3{1,2}")(int(3).q(1,2).toString)
  }

  test("type quantifiers toString"){
    assertResult("int")(int.toString)
    assertResult("int")(int.q(1).toString)
    assertResult("int")(int.q(1).q(1).q(10).q(1).toString)
    assertResult("int{0}")(int.q(0).toString)
    assertResult("int{?}")(int.q(0,1).toString)
    assertResult("int{+}")(int.q(1,Long.MaxValue).toString)
    assertResult("int{*}")(int.q(0,Long.MaxValue).toString)
    assertResult("int{1,2}")(int.q(1,2).toString)
    assertResult("int{1,2}")(int.q(1,2).toString)
    assertResult("bool{3}<=int{3}[gt,5]")(int.q(3).gt(5).toString)
    assertResult("int{?}<=int[is,bool<=int[gt,5]]")(int.is(int.gt(5)).toString)
    assertResult("int{?}<=int[is,bool<=int[gt,5]][plus,10]")(int.is(int.gt(5)).plus(10).toString)
  }
}
