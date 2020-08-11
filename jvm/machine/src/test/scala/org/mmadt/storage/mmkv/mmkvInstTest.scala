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

import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.obj.{Obj, Rec}
import org.mmadt.language.{LanguageException, LanguageFactory}
import org.mmadt.processor.Processor
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmkvInstTest extends FunSuite {

  lazy val engine: mmADTScriptEngine = LanguageFactory.getLanguage("mmlang").getEngine.get()

  val mmkv: String = "=mmkv"
  val file1: String = getClass.getResource("/mmkv/mmkv-1.mm").getPath
  val file2: String = getClass.getResource("/mmkv/mmkv-2.mm").getPath
  val file3: String = getClass.getResource("/mmkv/mmkv-3.mm").getPath
  val file4: String = getClass.getResource("/mmkv/mmkv-4.mm").getPath
  val file5: String = getClass.getResource("/mmkv/mmkv-5.mm").getPath
  val file6: String = getClass.getResource("/mmkv/mmkv-6.mm").getPath
  val source4: String = getClass.getResource("/mmkv/source-4.mm").getPath
  val source5: String = getClass.getResource("/mmkv/source-5.mm").getPath
  val kv: String = getClass.getResource("/model/kv.mm").getPath
  val tp3: String = getClass.getResource("/model/tp3.mm").getPath
  val tp3_kv: String = getClass.getResource("/model/functor/tp3_kv.mm").getPath
  val social_kv: String = getClass.getResource("/model/functor/social_kv.mm").getPath

  test("mmkv parsing") {
    assertResult("mmkv{*}")(engine.eval(s"[=mmkv,'${file1}']").range.toString)
    assertResult("mmkv{4}")(engine.eval(s"1[=mmkv,'${file1}']").toString)
    //
    assertResult("mmkv{*}")(engine.eval(s"[=mmkv,'${file2}']").range.toString)
    assertResult("mmkv{4}")(engine.eval(s"1[=mmkv,'${file2}']").toString)
    //
    assertResult("mmkv{*}")(engine.eval(s"[=mmkv,'${file3}']").range.toString)
    assertResult(zeroObj)(engine.eval(s"1[=mmkv,'${file3}']"))
  }

  test("mmkv branch parsing") {
    assertResult(int(2, 3, 4, 5))(engine.eval(s"{1}[=mmkv,'${file1}'][get,'k'][plus,1]"))
    assertResult(int(100))(engine.eval(s"10[is==1->[=mmkv,'${file1}'][get,'k'][plus,1] | int -> 100]"))
    assertResult(int(2, 3, 4, 5))(engine.eval(s"{1}[is==1->[=mmkv,'${file1}'][get,'k'][plus,1] | int -> 100]"))
    assertResult(int(2, 3, 4, 5))(engine.eval(s"1[is==1->[=mmkv,'${file1}'][get,'k'][plus,1] | int -> 100]"))
    assertResult(int(1))(engine.eval(s"1[=mmkv,'${file1}'][is,[get,'k'][eq,1]][get,'k'][[is>3]->0 | _ -> 1]"))
    assertResult(int(1))(engine.eval(s"1[=mmkv,'${file1}'][is,[get,'k'][eq,1]][[is,[get,'k'][gt,3]]->0 | _ -> 1]"))
    assertResult(int(1, 1, 1, 0))(engine.eval(s"1[=mmkv,'${file1}'][[get,'k'][is>3]->0 | _ -> 1]"))
  }

  test("mmkv file-2 parsing") {
    assertResult(s"mmkv{*}<=_[=mmkv,'${file2}']")(engine.eval(s"[=mmkv,'${file2}']").toString)
    assertResult(str("marko!", "stephen!"))(engine.eval(s"{1}[=mmkv,'${file2}'][get,'v'][is,[get,'age'][gt,28]][get,'name'][plus,'!']"))
    assertResult(str("marko!", "stephen!"))(engine.eval(s"{1}[=mmkv,'${file2}'].v[is.age>28].name+'!'"))
    assertResult(str("marko!", "stephen!"))(engine.eval(s"1[=mmkv,'${file2}'].v[is.age>28].name+'!'"))
  }

  test("[=mmkv] with mmkv-1.mm") {
    assertResult(s"mmkv{*}<=obj[=mmkv,'${file1}']")(obj.=:(mmkv)(str(file1)).toString)
    assertResult("rec{4}")(int(1).=:(mmkv)(str(file1)).toString)
    assertResult(int(1, 2, 3, 4))(Processor.iterator(int(4), int.=:[Rec[Obj, Obj]](mmkv)(str(file1)).get(str("k"), int)))
    // assertResult("mmkv:('k'->1,'v'->'marko')")((int(1) ==> int.=:[RecType[Obj, Obj]](mmkv)(str(file1))).toStrm.values.iterator.next().toString)
  }

  test("mmkv rewrites") {
    assertResult("mmkv")(engine.eval(s"[=mmkv,'${file2}']").name)
    assertResult(int.q(*))(engine.eval(s"[=mmkv,'${file2}'][get,'k']").range)
    assertResult(int.q(*))(engine.eval(s"[=mmkv,'${file2}'][get,'v'][get,'age']").range)
    assertResult(str.q(*))(engine.eval(s"[=mmkv,'${file2}'][get,'v'][get,'name']").range)
    engine.eval(s"[=mmkv,'${file2}'][put,'v',6]")
    assertThrows[LanguageException] {
      engine.eval(s"1[=mmkv,'${file2}'][put,'v',6]")

    }
    assertThrows[LanguageException] {
      engine.eval(s"1[=mmkv,'${file2}'][put,'k',346]")
    }
    // assertResult(s"mmkv{*}<=_[=mmkv,'${file2}','getByKeyEq',1]")(engine.eval(s"[=mmkv,'${file2}'][is,[get,'k'][eq,1]]").toString)
    // assertResult(s"mmkv{*}<=_[=mmkv,'${file2}','getByKeyEq',2]")(engine.eval(s"[=mmkv,'${file2}'][is,[get,'k'][eq,2]]").toString) // TODO
    assertResult(str("marko"))(engine.eval(s"1[=mmkv,'${file2}'][is,[get,'k'][eq,1]][get,'v'][get,'name']"))
    assertResult(str("marko"))(engine.eval(s"{1}[=mmkv,'${file2}'][is,[get,'k'][eq,1]][get,'v'][get,'name']"))
  }

  test("mmkv file-4 parsing") {
    assertResult(s"mmkv{*}<=_[=mmkv,'${file4}']")(engine.eval(s"[=mmkv,'${file4}']").toString)
    //  assertResult("vertex:('name'->'marko','friends'->person{3})")(
    //  engine.eval(s"1[load,'${source4}'][=mmkv,'${file4}'][is.k==1].v[as,vertex]").toString)
    // assertResult("vertex:('name'->'ryan','friends'->person:('name'->'stephen','age'->32,'knows'->4))")(engine.eval(s"1[load,'${source4}'][=mmkv,'${file4}'][is.k==1].v[as,vertex].friends[as,vertex]").toString)
  }

  test("mmkv file-5 parsing") {
    assertResult(s"mmkv{*}<=_[=mmkv,'${file5}']")(engine.eval(s"[=mmkv,'${file5}']").toString)
    assertResult("vertex:('id'->1,'name'->'marko','outE'->edge{2})")(
      engine.eval(s"1[load,'${source5}'][=mmkv,'${file5}'][is.k==1][as,vertex]").toString)
    //    assertResult("vertex{2}")(
    //    engine.eval(s"1[load,'${source5}'][rewrite,(.outE.inV[as,vertex])<=(.out)][=mmkv,'${file5}'][is.k==1][as,vertex].outE[as,edge].inV[as,vertex]").toString)
  }

  /*test("mmkv tp3") {
    println(file6)
    engine.put(":", engine.eval(s"[load,'${kv}'][load,'${tp3}'][load,'${tp3_kv}'][load,'${social_kv}'][define,db<=[=mmkv,'${file6}']]"))
    println(engine.eval(s"'g'[as,kvstore]"))
    println(engine.eval(s"'josh'[as,person].0[as,vertex]"))
    println(engine.eval(s"'g'[as,graph]<g>.V"))
    println(engine.eval(s"'g'[as,graph]<g>.V[as,vertex][is,.id==1]"))
    println(engine.eval(s"'g'[as,graph]<g>.V[as,vertex][is,.id==1].outE[as,edge].inV[as,vertex].properties.name[fold,x.0+x.1]"))
    engine.eval(":")
    engine.put(":model", null)
  }*/

}

