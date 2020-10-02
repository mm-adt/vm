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

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.{GraphTraversalSource, __}
import org.apache.tinkerpop.gremlin.structure.{Edge, Graph, Vertex}
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.obj.graph.ObjGraphUtil._

import scala.collection.JavaConverters

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ObjGraph {
  val graph:Graph = TinkerGraph.open
  val traversal:GraphTraversalSource = this.graph.traversal()

  def add(aobj:Obj):Vertex = {
    val vertex = addV(aobj)
    aobj.trace.reverse.foldLeft(vertex)((a, b) => {
      val nextVertex = addV(b._1)
      addE(nextVertex, b._2, a)
      nextVertex
    }).property(ROOT, true)
    vertex
  }

  def roots:Seq[Vertex] = JavaConverters.asScalaIterator(traversal.V().has(ROOT, true)).toSeq

  private def addV(aobj:Obj):Vertex = {
    traversal.V().has(OBJ, aobj).tryNext().orElseGet(() => {
      val vertex = graph.addVertex(if (aobj.isInstanceOf[Type[_]]) TYPE else VALUE)
      vertex.property(OBJ, aobj)
      vertex.property(RANGE, aobj.rangeObj)
      vertex
    })
  }

  private def addE(source:Vertex, inst:Inst[Obj, Obj], target:Vertex):Edge = {
    traversal.V(source).outE(inst.op).has(OBJ, inst).where(__.inV().has(OBJ, target.obj)).tryNext().orElseGet(() => {
      val edge = source.addEdge(inst.op, target)
      edge.property(OBJ, inst)
      edge.property(RANGE, inst)
      edge
    })
  }
}
