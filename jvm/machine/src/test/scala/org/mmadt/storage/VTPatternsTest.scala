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

package org.mmadt.storage

import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VTPatternsTest extends FunSuite {

  /*  test("type instructions"){
      assert(int.plus(1).mult(2).test(int.plus(1).mult(2)))
      assert(int(10).test(int.from[IntType]("x")))
      assert(int.plus(10).test(int.plus(int.from[IntType]("x"))))
      assert(int.plus(10).test(int.plus(int.from[IntType]("x").plus(2))))
      assert(!int.plus(10).test(int.plus(int.from[IntType]("x").plus(2)).mult(20)))
    }*/

  test("type/type patterns on atomic objs") {
    assert(int.test(int))
    assert(bool.test(bool))
    assert(str.test(str))
    //
    assert(!int.plus(2).test(int))
    assert(!bool.test(bool.is(btrue)))
    assert(!str.test(str.plus("a")))
    //
    assert(int.plus(2).test(int.plus(2)))
    assert(str.plus("a").test(str.plus("a")))
    //
    assert(!int.named("nat").test(int))
    assert(!int.named("nat").test(int.is(int.gt(0))))
    assert(!int.named("nat").test(int.named("nat").is(int.gt(0))))
  }

  test("value/type patterns on atomic objs") {
    assert(str("m").test(str("m")))
    assert(!str("m").test(int(2)))
    assert(str("m").test(str))
    assert(!int.test(int(3)))
    assert(int(3).test(int))
  }

  test("value/type patterns on refinement types") {
    assert(int(6).test(int))
    assert(!int(6).test(int.q(0)))
    assert(int.plus(2).test(int.plus(2)))
    assert(!int.plus(2).test(int.plus(3)))
  }

  test("record value/type checking") {
    val markoLess = vrec(str("name") -> str("marko"))
    val marko = vrec(str("name") -> str("marko"), str("age") -> int(29))
    val markoMore = vrec(str("name") -> str("marko"), str("age") -> int(29), str("alive") -> bfalse)
    val person = trec(str("name") -> str, str("age") -> int)
    val personLess = trec(str("age") -> int)
    val markoLessName = vrec(name = "person", Map(str("name") -> str("marko")))
    val markoName = vrec(name = "person", Map(str("name") -> str("marko"), str("age") -> int(29)))
    val markoMoreName = vrec(name = "person", Map(str("name") -> str("marko"), str("age") -> int(29), str("alive") -> bfalse))
    val personName = trec(name = "person", Map(str("name") -> str, str("age") -> int))
    assert(marko.test(marko))
    assert(markoMore.test(markoMore))
    assert(markoLess.test(markoLess))
    assert(!markoLess.test(markoMore))
    assert(!markoMore.test(markoLess))
    assert(markoName.test(marko))
    assert(markoMoreName.test(markoMore))
    assert(markoLessName.test(markoLess))
    assert(markoLess.test(rec))
    assert(marko.test(rec))
    assert(markoMore.test(rec))
    assert(markoLessName.test(rec))
    assert(markoName.test(rec))
    assert(markoMoreName.test(rec))
    assert(!markoLess.test(person))
    assert(marko.test(person))
    assert(markoMore.test(person))
    assert(markoLessName.test(personName))
    assert(markoName.test(personName))
    assert(markoMoreName.test(personName))
    assert(!markoLessName.test(person))
    assert(markoName.test(person))
    assert(markoMoreName.test(person))
    assert(!markoLess.test(personName))
    assert(marko.test(personName))
    assert(markoMore.test(personName))
    assert(person.test(personName))
    assert(personName.test(personName))
    assert(personName.test(person))
    assert(person.test(personLess))
    assert(!personLess.test(person))
    assert(personLess.test(rec))
    assert(!rec.test(personLess))
    assert(!rec.test(person))
    assert(rec.test(rec))
    assert(vrec(Map.empty[Value[Obj], Value[Obj]]).test(rec))
  }
}