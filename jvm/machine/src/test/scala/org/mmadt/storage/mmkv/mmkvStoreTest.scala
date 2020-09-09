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

package org.mmadt.storage.mmkv

import javax.script.ScriptContext
import org.mmadt.language.LanguageFactory
import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.obj.Rec._
import org.mmadt.language.obj.`type`.IntType
import org.mmadt.language.obj.value.{BoolValue, IntValue, StrValue}
import org.mmadt.language.obj.{Obj, Rec}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmkvStoreTest extends FunSuite {

  lazy val engine: mmADTScriptEngine = LanguageFactory.getLanguage("mmlang").getEngine.get()
  val file1: String = getClass.getResource("/mmkv/mmkv-1.mm").getPath
  val file2: String = getClass.getResource("/mmkv/mmkv-2.mm").getPath
  val file3: String = getClass.getResource("/mmkv/mmkv-3.mm").getPath
  val mmkv: String = "=mmkv"

  test("mmkv storage provider") {
    assert(engine.getBindings(ScriptContext.ENGINE_SCOPE).values().isEmpty)
  }

  test("mmkv store [get]") {
    val store = mmkvStore.open[IntValue, StrValue](file1)
    try {
      assertResult(str("marko"))(store.get(int(1)))
      assertResult(str("ryan"))(store.get(int(2)))
      assertResult(str("stephen"))(store.get(int(3)))
      assertResult(str("kuppitz"))(store.get(int(4)))
    } finally store.close()
  }

  test("mmkv store [count]") {
    val store: mmkvStore[IntType, Rec[StrValue, Obj]] = mmkvStore.open[IntType, Rec[StrValue, Obj]](file2)
    try {
      assertResult(rec[StrValue, Obj](g=(",", List(str("k") -> int, str("v") -> rec[StrValue, Obj](g=(",", List(str("name") -> str, str("age") -> int))).named("person")))).named("mmkv"))(store.schema)
      assertResult(4)(store.count())
    } finally store.close()
  }

  test("mmkv store [put]") {
    val store = mmkvStore.open[IntValue, BoolValue](file3)
    try {
      assertResult(rec(g=(",", List(str("k") -> int, str("v") -> bool))).named("mmkv"))(store.schema)
      store.clear()
      assertResult(0)(store.stream().drain.count(_ => true))
      assertResult(bfalse)(store.put(bfalse))
      assertResult(1)(store.stream().drain.count(_ => true))
      assert(store.stream().drain.map(x => x.gmap.fetch(str("k"))).exists(x => x.g == 0))
      assertResult(btrue)(store.put(45, btrue))
      assertResult(2)(store.stream().drain.count(_ => true))
      assert(store.stream().drain.map(x => x.gmap.fetch(str("k"))).exists(x => x.g == 0))
      assert(store.stream().drain.map(x => x.gmap.fetch(str("k"))).exists(x => x.g == 45))
      assertResult(btrue)(store.get(45))
      assertResult(bfalse)(store.get(0))
    } finally store.close()
  }

  test("mmkv store [close]/[clear]/[count]") {
    var store = mmkvStore.open[IntValue, BoolValue](file3)
    try {
      assertResult(rec(str("k") -> int, str("v") -> bool).named("mmkv"))(store.schema)
      store.clear()
      assertResult(bfalse)(store.put(0, bfalse))
      assertResult(1L)(store.count())
      store.close()
      store = mmkvStore.open[IntValue, BoolValue](file3)
      assertResult(bfalse)(store.get(0))
      assertResult(1L)(store.count())
      store.close()
      store = mmkvStore.open[IntValue, BoolValue](file3)
      assertResult(bfalse)(store.get(0))
      assertResult(1L)(store.count())
      store.clear()
      assertResult(0L)(store.count())
      store.close()
      store = mmkvStore.open[IntValue, BoolValue](file3)
      assertResult(0L)(store.count())
    } finally store.close()
  }

}
