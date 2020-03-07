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

package org.mmadt.storage.mmkv

import org.mmadt.language.jsr223.mmADTScriptEngine
import org.mmadt.language.mmlang.mmlangScriptEngineFactory
import org.mmadt.processor.Processor
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

import scala.collection.JavaConverters.asScalaIterator

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmkvInstTest extends FunSuite {

  val engine:mmADTScriptEngine = new mmlangScriptEngineFactory().getScriptEngine
  val file1 :String            = getClass.getResource("/mmkv/mmkv-1.txt").getPath
  val file2 :String            = getClass.getResource("/mmkv/mmkv-2.txt").getPath

  test("mmkv parsing"){
    println(engine.eval(s"3[=mmkv,'${file1}']").next())
  }

  test("mmkv choose parsing"){
    assertResult(List(int(1),int(1),int(1),int(0)))(asScalaIterator(engine.eval(s"1[=mmkv,'${file1}'][[get,'k'][is>3]->0 | rec -> 1]")).toList)
    assertResult(List(int(1),int(2),int(3),int(4)))(asScalaIterator(engine.eval(s"1[1->[=mmkv,'${file1}'][get,'k'] | int -> 100]")).toList)
    assertResult(List(int(2),int(3),int(4),int(5)))(asScalaIterator(engine.eval(s"1[=mmkv,'${file1}'][get,'k'][plus,1]")).toList)
  }

  test("mmkv file-2 parsing"){
    assertResult(s"mmkv:['k'->int,'v'->rec['name'->str,'age'->int]]{*}<=obj[=mmkv,'${file2}']")(engine.eval(s"obj[=mmkv,'${file2}']").next.toString)
    assertResult(List(str("marko!"),str("stephen!")))(asScalaIterator(engine.eval(s"1[=mmkv,'${file2}'].v[is.age>28].name+'!'")).toList)
  }

  test("[=mmkv] with mmkv-1.txt"){
    assertResult(s"mmkv:['k'->int,'v'->str]{*}<=obj[=mmkv,'${file1}']")(obj.mmkv(str(file1)).toString)
    assertResult("['k'->1,'v'->'marko'],['k'->2,'v'->'ryan'],['k'->3,'v'->'stephen'],['k'->4,'v'->'kuppitz']")(int(1).mmkv(str(file1)).toString)
    //assertResult("List(['k'->1,'v'->'marko'],['k'->2,'v'->'ryan'],['k'->3,'v'->'stephen'],['k'->4,'v'->'kuppitz'])")((int(1) ===> int.mkvInst("/Users/marko/mmkv-1.txt")).toList.toString())
    assertResult(List(int(1),int(2),int(3),int(4)))(Processor.iterator()(int(4),Processor.compiler().apply(int.mmkv(str(file1)).get("k",int))).map(_.obj()).toList)
  }
}
