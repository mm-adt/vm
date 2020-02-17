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

package org.mmadt.language.model.examples

import org.mmadt.language.model.Model
import org.mmadt.language.obj.`type`.IntType
import org.mmadt.language.obj.value.IntValue
import org.mmadt.language.obj.{Obj, Rec, Str}
import org.mmadt.processor.obj.`type`.CompilingProcessor
import org.mmadt.storage.obj.{int, rec, str}
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class SocialModelTest extends FunSuite {

  val nat: IntType = int("nat") //
  def nat(java: Long): IntValue = int("nat")(java)

  test("variable rewrites") {
    val processor = new CompilingProcessor(
      Model(nat -> int.is(int.gt(int(0)))))

    val marko: Rec[Str, Obj] = rec("person")(str("name") -> str("marko"), str("age") -> nat(29))
    val kuppitz: Rec[Str, Obj] = rec("person")(str("name") -> str("kuppitz"), str("age") -> nat(25))
    assertResult("person['name':'marko','age':nat[29]]")(marko.toString)
    assertResult("person")(marko.put(str("friend"), kuppitz).name)
  }

  test("nat rewrite") {
    val processor = new CompilingProcessor[IntType, IntType](
      Model(nat -> (int <= int.is(int.gt(int(0))))))

    assertResult(int.is(int.gt(0)).plus(34).is(int.gt(45)))(processor.apply(nat, nat.plus(34).is(nat.gt(45))).next().obj())
  }

}
