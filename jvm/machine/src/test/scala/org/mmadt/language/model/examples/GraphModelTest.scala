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

import org.mmadt.language.obj.value.{IntValue, RecValue}
import org.mmadt.language.obj.{Obj, Str}
import org.mmadt.processor.obj.`type`.CompilingProcessor
import org.mmadt.storage.obj.{int, rec, str}
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class GraphModelTest extends FunSuite {

  val * : (IntValue, IntValue) = (int(0), int(Long.MaxValue))
  val edge: RecValue[Str, Obj] = rec("edge")(str("inV") -> vertex, str("outV") -> vertex, str("label") -> str) //
  val vertex: RecValue[Str, Obj] = rec("vertex")(str("id") -> int, str("outE") -> edge.q(*), str("inE") -> edge.q(*)) //
  val graph: RecValue[Str, Obj] = vertex.q(*) //


  test("variable rewrites") {
    val processor = new CompilingProcessor()
    println(graph)
    println(vertex)
    println(edge)
    //println(vertex.is(vertex.get(str("id"),int).gt(int(0))).get(str("outE")))
  }
}