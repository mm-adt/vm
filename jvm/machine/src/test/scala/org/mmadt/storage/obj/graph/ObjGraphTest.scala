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
import org.mmadt.language.obj.`type`.__._
import org.mmadt.storage.StorageFactory.{bool, int, lst, real, rec, str}
import org.mmadt.storage.obj.graph.ObjGraph.{ObjGraph, RANGE}
import org.scalatest.FunSuite

import scala.collection.convert.ImplicitConversions.`iterator asScala`

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ObjGraphTest extends FunSuite {

  test("type existence w/ pg_2") {
    val graph:ObjGraph = ObjGraph.create('pg_2)
    assert(graph.exists(bool))
    assert(graph.exists(int))
    assert(graph.exists(real))
    assert(graph.exists(str))
    assert(graph.exists(lst))
    assert(graph.exists(rec))
    assert(graph.exists('poly))
    assert(graph.exists('vertex))
    assert(graph.exists('edge))
  }

  test("type construction w/ pg_2") {
    val graph:ObjGraph = ObjGraph.create('pg_2)
    assertResult(Seq(__('vertex)))(graph.fpath('vertex, 'vertex))
    assertResult(Seq('vertex <= int.-<(rec(str("id") -> __))))(graph.fpath(int, 'vertex))
    assertResult(Seq('edge <= (('vertex `;` 'vertex) <= (int `;` int)
      .combine(('vertex <= int.-<(rec(str("id") -> __))) `;`('vertex <= int.-<(rec(str("id") -> __)))))
      .-<((str("outV") -> ('vertex <= ('vertex `;` 'vertex).get(0))) `_,`(str("inV") -> ('vertex <= ('vertex `;` 'vertex).get(1))))))(graph.fpath(int `;` int, 'edge))
  }

  test("type construction w/ digraph") {
    val graph:ObjGraph = ObjGraph.create('digraph)
    assertResult(Nil)(graph.fpath(str("bad_id") -> int(12), 'vertex))
    assertResult(Nil)(graph.fpath(0, 'vertex))
    assertResult(Seq('vertex(str("id") -> 'nat(1)) `;` 'vertex(str("id") -> 'nat(2))))(graph.fpath('nat(1) `;` 'nat(2), 'vertex `;` 'vertex))
    assertResult(Seq('attr(str("key") -> str("a") `_,` str("value") -> str("b"))))(graph.fpath(str("a") `;` "b", 'attr))
    //assertResult(Seq('attr <= (str `;` str).-<(str("key") -> (str `;` id).get(0) `_,` str("value") -> (str `;` id).get(1))))(graph.fpath(str `;` str, 'attr))
    assertResult(Seq('vertex(str("id") -> 'nat(23))))(graph.fpath('nat(23), 'vertex))
    assertResult(Seq('vertex(str("id") -> 'nat(23))))(graph.fpath(23, 'vertex))
    assertResult(Seq('vertex(str("id") -> 'nat(23) `_,` str("attrs") -> 'attr(str("key") -> str("no") `_,` str("value") -> str("data")))))(graph.fpath(-23, 'vertex))
    assertResult(Seq('attr(str("key") -> str("marko") `_,` str("value") -> int(29))))(graph.fpath(str("key") -> str("marko") `_,` str("value") -> int(29), 'attr))
    assertResult(Seq('vertex(str("id") -> 'nat(55) `_,` str("attrs") -> 'attr(str("key") -> str("marko") `_,` str("value") -> int(29)))))(graph.fpath('nat(55) `;` 'attr(str("key") -> str("marko") `_,` str("value") -> int(29)), 'vertex))
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> 'nat(100)) `_,` str("inV") -> 'vertex(str("id") -> 'nat(200)))))(graph.fpath('vertex(str("id") -> 'nat(100)) `;` 'vertex(str("id") -> 'nat(200)), 'edge))
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> 'nat(100)) `_,` str("inV") -> 'vertex(str("id") -> 'nat(200)))))(graph.fpath('nat(100) `;` 'nat(200), 'edge))
  }

  test("xxx") {
    val graph = new ObjGraph(TinkerGraph.open())
    val g = graph.g
    graph.doModel('play)
    g.V().repeat(___.outE().inV()).until(___.outE().count().is(0L)).path().by(RANGE).foreach(x => println(x))
    println("-----")
    graph.fpath('A, 'C).foreach(x => println(x))
  }
}
