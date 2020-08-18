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

package org.mmadt.storage

import org.mmadt.language.LanguageException
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Obj, Rec}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ModelTest extends FunSuite {
  val tp3_kv: Type[Obj] = functor(KV, TP3)
  val kv: Type[Obj] = __.model(model(KV))
  val tp3: Type[Obj] = __.model(model(TP3))
  val all: Type[Obj] = kv `=>` tp3 `=>` tp3_kv

  test("[tp3] model") {
    val record1a = rec(
      str("id") -> int(1),
      str("label") -> str("person"))
    assertResult(record1a.named("vertex"))(record1a ==> tp3.as(__("vertex")))
    ///
    val record2a = rec(
      str("id") -> int(1),
      str("label") -> str("person"),
      str("properties") -> rec(str("name") -> str("marko")))
    val record2b = rec(
      str("id") -> int(1),
      str("label") -> str("person"),
      str("properties") -> rec(str("name") -> str("marko")).named("property")).named("vertex")
    assertResult(record2b)(record2a ==> tp3.as(__("vertex")))
    ///
    val record3 = rec(
      str("id") -> int(1),
      str("label") -> str("person"),
      str("properties") -> rec(str("id") -> str("marko")).named("property")).named("vertex")
    assertThrows[LanguageException] {
      record3 ==> tp3.as(__("vertex"))
    }
  }

  test("[kv] model") {
    val record1 = rec(
      str("k") -> int(1),
      str("v") -> str("marko"))
    assertResult(record1.named("kv"))(record1 ==> kv.as(__("kv")))
  }


  test("[tp3<=kv] functor") {
    val record1a: Rec[StrValue, Obj] = rec(
      str("k") -> (str("vertex") `,` int(1)),
      str("v") -> rec(str("name") -> str("marko")))
    val record1b = rec(
      str("id") -> int(1),
      str("label") -> str("vertex"),
      str("properties") -> rec(str("name") -> str("marko")).named("property")).named("vertex")
    assertResult(record1a.named("kv"))(record1a ==> kv.as(__("kv")))
//    assertResult(record1b)(record1a ==> all ==> __.as(__("kv")).as(__("vertex")))
    //
    val record2a = rec(
      str("k") -> (str("vertex") `,` int(1)),
      str("v") -> rec(str("label") -> str("person"), str("name") -> str("marko")))
    val record2b = rec(
      str("id") -> int(1),
      str("label") -> str("person"),
      str("properties") -> rec(str("label") -> str("person"), str("name") -> str("marko")).named("property")).named("vertex")
    assertResult(record2a.named("kv"))(record2a ==> kv.as(__("kv")))
//    assertResult(record2b)(record2a ==> all.as(__("kv")).as(__("vertex")))
    assertResult(record2b)(record2a ==> all.as(__("vertex")))
    //
    //val edge1: Rec[StrValue, Obj] = rec(str("k") -> (str("edge") `,` 7), str("v") -> rec(str("outV") -> int(1), str("inV") -> int(1)))
    //val store: Lst[Rec[StrValue, Obj]] = lst(",", strm(List(record1a, edge1)))
    //val g: Type[Obj] = all.as(__("graph"))
    //println(g)
    //println(store ==> g)
  }
}
