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

package org.mmadt.processor.inst.map

import org.mmadt.language.LanguageException
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Coprod, Lst, Obj, Prod}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}

class HeadInstTest extends FunSuite with TableDrivenPropertyChecks {

  test("[head] w/ products") {
    val check: TableFor2[Coprod[_], Obj] =
      new TableFor2(("product", "head"),
        (coprod[StrValue]("a"), "a"),
        (coprod[StrValue]("a", "b"), "a"),
        (coprod[StrValue]("a", "b", "c"), "a"),
        (coprod[StrValue]("d", "b", "c"), "d"),
      )
    forEvery(check) { (left, right) => {
      assertResult(right)(left.head())
    }
    }
  }

  test("[head] w/ coproducts") {
    val check: TableFor2[Prod[_], Obj] =
      new TableFor2(("product", "head"),
        (prod[StrValue]("a"), "a"),
        (prod[StrValue]("a", "b"), "a"),
        (prod[StrValue]("a", "b", "c"), "a"),
        (prod[StrValue]("d", "b", "c"), "d"),
      )
    forEvery(check) { (left, right) => {
      assertResult(right)(left.head())
    }
    }
  }

  test("[head] w/ values") {
    val check: TableFor2[Lst[_], Obj] =
      new TableFor2(("list", "head"),
        (vlst[StrValue]("a"), "a"),
        (vlst[StrValue]("a", "b"), "a"),
        (vlst[StrValue]("a", "b", "c"), "a"),
        (vlst[StrValue]("d", "b", "c"), "d"),
      )
    forEvery(check) { (left, right) => {
      assertResult(right)(left.head())
    }
    }
  }

  test("[head] exception") {
    assertThrows[LanguageException] {
      vlst().head()
    }
    assertThrows[LanguageException] {
      coprod().head()
    }
    assertThrows[LanguageException] {
      prod().head()
    }
  }
}