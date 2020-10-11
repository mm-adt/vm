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

package org.mmadt.processor.inst.sideeffect

import org.mmadt.language.obj.Obj.tupleToRecYES
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Obj, Rec}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class PutInstTest extends FunSuite {
  test("[put] w/ rec value") {
    val marko:Rec[StrValue, Obj] = rec(str("name") -> str("marko"))
    val markoFull = marko.put(str("age"), int(29))
    assertResult((str("name") -> str("marko") `_,` str("age") -> int(29)))(markoFull)
    assertResult((str("name") -> str("marko") `_,` str("age") -> int(29)))(markoFull.put(str("name"), str("marko")))
    assertResult((str("name") -> str("kuppitz") `_,` str("age") -> int(29)))(markoFull.put(str("name"), str("kuppitz")))
    assertResult((str("name") -> str("marko") `_,` str("age") -> int(28)))(markoFull.put(str("age"), int(28)))
    // test rec key/value ordering
    assertResult(List(str("name") -> str("kuppitz"), str("age") -> int(29)))(markoFull.put(str("name"), str("kuppitz")).gmap)
    assertResult(List(str("name") -> str("marko"), str("age") -> int(28)))(markoFull.put(str("age"), int(28)).gmap)
    assertResult(int(29))(markoFull.get(str("age")))
  }
}
