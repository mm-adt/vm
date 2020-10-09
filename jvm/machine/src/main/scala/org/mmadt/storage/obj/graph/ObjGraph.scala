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
  val ROOT:String = "root"
  val NONE:String = "none"
  val G:String = "g"
  val Q:String = "q"

  def create(model:Symbol):ObjGraph = create(storage.model(model))
  def create(model:Model):ObjGraph = new ObjGraph(model)

  @inline implicit class ObjVertex(val vertex:Vertex) {
    def obj:Obj = vertex.value[Obj](OBJ)
  }

  @inline implicit class ObjTraversalSource(val g:GraphTraversalSource) {
    def R:GraphTraversal[Vertex, Vertex] = g.V().has(ROOT, true)
  }
}
class ObjGraph(val model:Model, val graph:Graph = TinkerGraph.open()) {
  val g:GraphTraversalSource = graph.traversal()
  // load model into graph
  if (model.name.equals(NONE)) {
    List(bool, int, real, str, lst, rec).foreach(c => this.createType(c))
  } else {
    Option(Option(model.g._2).getOrElse(NOROOT).fetchOrElse(ModelOp.TYPE, NOREC).g._2).getOrElse(NOMAP)
      .filter(x => !x._2.glist.exists(y => y.domainObj.name == Tokens.lift_op)) // little optimization hack that will go away as model becomes more cleverly organized
      .flatMap(x => x._1 +: x._2.glist)
      .distinct
      .foreach(c => this.createType((if (__.isTokenRoot(c)) this.model.findCtype[Obj](c.name).getOrElse(c) else c).asInstanceOf[Type[Obj]]))
    model.dtypes.foreach(d => this.createType(d))
  }

  ///////////////////////////////////////////////////

  def coerce(source:Obj, target:Obj):Stream[target.type] = {
    Option(source match {
      case alst:Lst[_] if Lst.exactTest(alst, target) => source
      case _:Poly[_] => null
      case _ if target.name.equals(model.coreName) => model
      case _ if source.name.equals(target.name) => source
      case _ => null
    }).map(x => return Stream(x).asInstanceOf[Stream[target.type]])
    ////////////////////////////////////////////////////////////////
    paths(if (source.isInstanceOf[Value[_]]) asType(source) else source, target)
      .map(path => path.last.rangeObj <= path.tail.dropRight(1).foldLeft(path.head.update(model))(
        (a, b) => b match {
          case _ if !b.alive || !a.alive => zeroObj
          case inst:Inst[Obj, Obj] => inst.exec(a)
          case _ => Tokens.tryName(b, a)
        }))
      .filter(_.alive)
      .distinct
      .map(x => Try[Obj](source.update(model).compute(x)).filter(y => y.test(x.rangeObj)).getOrElse(zeroObj))
      .filter(_.alive)
      .asInstanceOf[Stream[target.type]]
  }

  def exists(aobj:Obj):Boolean = g.V(aobj).hasNext

  ///////////////////////////////////////////////////

  val noSource:Obj => Traverser[Vertex] => Boolean = (_:Obj) => (t:Traverser[Vertex]) => true
  val noTarget:Obj => Traverser[Vertex] => Boolean = (_:Obj) => (t:Traverser[Vertex]) => !__.isTokenRoot(t.get().obj) && (!t.get().edges(Direction.OUT).hasNext || !t.path().isSimple)
  val aSource:Obj => Traverser[Vertex] => Boolean = (source:Obj) => (t:Traverser[Vertex]) => objMatch(source, t.get().obj).alive
  val aTarget:Obj => Traverser[Vertex] => Boolean = (target:Obj) => (t:Traverser[Vertex]) => !__.isTokenRoot(t.get().obj) && objMatch(t.get().obj, target).alive

  def paths(source:Obj, target:Obj):Stream[List[Obj]] = {
    val xsource = source match {
      case _:__ if __.isAnon(source) => noSource(source)
      case _ => aSource(source)
    }
    val xtarget = target match {
      case _:__ if __.isAnon(target) => noTarget(target)
      case _ => aTarget(target)
    }
    paths(source, target, xsource, xtarget)
  }
  private def paths(source:Obj, target:Obj, sourceFilter:Traverser[Vertex] => Boolean, targetFilter:Traverser[Vertex] => Boolean):Stream[List[Obj]] = {
    val sroot:Obj = source.rangeObj
    JavaConverters.asScalaIterator(
      g.R.filter((t:Traverser[Vertex]) => sourceFilter(t))
        .until((t:Traverser[Vertex]) => targetFilter(t))
        .repeat(___.simplePath().outE().inV())
        .path().by(OBJ))
      .toStream
      .map(x => JavaConverters.asScalaBuffer(x.objects()).toList.asInstanceOf[List[Obj]])
      .filter(x => x.forall(y => y.alive))
      .map(x => x.map(y => y.rangeObj))
      // manipulate head and tail types with computable paths
      .map(x => x.filter(y => y != NoOp())) // direct mappings (e.g. str<=int) have a [noop] as the morphism
      .map(x => if (!__.isAnonRootAlive(sroot) && x.size > 1) x.head +: (objMatch(sroot, x.head) +: x.tail) else x)
      .map(x => if (__.isAnonRootAlive(sroot) || x.head == sroot) x else sroot +: x)
      .map(x => if (x.last.isInstanceOf[Lst[Obj]]) x.dropRight(1) :+ __.combine(toBaseName(x.last)).inst :+ x.last else x)
      .map(x => x.filter(y => !__.isAnonRootAlive(y)))
      .map(x => x.foldLeft(List.empty[Obj])((a, b) => {
        if (a.isEmpty) a :+ b
        else a.lastOption.filter(x => x != b).map(_ => a :+ b).getOrElse(a)
      })).distinct
  }

  ///////////////////////////////////////////////////

  private def objMatch(source:Obj, target:Obj):Obj = {
    source match {
      case _ if __.isAnon(target) => source
      case _:Poly[Obj] => source match {
        case _ if source.named && source.name.equals(target.name) => source
        case alst:Lst[Obj] => target match {
          case blst:LstType[Obj] if blst.ctype => alst.named(blst.name)
          case blst:Lst[Obj] if Lst.exactTest(alst, blst) => alst
          case blst:Lst[Obj] if alst.gsep == blst.gsep && alst.size == blst.size =>
            val combo = alst.glist.zip(blst.glist).map(pair => coerce(pair._1, pair._2))
            if (combo.exists(x => x.isEmpty)) return zeroObj
            // TODO: multiple legal paths leads to non-deterministic morphing (currently choosing smallest trace)
            val combination = alst.clone(_ => combo.map(x => x.minBy(x => x.trace.size).rangeObj)) // hmmmm.
            if (combination.glist.zip(alst.glist).forall(pair => pair._1 == pair._2)) __ else __.combine(combination).inst
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
          case _ => zeroObj
        }
      }
      case _ if source.name.equals(target.name) => source
      case _ => zeroObj
    }
  }


  def createType(atype:Type[Obj]):Unit = {
    val target:Vertex = createObj(atype)
    if (!atype.root) {
      g.V(target).outE(Tokens.noop).has(OBJ, NoOp()).where(___.inV().hasId(atype.range)).tryNext().orElseGet(() => {
        val rangeV = bindObj(atype.range)
        val edge = target.addEdge(Tokens.noop, rangeV, OBJ, NoOp())
        if (__.isTokenRoot(rangeV.obj))
          rangeV.addEdge(Tokens.noop, bindObj(model.resolve(rangeV.obj)), OBJ, NoOp())
        edge
      })
    }
  }

  private def createObj(aobj:Obj):Vertex = {
    val target:Vertex = bindObj(aobj)
    if (!aobj.root) {
      g.V(target).inE(aobj.via._2.op).has(OBJ, aobj.via._2).where(___.outV().hasId(aobj.via._1)).tryNext().orElseGet(() => {
        createObj(aobj.via._1).addEdge(aobj.via._2.op, target, OBJ, aobj.via._2)
      })
    }
    target
  }

  private def bindObj(aobj:Obj):Vertex = {
    g.V(aobj).tryNext().orElseGet(() => {
      aobj match {
        case avalue:Value[_] =>
          graph.addVertex(
            T.label, VALUE,
            T.id, avalue,
            OBJ, avalue,
            G, avalue.g.asInstanceOf[Object],
            Q, avalue.q,
            ROOT, Boolean.box(aobj.root)
          )
        case atype:Type[_] =>
          graph.addVertex(
            T.label, TYPE,
            T.id, atype,
            OBJ, atype,
            Q, atype.q,
            ROOT, Boolean.box(aobj.root)
          )
      }
    })
  }

}
