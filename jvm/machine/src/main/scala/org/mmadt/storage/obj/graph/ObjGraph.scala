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

import org.apache.tinkerpop.gremlin.process.traversal.Traverser
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.{GraphTraversal, GraphTraversalSource, __ => ___}
import org.apache.tinkerpop.gremlin.structure.{Edge, Graph, Vertex}
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.language.obj.op.trace.NoOp
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage

import scala.collection.JavaConverters

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object ObjGraph {

  val OBJ:String = "obj"
  val TYPE:String = "type"
  val VALUE:String = "value"
  val ROOT:String = "root"
  val RANGE:String = "range"

  @inline implicit class ObjVertex(val vertex:Vertex) {
    def obj:Obj = vertex.property[Obj](OBJ).value()
    def iso:Obj = vertex.property[Obj](RANGE).value()
  }

  @inline implicit class ObjTraversalSource(val g:GraphTraversalSource) {
    def R:GraphTraversal[Vertex, Vertex] = g.V().has(ROOT, true)
  }

  @inline implicit class ObjTraversal[A <: Object, B <: Object](val g:GraphTraversal[A, B]) {
    def toSeq:Seq[B] = JavaConverters.asScalaIterator(g).toSeq
  }

  @inline implicit class ObjGraph(val graph:Graph) {
    val g:GraphTraversalSource = graph.traversal()
    def doObj(aobj:Obj):Obj = {
      this.addType(aobj)
      aobj
    };
    def doRewrite(aobj:Obj, bobj:Obj):Unit = {
      this.addObj(aobj).addEdge("==", this.addObj(bobj)).property(OBJ, NoOp())
    }
    def doModel(model:Symbol):Unit = this.doModel(storage.model(model))
    def doModel(model:Model):Unit = {
      model.dtypes.foreach(d => {
        val st = this.addType(d)
        st._1.property(OBJ, st._1.property[Obj](OBJ).value().domainObj)
        st._2.property(OBJ, st._2.property[Obj](OBJ).value().rangeObj)
      })
    }

    ///////////////////////////////////////////////////

    def compute(source:Obj, target:Obj):Unit = {
      this.doObj(source)
      this.doObj(target)
      source ==> target
    }

    def path(source:Obj, range:Obj):Seq[List[Obj]] = {
      g.R.filter((t:Traverser[Vertex]) => __.isAnon(source) || source.name.equals(t.get().obj.name))
        .repeat(___.outE().inV())
        .until((t:Traverser[Vertex]) => __.isAnon(range) || range.name.equals(t.get().obj.name))
        .path().by(RANGE)
        .toSeq
        .map(x => JavaConverters.asScalaBuffer(x.objects()).toList.asInstanceOf[List[Obj]])
    }

    ///////////////////////////////////////////////////

    private def addType(aobj:Obj):(Vertex, Vertex) = {
      val vertex = addObj(aobj)
      (aobj.trace.reverse.foldLeft(vertex)((a, b) => {
        val nextVertex = addObj(b._1)
        addInst(nextVertex, b._2, a)
        nextVertex
      }), vertex)
    }
    private def addObj(aobj:Obj):Vertex = {
      g.V().has(OBJ, aobj).tryNext().orElseGet(() => {
        val vertex = graph.addVertex(if (aobj.isInstanceOf[Type[_]]) TYPE else VALUE)
        vertex.property(OBJ, aobj)
        vertex.property(RANGE, aobj.rangeObj)
        vertex.property(ROOT, true)
        vertex
      })
    }
    private def addInst(source:Vertex, inst:Inst[Obj, Obj], target:Vertex):Edge = {
      g.V(source).outE(inst.op).has(OBJ, inst).where(___.inV().has(OBJ, target.obj.asInstanceOf[Obj])).tryNext().map[Edge](x => {
        target.property(ROOT, false)
        x
      }).orElseGet(() => {
        val edge = source.addEdge(inst.op, target)
        target.property(ROOT, false)
        edge.property(OBJ, inst)
        edge.property(RANGE, inst)
        edge
      })
    }
  }
}
