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

  val _vertex: RecType[Str, Obj] = trec("vertex")()
  val _edge: RecType[Str, Obj] = trec("edge")() //
  val edge: RecType[Str, Obj] = trec("edge")(str("inV") -> _vertex, str("outV") -> _vertex, str("label") -> str) //
  val vertex: RecType[Str, Obj] = trec("vertex")(str("id") -> int ~ "i", str("outE") -> _edge.q(*), str("inE") -> _edge.q(*)) //
  val graph: RecType[Str, Obj] = trec("graph")() //

  val model: Model = Model.simple().
    put(_vertex, trec("vertex")(str("id") -> int ~ "i", str("outE") -> _edge.q(*), str("inE") -> _edge.q(*))).
    put(_vertex.put(str("id"),int), _vertex).
    put(_edge, trec("edge")(str("inV") -> _vertex, str("outV") -> _vertex, str("label") -> str)).
    put(_edge.get(str("label"),str),str("friend").start()).
    put(graph, _vertex.q(*)).
    put(graph.is(graph.get(str("id"),int).gt(int(0))),graph.model("db"))

  test("variable rewrites") {
    println(model)
    val processor = new CompilingProcessor()
    println(graph)
    println(vertex)
    println(edge)
    println(vertex.is(vertex.get(str("id"), int).gt(int(0))).get(str("outE")))
  }
}