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
import org.mmadt.language.Tokens
import org.mmadt.language.mmlang.mmlangScriptEngineFactory
import org.mmadt.language.obj._
import org.mmadt.language.obj.op.trace.JuxtOp
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor2}

class JuxtaInstTest extends FunSuite with TableDrivenPropertyChecks {

  test("[juxta] value, type, strm, anon combinations") {
    val starts: TableFor2[List[Obj], Obj] =
      new TableFor2[List[Obj], Obj](("query", "result"),
        // value/value
        (List(int(1).q(5)), int(1).q(5)),
        (List(int(1), int(2), int(3)), int(3)),
        (List(int(1), int(2).q(10), int(3)), int(3).q(10)),
        (List(int(1), int(2).q(10), int(3).q(2)), int(3).q(20)),
        // value/type
        (List[Int](int(1), int.plus(1)), int(2)),
        (List[Int](int(1), int.plus(10)), int(11)),
        (List[Int](int(1), int.plus(int)), int(2)),
        (List[Int](int(1), int.plus(int.plus(2))), int(4)),
        (List[Obj](int(1), int.plus(int.plus(2)).as(str), str.plus("a")), str("4a")),
        (List[Int](int(1), int.plus(1).q(0)), int(2).q(qZero)),
        // type/value
        (List[Int](int.plus(1), int(1)), int(1)),
        (List[Str](str, str("marko")), str("marko")),
        (List[Real](real.plus(1.0).q(10), real(13.0).q(2)), real(13.0).q(20)),
        // type/type
        (List(str), str),
        (List(str, str.id()), str.id()),
        (List(int, int.plus(1), int.plus(2)), int.plus(1).plus(2)),
      )
    forEvery(starts) { (left, right) => {
      println(left.map(_.toString).reduce((a, b) => a + Tokens.juxt_op + b))
     // assertResult(right)(new mmlangScriptEngineFactory().getScriptEngine.eval(s"${left.map(_.toString).reduce((a, b) => a + "=>" + b)}"))
      assertResult(right)(left.reduce((a, b) => a `=>` b))
      assertResult(right)(left.reduce((a, b) => JuxtOp(b).exec(a)))
    }
    }
  }
}
