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
import org.mmadt.language.obj.`type`.RecType
import org.mmadt.language.obj.{Obj, Str}
import org.mmadt.processor.obj.`type`.CompilingProcessor
import org.mmadt.storage.obj.{*, int, str, trec}
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class GraphModelTest extends FunSuite {

  val vertex: RecType[Str, Obj] = trec("vertex") //
  val edge: RecType[Str, Obj] = trec("edge") //
  val graph: RecType[Str, Obj] = trec("graph") //

  val model: Model = Model.simple().
    put(edge, edge(str("inV") -> vertex, str("outV") -> vertex, str("label") -> str)).
    put(edge.get(str("label"), str), str("friend").start()).
    put(vertex, vertex(str("id") -> int ~ "i", str("outE") -> edge.q(*), str("inE") -> edge.q(*))).
    put(vertex.put(str("id"), int), vertex).
    put(graph, vertex.q(*)).
    put(graph.is(graph.get(str("id"), int).gt(int(0))), graph.model("db"))

  test("variable rewrites") {
    println(model)
    val processor = new CompilingProcessor[Obj, Obj](model)
    println(graph)
    println(model[RecType[Str, Obj]](vertex).get("outE"))
    println(edge)
    println(model[RecType[Str, Obj]](vertex).is(vertex.get(str("id"), int).gt(int(0))).get(str("outE")))
  }
}