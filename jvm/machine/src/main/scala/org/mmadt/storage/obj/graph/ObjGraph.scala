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
import org.apache.tinkerpop.gremlin.structure.{Edge, Graph, T, Vertex}
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.trace.ModelOp
import org.mmadt.language.obj.op.trace.ModelOp.{Model, NOMAP, NOREC, NOROOT}
import org.mmadt.language.obj.{Inst, Lst, Obj, asType}
import org.mmadt.storage
import org.mmadt.storage.StorageFactory.{bool, int, lst, real, rec, str, zeroObj}

import scala.collection.JavaConverters
import scala.util.Try

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object ObjGraph {
  val OBJ:String = "obj"
  val TYPE:String = "type"
  val VALUE:String = "value"
  val RANGE:String = "range"
  val ROOT:String = "root"

  def create(model:Symbol):ObjGraph = create(storage.model(model))
  def create(model:Model):ObjGraph = {
    val tg = TinkerGraph.open()
    // tg.createIndex(OBJ,classOf[Vertex])
    val graph = new ObjGraph(tg)
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
    var model:Model = ModelOp.NONE
    val g:GraphTraversalSource = graph.traversal()
    def doObj(aobj:Obj):Obj = {
      this.addType(aobj)
      aobj
    };
    def doModel(model:Symbol):Unit = this.doModel(storage.model(model))
    def doModel(model:Model):Unit = {
      if (model.name.equals("none")) {
        List(bool, int, real, str, lst, rec).foreach(c => this.addType(c))
      } else {
        Option(Option(model.g._2).getOrElse(NOROOT).fetchOrElse(ModelOp.TYPE, NOREC).g._2).getOrElse(NOMAP)
          .filter(x => !x._2.glist.exists(y => y.domainObj.name == Tokens.lift_op)) // little optimization hack that will go away as model becomes more cleverly organized
          .flatMap(x => x._1 +: x._2.glist)
          .distinct
          .filter(x => x.root)
          .foreach(c => this.addType(c))
        model.dtypes.foreach(d => this.addType(d))
        this.addType(model)
      }
      this.model = model
    }

    ///////////////////////////////////////////////////

    def fpath(source:Obj, target:Obj, filtering:Boolean = true):Seq[Obj] = {
      if (source.name.equals(model.coreName) && target.name.equals(model.coreName)) return List(model)
      path(source, target)
        .map(p => pathToObj(p))
        .filter(o => !filtering || o.name.equals(target.name))
        .distinct
    }

    def exists(aobj:Obj):Boolean = g.R.has(RANGE, aobj).hasNext

    ///////////////////////////////////////////////////

    private def pathToObj(path:List[Obj]):Obj = {
      path.tail.foldLeft(path.head.update(model))((a, b) => b match {
        case _ if !b.alive || !a.alive => return zeroObj
        case inst:Inst[Obj, Obj] => a.compute(__.via(asType(a), inst).asInstanceOf[Obj], withAs = false)
        case _ if __.isToken(b) => Try[Obj](a.compute(Obj.resolveToken(a, b, baseName = false))).getOrElse(zeroObj)
        case _ => Tokens.tryName(b, a)
      })
    }

    private def path(source:Obj, target:Obj):Seq[List[Obj]] = {
      g.R.filter((t:Traverser[Vertex]) => t.get().property(ROOT).value().equals(true) && objMatch(source, t.get().obj).alive)
        .until((t:Traverser[Vertex]) => t.get().property(ROOT).value().equals(true) && objMatch(t.get().obj, target).alive)
        .repeat(___.outE().inV())
        .filter((t:Traverser[Vertex]) => t.get().obj.alive)
        .path().by(OBJ)
        .toSeq
        .map(x => JavaConverters.asScalaBuffer(x.objects()).toList.asInstanceOf[List[Obj]])
        .map(x => List(source, objMatch(source, x.head), x.head) ++ x.tail)
    }

    private def objMatch(source:Obj, target:Obj):Obj = {
      source match {
        case _ if __.isAnon(target) => source
        case _ if source.named && source.name.equals(target.name) => source
        case alst:Lst[Obj] => target match {
          case blst:Lst[Obj] if alst.gsep == blst.gsep && alst.size == blst.size =>
            val combo = alst.g._2.zip(blst.g._2).map(pair => fpath(pair._1, pair._2, filtering = false))
            if (combo.exists(x => x.isEmpty)) return zeroObj
            val combination = alst.clone(_ => combo.map(x => x.head))
            if (combination.g._2.zip(alst.g._2).forall(pair => pair._1 == pair._2)) __ else __.combine(combination).inst
          case _ => zeroObj
        }
        case _ if source.name.equals(target.name) => source
        case _ => zeroObj
      }
    }

    private def addType(aobj:Obj):(Vertex, Vertex) = {
      val target = if (__.isToken(aobj))
        g.R.filter((t:Traverser[Vertex]) => t.get().obj.name.equals(aobj.name)).tryNext().orElse(addObj(aobj, aobj.root))
      else
        addObj(aobj, aobj.root)
      val source = aobj.trace.reverse.foldLeft(target)((a, b) => {
        val nextVertex = if (b._1.equals(aobj.domainObj)) addObj(b._1, root = true) else addObj(b._1)
        addInst(nextVertex, b._2, a)
        nextVertex
      })
      (source, target)
    }

    private def addObj(aobj:Obj, root:Boolean = false):Vertex = {
      g.V().has(OBJ, aobj).has(ROOT, root).tryNext().orElseGet(() =>
        graph.addVertex(
          T.label, if (aobj.isInstanceOf[Type[_]]) TYPE else VALUE,
          OBJ, aobj,
          ROOT, Boolean.box(root),
          RANGE, aobj.rangeObj))
    }

    private def addInst(source:Vertex, inst:Inst[Obj, Obj], target:Vertex):Edge = {
      g.V(source).outE(inst.op).has(OBJ, inst).where(___.inV().has(OBJ, target.obj)).tryNext().orElseGet(() =>
        source.addEdge(
          inst.op, target,
          OBJ, inst,
          RANGE, inst))
    }
  }
}
