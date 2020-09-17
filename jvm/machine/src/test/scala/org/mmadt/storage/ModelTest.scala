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
import org.mmadt.language.obj.Obj.{intToInt, tupleToRecYES}
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.trace.ModelOp
import org.mmadt.language.obj.op.trace.ModelOp.EMPTY
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Lst, Obj, Rec}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ModelTest extends FunSuite {
  val tp3_kv:Type[Obj] = __.model(model('tpkv))
  val kv:Type[Obj] = __.model(model('kv))
  val tp3:Type[Obj] = __.model(model('tp))

  test("model naming") {
    assertResult("mm")(model('mm).name)
    assertResult("mm")(model('mm).merging(model('mm)).name)
    assertResult("mm")(model('mm).merging(EMPTY).name)
    val name1 = model('mm).merging(model('kv)).name
    assert(name1.contains(ModelOp.MODEL_EDIT))
    assert(name1.startsWith("mm" + ModelOp.MODEL_EDIT))
    Integer.valueOf(name1.substring(name1.indexOf(ModelOp.MODEL_EDIT) + 1, name1.length - 1))
    val name2 = model('kv).merging(model('mm)).name
    assert(name2.contains(ModelOp.MODEL_EDIT))
    assert(name2.startsWith("kv" + ModelOp.MODEL_EDIT))
    Integer.valueOf(name2.substring(name2.indexOf(ModelOp.MODEL_EDIT) + 2, name1.length - 1))
  }

  test("[tp3] model") {
    val record1a = rec(
      str("id") -> int(1),
      str("label") -> str("person"))
    assertResult('vertex(record1a))(record1a ==> tp3 `=>` as('vertex))
    ///
    val record2a = str("id") -> int(1) `_,`
      str("label") -> str("person") `_,`
      str("properties") -> (str("name") -> str("marko"))
    val record2b:Rec[_, _] = 'vertex(str("id") -> int(1) `_,` str("label") -> str("person") `_,` str("properties") -> 'property(str("name") -> str("marko")))
    assertResult(record2b)(record2a ==> tp3 `=>` __("vertex"))
    assertResult(record2b)(record2a ==> tp3 `=>` as('vertex))
    ///
    val record3 = 'vertex(rec(
      str("id") -> int(1),
      str("label") -> str("person"),
      str("properties") -> 'property(rec(str("id") -> str("marko")))))
    assertThrows[LanguageException] {
      record3 ==> tp3 `=>` as('vertex)
    }
  }

  test("[kv] model") {
    val record1 = str("k") -> int(1) `_,` str("v") -> str("marko")
    assertResult('kv(record1))(record1 ==> kv `=>` as('kv))
  }


  test("[tp3<=kv] functor") {
    val record1a:Rec[StrValue, Obj] =
      str("k") -> (str("vertex") `,` int(1)) `_,`
        str("v") -> rec(str("name") -> str("marko"))
    val record1b =
      'vertex(str("id") -> int(1) `_,`
        str("label") -> str("vertex") `_,`
        str("properties") -> 'property(str("name") -> str("marko")))
    assertResult('kv(record1a))(record1a ==> kv `=>` as('kv))
    assertResult(record1b)(record1a ==> kv `=>` as('kv) `=>` tp3 `=>` tp3_kv `=>` as('vertex))
    //

    val record2a =
      str("k") -> (str("vertex") `,` int(1)) `_,`
        str("v") -> (str("label") -> str("person") `_,` str("name") -> str("marko"))
    val record2b =
      'vertex(str("id") -> int(1) `_,`
        str("label") -> str("person") `_,`
        str("properties") -> 'property(str("label") -> str("person") `_,` str("name") -> str("marko")))
    assertResult('kv(record2a))(record2a ==> kv `=>` as('kv))
    assertResult(record2b)(record2a ==> kv `=>` as('kv) `=>` tp3_kv `=>` as('vertex))
    assertResult(record2b)(record2a ==> tp3_kv `=>` as('vertex))
    // (edge<=kv:('k'->(is=='edge',obj),'v'->('link'->(obj;obj),str->obj{*}))<x>-<
    val edge1:Rec[StrValue, Obj] = rec(str("k") -> (str("edge") `,` 7), str("v") -> rec(str("link") -> (1 `;` 1)))
    val store:Lst[Rec[StrValue, Obj]] = record1a `;` edge1
    val s:Obj = store ==> kv `=>` 'store
    println(s)
    assertResult(btrue)(s ==> kv `=>` combine(id `;`).a('store))
    val g:Obj = s ==> tp3_kv `=>` 'graph
    println(g)
    assertResult(btrue)(g ==> tp3 `=>` a('graph))
  }
}
