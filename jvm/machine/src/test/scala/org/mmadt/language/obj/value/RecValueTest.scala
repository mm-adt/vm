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
package org.mmadt.language.obj.value

import org.mmadt.language.obj.Obj.tupleToRecYES
import org.mmadt.language.obj.Rec
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.TableDrivenPropertyChecks

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class RecValueTest extends FunSuite with TableDrivenPropertyChecks {

  test("rec value [split]/[merge]") {
    val crec: Rec[StrValue, IntValue] = str("a") -> int(1) `_,` str("b") -> int(2) `_,` str("c") -> int(3)
    val prec: Rec[StrValue, IntValue] = str("a") -> int(1) | str("b") -> int(2) | str("c") -> int(3)
    val srec: Rec[StrValue, IntValue] = str("a") -> int(1) `_;` str("b") -> int(2) `_;` str("c") -> int(3)

    assertResult(int(1, 2, 3))(crec.merge)
    assertResult(int(1))(prec.merge)
    assertResult(int(3))(srec.merge)

    assertResult(int(1, 2, 3))(int(10).split(crec).merge)
    assertResult(int(1))(int(10).split(prec).merge)
    assertResult(int(3))(int(10).split(srec).merge)
  }

}
