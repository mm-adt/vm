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
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.trace.ModelOp
import org.mmadt.language.obj.op.trace.ModelOp.{Model, NOMAP, NOREC, NOROOT}
import org.mmadt.language.obj.{Inst, Lst, Obj}
import org.mmadt.storage
import org.mmadt.storage.StorageFactory.zeroObj

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

  def create(model:Symbol):ObjGraph = create(storage.model(model))
  def create(model:Model):ObjGraph = {
    val graph = new ObjGraph(TinkerGraph.open())
    graph.doModel(model)
    graph
  }

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
    def doModel(model:Symbol):Unit = this.doModel(storage.model(model))
    def doModel(model:Model):Unit = {
      model.dtypes.foreach(d => {
        val st = this.addType(d)
        st._1.property(OBJ, st._1.property[Obj](OBJ).value().domainObj)
        st._2.property(OBJ, st._2.property[Obj](OBJ).value().rangeObj)
      })
      // TODO: fix in model
      Option(Option(model.g._2).getOrElse(NOROOT).fetchOrElse(ModelOp.TYPE, NOREC).g._2).getOrElse(NOMAP)
        .filter(x => !x._2.glist.exists(y => y.domainObj.name == Tokens.lift_op)) // little optimization hack that will go away as model becomes more cleverly organized
        .map(x => x._1.asInstanceOf[Type[Obj]]).foreach(c => {
        this.addType(c)._1.property(ROOT, true)
      })
    }

    ///////////////////////////////////////////////////

    def compute(source:Obj, target:Obj):Unit = {
      this.doObj(source)
      this.doObj(target)
      source ==> target
    }

    def path(source:Obj, target:Obj):Seq[List[Obj]] = {
      g.V().filter((t:Traverser[Vertex]) => objMatch(source, t.get().obj).alive)
        .until((t:Traverser[Vertex]) => objMatch(target, t.get().obj).alive)
        .repeat(___.outE().inV())
        .path().by(RANGE)
        .toSeq
        .map(x => JavaConverters.asScalaBuffer(x.objects()).toList.asInstanceOf[List[Obj]])
        .map(x => List(source, objMatch(source, x.head), x.head) ++ x.tail)
    }

    def types(source:Obj, target:Obj):Seq[Obj] = path(source, target).map(p => pathToObj(p)).distinct

    def exists(aobj:Obj):Boolean = g.V().has(OBJ, aobj).hasNext

    ///////////////////////////////////////////////////

    def pathToObj(path:List[Obj]):Obj = {
      path.tail.foldLeft(path.head)((a, b) => b match {
        case inst:Inst[Obj, Obj] => inst.exec(a)
        case aobj:Obj => aobj <= a
      })
    }

    private def objMatch(aobj:Obj, bobj:Obj):Obj = {
      aobj match {
        case _ if __.isAnon(aobj) => bobj
        case _ if aobj.named && aobj.name.equals(bobj.name) => bobj
        case alst:Lst[Obj] => bobj match {
          case blst:Lst[Obj] if alst.gsep == blst.gsep && alst.size == blst.size => __.combine(alst.clone(_ => alst.g._2.zip(blst.g._2).map(pair => types(pair._1, pair._2).head))).inst
          case _ => zeroObj
        }
        case _ if aobj.name.equals(bobj.name) => bobj
        case _ => zeroObj
      }
    }

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
      g.V(source).outE(inst.op).has(OBJ, inst).where(___.inV().has(OBJ, target.obj)).tryNext().map[Edge](x => {
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
