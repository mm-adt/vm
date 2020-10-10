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

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.{GraphTraversal, GraphTraversalSource, __ => ___}
import org.apache.tinkerpop.gremlin.process.traversal.{Path, Traverser}
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
import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`
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
          case _:Type[Obj] => a.q(b.q)
          case _:Value[Obj] => a `=>` b
        }))
      .filter(_.alive)
      .distinct
      .map(x => Try[Obj](source.update(model) `=>` x.q(target.q))
        .filter(y => y.hardQ(x.rangeObj.q).test(x.rangeObj)).getOrElse(zeroObj)) // filter needed because => doesn't use biproduct coercion yet
      .filter(_.alive)
      .asInstanceOf[Stream[target.type]]
  }

  def exists(aobj:Obj):Boolean = g.V(aobj).hasNext

  ///////////////////////////////////////////////////

  def paths(source:Obj, target:Obj):Stream[List[Obj]] = {
    val sroot:Obj = source.rangeObj
    val troot:Obj = target.rangeObj
    JavaConverters.asScalaIterator(
      g.withSack((zeroObj, zeroObj)) // sack(source morph,target morph)
        .R.inject(createObj(target))
        .sideEffect((t:Traverser[Vertex]) => t.sack[(Obj, Obj)](objMatch(sroot, t.get.obj), objMatch(t.get.obj, troot)))
        .filter((t:Traverser[Vertex]) =>
          (source == __) || // all roots
            t.sack[(Obj, Obj)]._1.alive) // sourced paths
        .until((t:Traverser[Vertex]) =>
          (target == __ && (!t.get().edges(Direction.OUT).hasNext || !t.path().isSimple)) || // all reachable objs
            (target != __ && !__.isTokenRoot(t.get().obj) && t.sack[(Obj, Obj)]._2.alive)) // targeted paths
        .repeat(___ // this is where various cost/sort algorithms will prune expensive paths
          .simplePath()
          .outE()
          .inV()
          .sideEffect((t:Traverser[Vertex]) => t.sack(t.sack[(Obj, Obj)]._1, objMatch(t.get.obj, troot))))
        .path().by(OBJ)
        .map((t:Traverser[Path]) => (t.get().objects().asInstanceOf[java.util.List[Obj]]
          //.map(y => y.rangeObj)
          .filter(y => y != NoOp() && y.alive)
          .toList, t.sack[(Obj, Obj)])))
      .toStream
      // manipulate head and tail types with computable paths
      // TODO: reconstruct arguments to all instructions so that a coercion maintains to complete bytecode specification
      .map(x => ((if (!__.isAnonRootAlive(sroot) && x._1.size > 1) x._1.head +: x._2._1 +: x._1.tail else x._1), x._2))
      .map(x => ((if (__.isAnonRootAlive(sroot) || x._1.head == sroot) x._1 else (sroot +: x._1)), x._2))
      .map(x => if (x._1.last.isInstanceOf[Lst[Obj]]) x._1.dropRight(1) :+ x._2._2 :+ x._1.last else x._1)
      .map(x => x.filter(y => !__.isAnonRootAlive(y)))
      .map(x => x.foldLeft(List.empty[Obj])((a, b) => {
        if (a.isEmpty) a :+ b
        else a.lastOption.filter(x => x != b).map(_ => a :+ b).getOrElse(a)
      })).distinct
  }

  ///////////////////////////////////////////////////

  private def objMatch(source:Obj, target:Obj):Obj = {
    Option(source match {
      case _ if __.isAnon(target) => source
      case _:Poly[Obj] => source match {
        case _ if source.named && source.name.equals(target.name) => source
        case alst:Lst[Obj] => target match {
          case blst:LstType[Obj] if blst.ctype => alst.named(blst.name)
          case blst:Lst[Obj] if Lst.exactTest(alst, blst) => alst
          case blst:Lst[Obj] if alst.gsep == blst.gsep && alst.size == blst.size =>
            val combo = alst.glist.zip(blst.glist).map(pair => coerce(pair._1, pair._2))
            if (combo.exists(x => x.isEmpty)) return zeroObj
            val combination = alst.clone(_ => combo.map(x => x.minBy(x => x.trace.size))) // hmmmm.
            if (combination.glist.zip(alst.glist).forall(pair => pair._1 == pair._2)) __ else alst.clone(_ => combo.map(x => x.minBy(x => x.trace.size)))
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
    }).filter(_.alive).map(o => o.q(target.q)).getOrElse(zeroObj)
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
