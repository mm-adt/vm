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

import org.mmadt.processor.Processor
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class mmkvInstTest extends FunSuite {
  val file:String = getClass.getResource("/mmkv/mmkv.txt").getPath

  test("[=mmkv] with mmkv.txt"){
    assertResult(s"mmkv:['k'->int,'v'->str]{*}<=obj[=mmkv,'${file}']")(obj.mmkv(str(file)).toString)
    assertResult("['k'->1,'v'->'marko'],['k'->2,'v'->'ryan'],['k'->3,'v'->'stephen'],['k'->4,'v'->'kuppitz']")(int(1).mmkv(str(file)).toString)
    //assertResult("List(['k'->1,'v'->'marko'],['k'->2,'v'->'ryan'],['k'->3,'v'->'stephen'],['k'->4,'v'->'kuppitz'])")((int(1) ===> int.mkvInst("/Users/marko/mmkv.txt")).toList.toString())
    assertResult(List(int(1),int(2),int(3),int(4)))(Processor.iterator()(int(4),Processor.compiler().apply(int.mmkv(str(file)).get("k",int))).map(_.obj()).toList)
  }
}
