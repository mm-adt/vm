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
import org.apache.tinkerpop.gremlin.structure._
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{LstType, RecType, Type, __}
import org.mmadt.language.obj.op.trace.ModelOp.{Model, NOMAP, NOREC, NOROOT}
import org.mmadt.language.obj.op.trace.{ModelOp, NoOp}
import org.mmadt.language.obj.value.Value
import org.mmadt.storage
import org.mmadt.storage.StorageFactory.{bool, int, lst, real, rec, str, zeroObj}
import org.mmadt.storage.obj.graph.ObjGraph._

import scala.collection.JavaConverters
import scala.util.Try

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object ObjGraph {
  val OBJ:String = "obj"
  val TYPE:String = "type"
  val VALUE:String = "value"
  val ISO:String = "iso"
  val ROOT:String = "root"
  val NONE:String = "none"

  def create(model:Symbol):ObjGraph = create(storage.model(model))
  def create(model:Model):ObjGraph = new ObjGraph(model)

  @inline implicit class ObjVertex(val vertex:Vertex) {
    def obj:Obj = vertex.property[Obj](OBJ).value()
    def iso:Obj = vertex.property[Obj](ISO).value()
  }

  @inline implicit class ObjTraversalSource(val g:GraphTraversalSource) {
    def R:GraphTraversal[Vertex, Vertex] = g.V().has(ROOT, true)
  }
}
class ObjGraph(val model:Model, val graph:Graph = TinkerGraph.open()) {
  // tg.createIndex(OBJ,classOf[Vertex])
  val g:GraphTraversalSource = graph.traversal()
  // load model into graph
  if (model.name.equals(NONE)) {
    List(bool, int, real, str, lst, rec).foreach(c => this.addType(c))
  } else {
    Option(Option(model.g._2).getOrElse(NOROOT).fetchOrElse(ModelOp.TYPE, NOREC).g._2).getOrElse(NOMAP)
      .filter(x => !x._2.glist.exists(y => y.domainObj.name == Tokens.lift_op)) // little optimization hack that will go away as model becomes more cleverly organized
      .flatMap(x => x._1 +: x._2.glist)
      .distinct
      .foreach(c => this.addType(if (__.isTokenRoot(c)) this.model.findCtype[Obj](c.name).getOrElse(c) else c))
    model.dtypes.foreach(d => this.addType(d))
    this.addType(model)
  }

  ///////////////////////////////////////////////////

  def coerce(source:Obj, target:Obj):Stream[target.type] = {
    if (source.name.equals(model.coreName) && target.name.equals(model.coreName)) return Stream(model.asInstanceOf[target.type])
    paths(if (source.isInstanceOf[Value[_]]) asType(source) else source, target)
      .map(path => path.last <= path.tail.dropRight(1).foldLeft(path.head.update(model))(
        (a, b) => b match {
          case _ if !b.alive || !a.alive => zeroObj
          case inst:Inst[Obj, Obj] => inst.exec(a)
          case _ => Tokens.tryName(b, a)
        }))
      .filter(_.alive)
      .distinct
      .map(x => Try[Obj](source.rangeObj.update(model).compute(x)).getOrElse(zeroObj))
      .filter(_.alive).asInstanceOf[Stream[target.type]]
  }

  def exists(aobj:Obj):Boolean = g.R.has(ISO, aobj).hasNext

  ///////////////////////////////////////////////////

  val noSource:Obj => Traverser[Vertex] => Boolean = (_:Obj) => (t:Traverser[Vertex]) => true
  val noTarget:Obj => Traverser[Vertex] => Boolean = (_:Obj) => (t:Traverser[Vertex]) => !t.get().edges(Direction.OUT).hasNext || !t.path().isSimple
  val aSource:Obj => Traverser[Vertex] => Boolean = (source:Obj) => (t:Traverser[Vertex]) => objMatch(source, t.get().obj).alive
  val aTarget:Obj => Traverser[Vertex] => Boolean = (target:Obj) => (t:Traverser[Vertex]) => objMatch(t.get().obj, target).alive

  def paths(source:Obj, target:Obj, form:String = ISO):Stream[List[Obj]] = {
    val xsource = source match {
      case _:__ if __.isAnon(source) => noSource(source)
      case _ => aSource(source)
    }
    val xtarget = target match {
      case _:__ if __.isAnon(target) => noTarget(target)
      case _ => aTarget(target)
    }
    paths(source, target, xsource, xtarget, form)
  }
  private def paths(source:Obj, target:Obj, sourceFilter:Traverser[Vertex] => Boolean, targetFilter:Traverser[Vertex] => Boolean, form:String):Stream[List[Obj]] =
    JavaConverters.asScalaIterator(
      g.R.filter((t:Traverser[Vertex]) => sourceFilter(t))
        .until((t:Traverser[Vertex]) => targetFilter(t))
        .repeat(___.simplePath().outE().inV())
        .path().by(form))
      .toStream
      .map(x => JavaConverters.asScalaBuffer(x.objects()).toList.asInstanceOf[List[Obj]])
      // manipulate head and tail types with computable paths
      .map(x => x.filter(y => y != NoOp())) // direct mappings (e.g. str<=int) have a [noop] as the morphism
      .map(x => if (!__.isAnonRootAlive(source) && x.size > 1) x.head +: (objMatch(source, x.head) +: x.tail) else x)
      .map(x => if (__.isAnonRootAlive(source) || x.head == source) x else source +: x)
      .map(x => if (x.last.isInstanceOf[Lst[Obj]]) x.dropRight(1) :+ __.combine(toBaseName(x.last)).inst :+ x.last else x)

  ///////////////////////////////////////////////////

  private def objMatch(source:Obj, target:Obj):Obj = {
    source match {
      case _ if __.isAnon(target) => source
      case _ if source.named && source.name.equals(target.name) => source
      case alst:Lst[Obj] => target match {
        case blst:LstType[Obj] if blst.ctype => alst.named(blst.name)
        case blst:Lst[Obj] if alst.gsep == blst.gsep && alst.size == blst.size =>
          val combo = alst.glist.zip(blst.glist).map(pair => coerce(pair._1, pair._2))
          if (combo.exists(x => x.isEmpty)) return zeroObj
          // TODO: multiple legal paths leads to non-deterministic morphing (currently choosing smallest trace)
          val combination = alst.clone(_ => combo.map(x => x.minBy(x => x.trace.size).rangeObj)) // hmmmm.
          if (combination.glist.zip(alst.glist).forall(pair => pair._1 == pair._2)) __ else __.combine(combination).inst
        case _ if source.name.equals(target.name) => source
        case _ => zeroObj
      }
      case arec:Rec[Obj, Obj] => target match {
        case brec:RecType[Obj, Obj] if brec.ctype => arec.named(brec.name)
        case brec:Rec[Obj, Obj] =>
          val z = arec.clone(name = brec.name, g = (brec.gsep,
            arec.gmap.flatMap(a => brec.gmap
              .filter(b => a._1.test(b._1))
              .map(b => (coerce(a._1, b._1).headOption.getOrElse(zeroObj), coerce(a._2, b._2).headOption.getOrElse(zeroObj)))
              .filter(b => b._1.alive && b._2.alive))))
          if (z.gmap.size < brec.gmap.count(x => x._2.q._1.g > 0)) zeroObj else z
        case _ if source.name.equals(target.name) => source
        case _ => zeroObj
      }
      case _ if source.name.equals(target.name) => source
      case _ => zeroObj
    }
  }

  private def addType(aobj:Obj):(Vertex, Vertex) = {
    val target = addObj(aobj, root = true)
    val source = aobj.trace.reverse.foldLeft(target)((a, b) => {
      val nextVertex = if (b._1.equals(aobj.domainObj)) addObj(b._1, root = true) else addObj(b._1)
      addInst(nextVertex, b._2, a)
      nextVertex
    })
    (source, target)
  }

  private def addObj(aobj:Obj, root:Boolean = false):Vertex = {
    (if (root) g.R.has(ISO, aobj.rangeObj) else g.V().has(OBJ, aobj)).tryNext().orElseGet(() =>
      graph.addVertex(
        T.label, if (aobj.isInstanceOf[Type[_]]) TYPE else VALUE,
        OBJ, aobj,
        ROOT, Boolean.box(root),
        ISO, aobj.rangeObj))
  }

  private def addInst(source:Vertex, inst:Inst[Obj, Obj], target:Vertex):Edge = {
    g.V(source).outE(inst.op).has(OBJ, inst).where(___.inV().is(target)).tryNext().orElseGet(() =>
      source.addEdge(
        inst.op, target,
        OBJ, inst,
        ISO, inst))

  }
}
