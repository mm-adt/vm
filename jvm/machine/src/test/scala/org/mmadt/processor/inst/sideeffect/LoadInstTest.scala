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
import org.mmadt.language.LanguageException
import org.mmadt.language.obj.`type`.__
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

class LoadInstTest extends FunSuite {
  val file1: String = getClass.getResource("/load/source-1.mm").getPath
  test("[load] w/ [a] mapping") {
    assertResult(bfalse)(int(5).load(file1).a(__("person")))
    assertResult(btrue)(int(5).load(file1).a(__("vertex")))
    assertResult(btrue)(rec(str("name") -> str("marko"), str("age") -> int(29)).load(file1).a(__("person")))
    assertResult(bfalse)(rec(str("name") -> str("marko"), str("age") -> int(0)).load(file1).a(__("person")))
    assertResult(bfalse)(rec(str("age") -> int(29)).load(file1).a(__("person")))
    assertResult(btrue)(rec(str("name") -> str("marko"), str("age") -> int(29)).load(file1).get("age").a(__("nat")))
    assertResult(bfalse)(rec(str("name") -> str("marko"), str("age") -> int(0)).load(file1).get("age").a(__("nat")))
  }
  test("[load] w/ [as] mapping") {
    assertResult(rec(str("id") -> int(5)).named("vertex"))(int(5).load(file1).as(__("vertex")))
    assertThrows[LanguageException] {
      int(5).load(file1).as(__("person"))
    }
    //
    assertResult(rec(str("name") -> str("marko"), str("age") -> int(29).named("nat")).named("person"))(rec(str("name") -> str("marko"), str("age") -> int(29)).load(file1).as(__("person")))
    assertThrows[LanguageException] {
      rec(str("name") -> str("marko"), str("age") -> int(0)).load(file1).as(__("person"))
    }
    //
    assertResult(int(29).named("nat"))(rec(str("name") -> str("marko"), str("age") -> int(29)).load(file1).get("age").as(__("nat")))
    assertThrows[LanguageException] {
      println(rec(str("name") -> str("marko"), str("age") -> int(0)).load(file1).get("age").as(__("nat")))
    }
  }
}