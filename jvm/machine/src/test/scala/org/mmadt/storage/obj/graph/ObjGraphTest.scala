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

package org.mmadt.storage.obj.graph

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.{__ => ___}
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.mmadt.language.obj.Obj.{intToInt, symbolToToken, tupleToRecYES}
import org.mmadt.language.obj.`type`.__
import org.mmadt.storage.StorageFactory.{bool, int, real, rec, str}
import org.mmadt.storage.obj.graph.ObjGraph.{ObjGraph, ObjTraversalSource, RANGE}
import org.scalatest.FunSuite

import scala.collection.convert.ImplicitConversions.`iterator asScala`

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ObjGraphTest extends FunSuite {

  test("type existence") {
    val graph:ObjGraph = ObjGraph.create('pg_2)
    assert(graph.exists(bool))
    assert(graph.exists(int))
    assert(graph.exists(real))
    assert(graph.exists(str))
    assert(graph.exists('vertex))
    assert(graph.exists('edge))
  }

  test("type construction") {
    val graph:ObjGraph = ObjGraph.create('pg_2)
    //
    assertResult(1)(graph.types('vertex, 'vertex).length)
    assertResult(__('vertex))(graph.types('vertex, 'vertex).head)
    //
    assertResult(1)(graph.types(int, 'vertex).length)
    assertResult('vertex <= int.-<(rec(str("id") -> __)))(graph.types(int, 'vertex).head)
    //
    assertResult('edge <= (('vertex `;` 'vertex) <= (int `;` int)
      .combine(('vertex <= int.-<(rec(str("id") -> __))) `;`('vertex <= int.-<(rec(str("id") -> __)))))
      .-<((str("outV") -> ('vertex <= ('vertex `;` 'vertex).get(0))) `_,`(str("inV") -> ('vertex <= ('vertex `;` 'vertex).get(1)))))(graph.types(int `;` int, 'edge).head)
  }

  test("xxx") {
    val graph = new ObjGraph(TinkerGraph.open())
    val g = graph.g
    graph.doModel('play)
    g.R.repeat(___.outE().inV()).until(___.outE().count().is(0L)).path().by(RANGE).foreach(x => println(x))
    println("-----")
    graph.types('A, 'C).foreach(x => println(x))
  }
}
