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
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.map.OrOp
import org.mmadt.storage.StorageFactory.{bfalse, bool, btrue, int}
import org.scalatest.FunSuite
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor1, TableFor4}

class OrInstTest extends FunSuite with TableDrivenPropertyChecks {

  test("[or] value, type, strm, anon combinations") {
    val starts:TableFor4[Obj, Obj, Obj, String] =
      new TableFor4[Obj, Obj, Obj, String](("input", "type", "result", "kind"),
        (bfalse, __.or(btrue), btrue, "value"), // value * value = value
        (bfalse, __.or(bool), bfalse, "value"), // value * type = value
        (bfalse, __.or(__.or(bool)), bfalse, "value"), // value * anon = value
        //(bool, __.or(btrue), btrue, "value"), // type * value = type
        //(bool, __.or(bool), bool.or(bool), "type"), // type * type = type
        (bool(true, true, false), __.or(btrue), btrue.q(3), "value"), // strm * value = strm
        (bool(true, true, false), __.or(bool), bool(true, true, false), "strm"), // strm * type = strm
        (bool(true, true, false), __.or(__.or(bool)), bool(true, true, false), "strm"), // strm * anon = strm
      )
    forEvery(starts) { (input, atype, result, kind) => TestUtil.evaluate(input, atype, result, compile = false)
    }
  }

  test("[or] testing") {
    def maker(x:Obj with OrOp):Obj = x.q(2).or(bfalse).q(3).or(bfalse).q(10)

    val starts:TableFor1[OrOp with Obj] =
      new TableFor1("obj",
        bool,
        btrue,
        bfalse)
    forEvery(starts) { obj => {
      val expr = maker(obj)
      assert(obj.q != expr.q)
      assertResult(2)(expr.trace.length)
      assertResult((int(60), int(60)))(expr.q)
      assertResult((obj.q(2), OrOp(bfalse).q(3)))(expr.trace.head)
      assertResult((obj.q(2).or(bfalse).q(3), OrOp(bfalse).q(10)))(expr.trace.last)
    }
    }
  }
}
