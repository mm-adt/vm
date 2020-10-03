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
import org.mmadt.language.obj.Obj.intToInt
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__._
import org.mmadt.storage.StorageFactory.int
import org.mmadt.storage.obj.graph.ObjGraph.{OBJ, ObjGraph, ObjTraversalSource, RANGE, ROOT}
import org.scalatest.FunSuite

import scala.collection.convert.ImplicitConversions.`iterator asScala`

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ObjGraphTest extends FunSuite {

  test("obj graph") {
    val graph:ObjGraph = new ObjGraph(TinkerGraph.open())
    val g = graph.g

    graph.doObj(10 ==> int.mult(5).plus(1).gt(2))
    graph.doObj(int.mult(2).plus(10).is(gt(4)))
    graph.doObj(int.mult(12).plus(10).is(gt(4)))
    graph.doObj(int.mult(12).plus(10).mult(100))
    println("Number of roots: " + g.V().has(ROOT, true).count().next())
    println("Number of vertices:" + g.V().count().next())
    println("-----")
    g.R.repeat(___.outE().inV()).until(___.outE().count().is(0L)).path().by("range").foreach(x => println(x))
  }

  test("more") {
    val graph:ObjGraph = new ObjGraph(TinkerGraph.open())
    val g = graph.g
    graph.doObj(int.mult(15).plus(88).branch(int.plus(3) `,` int.mult(55)).gt(500))
    g.R.foreach(x => println(x))
    g.R.valueMap[Object]().foreach(x => println(x))
    println("-----")
    g.R.repeat(___.outE().inV()).until(___.outE().count().is(0L)).path().by("range").foreach(x => println(x))
  }

  test("more2") {
    val graph = new ObjGraph(TinkerGraph.open())
    val g = graph.g
    graph.doRewrite(int.plus(10).plus(-10), int.plus(0))
    println("-----")
    g.R.repeat(___.outE().inV()).until(___.outE().count().is(0L)).path().by(OBJ).foreach(x => println(x))
  }

  test("yyy") {
    val graph = new ObjGraph(TinkerGraph.open())
    val g = graph.g
    graph.doModel('pg_2)
    g.R.repeat(___.outE().inV()).until(___.outE().count().is(0L)).path().by(RANGE).foreach(x => println(x))
    println("-----")
    graph.path(__, 'edge).foreach(x => println(x))
  }

  test("xxx") {
    val graph = new ObjGraph(TinkerGraph.open())
    val g = graph.g
    graph.doModel('play)
    g.R.repeat(___.outE().inV()).until(___.outE().count().is(0L)).path().by(RANGE).foreach(x => println(x))
    println("-----")
    graph.path('A, 'C).foreach(x => println(x))
  }
}
