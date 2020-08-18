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

package org.mmadt.processor.inst.map

import org.mmadt.TestUtil
import org.mmadt.language.LanguageException
import org.mmadt.language.obj.Obj._
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.map.GetOp
import org.mmadt.language.obj.{Int, Obj, Str}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class GetInstTest extends FunSuite with TableDrivenPropertyChecks {

  test("[get] value, type, strm") {
    val starts: TableFor3[Obj, Obj, Obj] =
      new TableFor3[Obj, Obj, Obj](("lhs", "rhs", "result"),
        //////// ,-rec
        (str("a") -> int(1) `_,` str("b") -> int(2), rec[Str, Int].get(str("a")), int(1)),
        (str("a") -> int(1) `_,` str("a") -> int(2), rec[Str, Int].get(str("a")), int(1, 2)),
        (str("a") -> int(1) `_,` str("a") -> int(1), rec[Str, Int].get(str("a")), int(1).q(2)),
        (int(1) -> int(1) `_,` int(100) -> int(2) `_,` int(200) -> int(3), rec[Obj, Obj].get(int.is(gt(50))), int(2, 3)),
        //////// |-rec
        (str("name") -> str("marko") `_,` str("age") -> int(29), get("name", str).plus(" rodriguez"), "marko rodriguez"),
        // (str("name") -> str("marko") `_,` str("age") -> int(29), get("name", str).plus(" rodriguez").path(id()`;`id()).merge.count(), 4),
        // (str("name") -> str("marko") `_,` str("age") -> int(29), get("bad-key"), zeroObj),
        //////// |-lst
        ("a" |, lst.get(0), "a"),
        ("a" | "b", get(0), "a"),
        //("a" | "b" | "c", get(1), "b"),
        //("d" | "b" | "c", get(2), "c"),
      )
    forEvery(starts) { (lhs, rhs, result) => TestUtil.evaluate(lhs, rhs, result, if (rhs.trace.size == 1) GetOp(rhs.trace.head._2.arg0[Obj]) else null)
    }
  }

  // TODO: get exceptions into the table harness
  test("[get] w/ lst value exception") {
    assertThrows[LanguageException] {
      (str("a") | "b" | "c").get(-1)
    }
    assertThrows[LanguageException] {
      (str("a") | "b" | "c").get(3)
    }
    assertThrows[LanguageException] {
      assertResult(obj)(lst[Obj]("|").get(0))
    }
  }
}
