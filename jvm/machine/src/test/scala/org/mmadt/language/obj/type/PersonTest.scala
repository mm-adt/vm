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

package org.mmadt.language.obj.`type`

import org.mmadt.language.obj.Rec
import org.mmadt.language.obj.value.{ObjValue, StrValue}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

import scala.collection.immutable.ListMap

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class PersonTest extends FunSuite {

  test("person rec") {
    val marko: Rec[StrValue, ObjValue] = rec(str("name") -> str("marko"), str("age") -> int(29))
    assertResult(ListMap(str("name") -> str("marko"), str("age") -> int(29)))(marko.gmap)
    assertResult("['name'->'marko','age'->29]")(marko.toString)
    assertResult("rec")(marko.name)
    assertResult(str("marko"))(marko ==> rec.get(str("name"), str))
    assertResult(int(29))(marko ==> rec.get(str("age"), int))
  }
}

