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
import org.mmadt.language.obj.op.map.GetOp
import org.mmadt.language.obj.value.{IntValue, StrValue}
import org.mmadt.language.obj.{Lst, Obj}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class GetInstTest extends FunSuite with TableDrivenPropertyChecks {

  test("[get] w/ lst values") {
    val check: TableFor3[Lst[StrValue], IntValue, StrValue] =
      new TableFor3[Lst[StrValue], IntValue, StrValue](("list", "key", "value"),
        (vlst[StrValue]("a"), 0, str("a")),
        (vlst[StrValue]("a", "b"), 0, "a"),
        (vlst[StrValue]("a", "b", "c"), 1, "b"),
        (vlst[StrValue]("d", "b", "c"), 2, "c"),
      )
    forEvery(check) { (alst, akey, avalue) => {
      assertResult(avalue)(alst.get(akey))
      assertResult(avalue)(GetOp(akey).exec(alst.asInstanceOf[GetOp.GetType[IntValue, StrValue]]))
    }
    }
  }

  test("[get] w/ lst value exception") {
    assertThrows[LanguageException] {
      vlst[StrValue]("a", "b", "c").get(-1)
    }
    assertThrows[LanguageException] {
      vlst[StrValue]("a", "b", "c").get(3)
    }
    assertThrows[LanguageException] {
      vlst.get(0)
    }
  }

  test("[get] lineage") {
    val marko = rec(str("name") -> str("marko"), str("age") -> int(29))
    assertResult(2)(rec.get(str("name"), str).plus(" rodriguez").lineage.length)
  }


  test("[get] w/ rec value") {
    val marko = rec(str("name") -> str("marko"), str("age") -> int(29))
    assertResult(str("marko"))(marko.get(str("name")))
    assertResult(int(29))(marko.get(str("age")))
    assertThrows[NoSuchElementException] {
      marko.get(str("bad-key"))
    }
  }
}
