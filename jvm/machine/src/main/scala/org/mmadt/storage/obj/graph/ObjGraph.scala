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
import org.mmadt.language.obj.`type`.{LstType, RecType, Type, __}
import org.mmadt.language.obj.op.trace.ModelOp.{Model, NOMAP, NOREC, NOROOT}
import org.mmadt.language.obj.op.trace.{ModelOp, NoOp}
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Obj, _}
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
  val CTYPE:String = "ctype"
  val TYPE:String = "type"
  val VALUE:String = "value"
  val ROOT:String = "root"
  val NONE:String = "none"
  val NAME:String = "name"
  val G:String = "g"
  val Q:String = "q"

  def create(model:Symbol):ObjGraph = create(storage.model(model))
  def create(model:Model):ObjGraph = new ObjGraph(model)

  @inline implicit class ObjVertex(val vertex:Vertex) {
    def obj:Obj = vertex.value[Obj](OBJ)
  }

  @inline implicit class ObjTraversalSource(val g:GraphTraversalSource) {
    def R:GraphTraversal[Vertex, Vertex] = g.V().has(ROOT, true)
    def C(token:Symbol):GraphTraversal[Vertex, Vertex] = g.C(token.name)
    def C(name:String):GraphTraversal[Vertex, Vertex] = g.R.has(NAME, name).filter((t:Traverser[Vertex]) => !__.isTokenRoot(t.get().obj))
  }
}
class ObjGraph(val model:Model, val graph:Graph = TinkerGraph.open()) {
  graph.asInstanceOf[TinkerGraph].createIndex(NAME, classOf[Vertex])
  val g:GraphTraversalSource = graph.traversal()
  // load model into graph
  if (model.name.equals(NONE)) {
    List(bool, int, real, str, lst, rec).foreach(c => this.createType(c))
  } else {
    Option(Option(model.g._2).getOrElse(NOROOT).fetchOrElse(ModelOp.TYPE, NOREC).g._2).getOrElse(NOMAP)
      .filter(x => !x._2.glist.exists(y => y.domainObj.name == Tokens.lift_op)) // little optimization hack that will go away as model becomes more cleverly organized
      .flatMap(x => x._2.glist.filter(y => y.root) :+ x._1) // ctype + token ctype
      .distinct
      .foreach(c => this.createType(c.asInstanceOf[Type[Obj]]))
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
      .map(path => {
        path.last.rangeObj <= path.tail.dropRight(1).foldLeft(path.head.update(model))(
          (a, b) => b match {
            case _ if !b.alive || !a.alive => zeroObj
            case inst:Inst[Obj, Obj] => inst.exec(a)
            case _:Type[Obj] => a.q(b.q)
            case _:Value[Obj] => b.q(q => a.q.mult(q))
          })
      })
      .filter(_.alive)
      .distinct
      .flatMap(x => objMatch(source.update(model), Obj.resolveToken(source.update(model), x.domainObj)).map(y => (x, y)))
      .map(x => (x._1, Try[Obj](x._2.compute(x._1).hardQ(source.q.mult(target.q))).getOrElse(zeroObj))) // TODO: get rid of withAs=true
      .flatMap(x => objMatch(x._2.update(model), Obj.resolveToken(x._2.update(model), x._1.rangeObj)).map(y => (x._1, y)))
      .filter(x => x._2.hardQ(x._1.rangeObj.q).test(x._1.rangeObj))
      .map(x => x._2)
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
        .sideEffect((t:Traverser[Vertex]) => t.sack[(Stream[Obj], Stream[Obj])](objMatch(sroot, t.get.obj), objMatch(t.get.obj, troot)))
        .flatMap((t:Traverser[Vertex]) => {
          val sack = t.sack[(Stream[Obj], Stream[Obj])]()
          JavaConverters.asJavaIterator((
            if (sack._2.isEmpty) sack._1.map(x => (t.get(), x, zeroObj))
            else sack._1.flatMap(x => sack._2.map(y => (t.get(), x, y)))).iterator).asInstanceOf[java.util.Iterator[Vertex]]
        })
        .sideEffect((t:Traverser[Vertex]) => {
          val sack = (t.get.asInstanceOf[(Vertex, Obj, Obj)]._2, t.get.asInstanceOf[(Vertex, Obj, Obj)]._3)
          t.asAdmin().sack(sack)
          t.asAdmin().set(t.get.asInstanceOf[(Vertex, Obj, Obj)]._1)
        })
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
          .flatMap((t:Traverser[Vertex]) => {
            val sack = t.sack[(Obj, Obj)]
            val s:Stream[Obj] = objMatch(t.get.obj, troot)
            JavaConverters.asJavaIterator(
              if (s.isEmpty) Iterator((t.get, sack._1, zeroObj))
              else s.map(x => (t.get, sack._1, x)).iterator).asInstanceOf[java.util.Iterator[Vertex]]
          })
          .sideEffect((t:Traverser[Vertex]) => {
            val sack:(Obj, Obj) = (t.get.asInstanceOf[(Vertex, Obj, Obj)]._2, t.get.asInstanceOf[(Vertex, Obj, Obj)]._3)
            t.asAdmin().sack(sack)
            t.asAdmin().set(t.get.asInstanceOf[(Vertex, Obj, Obj)]._1)
          }))
        .path()
        .by((t:Any) => t match {
          case e:Element => e.value[Obj](OBJ) // avoid sack in the path
          case _ => __
        }).map((t:Traverser[Path]) => (t.get().objects().asInstanceOf[java.util.List[Obj]].toList, t.sack[(Obj, Obj)])))
      .toStream
      // manipulate head and tail types with computable paths
      // TODO: reconstruct arguments to all instructions so that a coercion maintains to complete bytecode specification
      .filter(x => x._1.forall(_.alive))
      .map(x => (x._1.filter(y => y != NoOp()), x._2))
      .map(x => (if (!__.isAnonRootAlive(sroot) && x._1.size > 1) x._1.head +: x._2._1 +: x._1.tail else x._1, x._2)) // append the source morph sack
      .map(x => (if (__.isAnonRootAlive(sroot) || x._1.head == sroot) x._1 else (sroot +: x._1), x._2)) // append the source range
      .map(x => if (x._1.last.isInstanceOf[Poly[Obj]]) x._1.dropRight(1) :+ x._2._2 :+ x._1.last else x._1 :+ x._2._2) // append the target morph sack*/
      .map(x => x.filter(y => !__.isAnonRootAlive(y)))
      .map(x => x.foldLeft(List.empty[Obj])((a, b) => {
        if (a.isEmpty) a :+ b
        else a.lastOption.filter(x => x != b).map(_ => a :+ b).getOrElse(a)
      })).distinct
  }

  ///////////////////////////////////////////////////

  private def objMatch(source:Obj, target:Obj):Stream[Obj] = {
    (source match {
      case _ if __.isAnon(target) => Stream(source)
      case _:Poly[Obj] => source match {
        case _ if source.named && source.name.equals(target.name) => Stream(source)
        case alst:Lst[Obj] => target match {
          case blst:LstType[Obj] if blst.ctype => Stream(alst.named(blst.name))
          case blst:Lst[Obj] if Lst.exactTest(alst, blst) => Stream(alst)
          case blst:Lst[Obj] if alst.gsep == blst.gsep && alst.size == blst.size =>
            alst.update(model).glist.zip(blst.glist).flatMap(pair => coerce(pair._1, pair._2).flatten(pair => Iterator(pair)))
              .foldLeft(List.empty[Obj])((a, b) => a :+ b)
              .combinations(alst.size).toList.distinct
              .filter(x => x.size == alst.size)
              .filter(x => x.forall(_.alive))
              .map(x => alst.update(model).clone(_ => x))
              .map(z =>
                if (z.glist.zip(alst.glist).forall(pair => pair._1 == pair._2)) __
                else __.combine(z).inst).toStream
          case _ => Stream(zeroObj)
        }
        case arec:Rec[Obj, Obj] => target match {
          case brec:RecType[Obj, Obj] if brec.ctype => Stream(arec.named(brec.name))
          case brec:Rec[Obj, Obj] =>
            val z = arec.clone(name = brec.name, g = (brec.gsep,
              arec.update(model).gmap.flatMap(a => brec.gmap
                .filter(b => a._1.test(b._1))
                .map(b => (a._1.coerce(b._1), a._2.coerce(b._2)))
                .filter(b => b._1.alive && b._2.alive))))
            if (z.gmap.size < brec.gmap.count(x => x._2.q._1.g > 0)) Stream(zeroObj) else Stream(z)
          case _ => Stream(zeroObj)
        }
      }
      case _ if source.name.equals(target.name) => Stream(source)
      case _ => Stream(zeroObj)
    }).filter(_.alive).map(o => o.hardQ(source.q).named(target.name))
  }


  ///////////// CONSTRUCT GRAPH

  def createType(atype:Type[Obj]):Unit = {
    // ...---[inst]--->atype---[noop]--->token---[noop]--->roottype
    val target:Vertex = createObj(atype)
    if (!atype.root) {
      g.V(target).outE(Tokens.noop).has(OBJ, NoOp()).where(___.inV().hasId(atype.rangeObj)).tryNext().orElseGet(() => {
        val rangeV = bindObj(atype.rangeObj)
        val edge = target.addEdge(Tokens.noop, rangeV, OBJ, NoOp())
        if (__.isTokenRoot(rangeV.obj))
          g.C(rangeV.obj.name).map((t:Traverser[Vertex]) => rangeV.addEdge(Tokens.noop, t.get(), OBJ, NoOp())).iterate()
        edge
      })
    }
  }

  private def createObj(aobj:Obj):Vertex = {
    // baobj---[inst]--->aobj
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
            NAME, aobj.name,
            OBJ, avalue,
            G, avalue.g.asInstanceOf[Object],
            Q, avalue.q,
            ROOT, Boolean.box(aobj.root))
        case atype:Type[_] =>
          graph.addVertex(
            T.label, if (aobj.root && !__.isTokenRoot(aobj)) CTYPE else TYPE,
            T.id, atype,
            NAME, aobj.name,
            OBJ, atype,
            Q, atype.q,
            ROOT, Boolean.box(aobj.root))
      }
    })
  }
}
