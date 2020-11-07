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
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils
import org.mmadt.language.Tokens
import org.mmadt.language.obj.Obj._
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.trace.ModelOp.{Model, NOMAP, NOREC, NOROOT}
import org.mmadt.language.obj.op.trace.{AsOp, ModelOp, NoOp}
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage
import org.mmadt.storage.StorageFactory.{bool, int, lst, qStar, real, rec, str, strm, zeroObj}
import org.mmadt.storage.obj.graph.ObjGraph.{CTYPE, G, NAME, NONE, OBJ, ObjEdge, ObjTraversalSource, ObjVertex, Q, ROOT, TYPE, VALUE}

import scala.collection.JavaConverters
import scala.collection.convert.ImplicitConversions.`iterator asScala`
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

  @inline implicit class ObjEdge(val edge:Edge) {
    def inst:Inst[Obj, Obj] = edge.value[Inst[Obj, Obj]](OBJ)
  }

  @inline implicit class ObjTraversalSource(val g:GraphTraversalSource) {
    def R:GraphTraversal[Vertex, Vertex] = g.V().has(ROOT, true)
    def C(token:Symbol):GraphTraversal[Vertex, Vertex] = g.C(token.name)
    def C(name:String):GraphTraversal[Vertex, Vertex] = g.R.has(NAME, name).filter((t:Traverser[Vertex]) => !__.isTokenRoot(t.get().obj))
  }
}
class ObjGraph(val model:Model, val graph:Graph = TinkerGraph.open()) {
  val g:GraphTraversalSource = graph.traversal()
  if (model.name.equals(NONE)) {
    List(bool, int, real, str, lst, rec).foreach(c => this.createObj(c))
  } else {
    Option(Option(model.g._2).getOrElse(NOROOT).fetchOrElse(ModelOp.TYPE, NOREC).g._2).getOrElse(NOMAP)
      .filter(x => !x._2.glist.exists(y => y.domainObj.name == Tokens.lift_op)) // little optimization hack that will go away as model becomes more cleverly organized
      .flatMap(x => x._2.glist.filter(y => y.root) :+ x._1) // ctype + token ctype
      .distinct
      .foreach(c => this.createObj(c))
    model.dtypes.foreach(d => this.createObj(d))
  }
  ///////////// CONSTRUCT GRAPH /////////////
  def createObj(aobj:Obj):Vertex = {
    // baobj---[inst]--->aobj
    val target:Vertex = bindObj(aobj)
    if (!aobj.root) {
      g.V(target).inE(aobj.via._2.op).has(OBJ, aobj.via._2).where(___.outV().hasId(aobj.via._1)).tryNext().orElseGet(() => {
        createObj(aobj.via._1).addEdge(aobj.via._2.op, target, OBJ, aobj.via._2)
      })
      target.addEdge(Tokens.noop, g.R.has(NAME, aobj.rangeObj.name).tryNext().orElseGet(() => bindObj(aobj.rangeObj)), OBJ, NoOp())
    } else bindObj(toBaseName(aobj.rangeObj)).addEdge(Tokens.noop, target, OBJ, NoOp())
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
            T.label, if (aobj.root) CTYPE else TYPE,
            T.id, atype,
            NAME, aobj.name,
            OBJ, atype,
            Q, atype.q,
            ROOT, Boolean.box(aobj.root))
      }
    })
  }

  def exists(aobj:Obj):Boolean = g.V(aobj).hasNext
  def paths(source:Obj, target:Obj):Stream[List[Obj]] = Stream.empty

  def coerce(source:Obj, target:Obj):Stream[Obj] = {
    Option(source match {
      case _ if !source.alive || source.model.vars(target.name).isDefined => source
      case _ if !target.alive => zeroObj
      case _ if source == target => source
      case _ if __.isAnonRootAlive(source) => target
      case _ if __.isAnon(target.domainObj) => target.trace.reconstruct[Obj](source, target.name)
      case _ if __.isToken(target) && source.isInstanceOf[Type[_]] && source.reload.model.vars(target.name).isDefined => source.from(__(target.name))
      case _:Strm[Obj] if source.model.og.V().has(NAME, target.name).exists(x => source.q.within(x.obj.domainObj.q)) => source // target.trace.reconstruct(source, target.name)
      case _:Strm[Obj] => strm(coerce(source, target))
      //case _:Lst[_] if target.isInstanceOf[Lst[_]] && target.asInstanceOf[Lst[_]].ctype => source.named(target.name)
      case _:Poly[_] => null
      case _ if target.name.equals(model.coreName) => model
      case _:Value[_] if source.name.equals(target.domainObj.name) => source // target.trace.reconstruct(source, target.name)
      case _:Type[_] if source.name.equals(target.domainObj.name) => target.trace.reconstruct(source, target.name)
      case _ => null
    }).map(x => return Stream(x).asInstanceOf[Stream[target.type]])
    ///////////////////////////////////////////////////////////////
    val sroot:Obj = asType(source.inflate[Obj](model))
    val troot:Obj = bindObj(asType(target.domainObj.inflate[Obj](model))).obj
    Stream(sroot).flatMap(s => {
      JavaConverters.asScalaIterator(
        g.withSack(s).R.has(CTYPE, NAME, s.name)
          ///////// ALL MATCHING PERMUTATIONS OF DOMAIN /////////
          .flatMap((t:Traverser[Vertex]) => JavaConverters.asJavaIterator(
            Converters.objConverter(t.sack[Obj], t.get.obj).map(sack => (t.get, sack)).iterator)
            .asInstanceOf[java.util.Iterator[Vertex]]) // hack on typing (necessary because TP3 doesn't have flatmap on traverser
          .sideEffect((t:Traverser[Vertex]) => {
            val sack = t.get.asInstanceOf[(Vertex, Obj)]._2
            t.asAdmin().sack(sack)
            t.asAdmin().set(t.get.asInstanceOf[(Vertex, Obj)]._1)
          })
          .until((t:Traverser[Vertex]) => t.get.obj.root && finalStructureTest(t.sack[Obj].rangeObj, troot)) // this can also be emit() instead resolution ends after a full span of the obj graph
          .repeat(___
            .simplePath() // no cycles allowed
            .outE()
            .flatMap((t:Traverser[Edge]) => {
              val sack = t.sack[Obj]
              val inst = t.get.inst
              val incidentObj = t.get.inVertex().obj
              if (inst.op.equals(Tokens.noop) && baseName(sack) != baseName(incidentObj))
                JavaConverters.asJavaIterator(Converters.objConverter(sack, incidentObj)
                  .map(newSack => (t.get, newSack)).iterator)
                  .asInstanceOf[java.util.Iterator[Edge]]
              else {
                val incidentName = incidentObj.name
                val instOut = t.get.inst.exec(sack.rangeObj).clone(name = incidentName, q = sack.pureQ).normQ(sack.rangeObj.q)
                IteratorUtils.of((t.get,
                  if (instOut.root) sack.named(incidentName)
                  else instOut.clone(via = (sack, AsOp(instOut))))).asInstanceOf[java.util.Iterator[Edge]]
              }
            }) // evaluate edge instruction
            .sideEffect((t:Traverser[Edge]) => {
              val sack = t.get.asInstanceOf[(Edge, Obj)]._2
              t.asAdmin().sack(sack)
              t.asAdmin().set(t.get.asInstanceOf[(Edge, Obj)]._1)
            })
            .inV()
            .filter((t:Traverser[Vertex]) => t.sack[Obj].alive)
          )
          .sack[Obj]
      ).toStream
    }).map(obj => target.trace.reconstruct[Obj](obj, target.name).normQ(target.q))
      .map(obj => {
        source match {
          case _:Value[_] => Try[Obj](source.update(model).compute(obj, withAs = false).named(target.name)).getOrElse(zeroObj) match {
            case x if !x.isInstanceOf[Poly[_]] => Converters.objConverter(x, obj.rangeObj).headOption.getOrElse(zeroObj)
            case x => x
          }
          case _:Type[_] => obj
        }
      })
      .filter(_.alive)
      .union(Try(Converters.objConverter(source, troot)
        .filter(_ => Tokens.named(target.name))
        .map(x => target.trace.reconstruct[Obj](x))).getOrElse(Stream.empty[Obj])) // direct translation of source to target with reconstruction via target trace
      .filter(x => finalStructureTest(x.rangeObj, target.rangeObj))
      .distinct
  }

  def finalStructureTest(a:Obj, b:Obj):Boolean = {
    if (__.isAnon(b.domainObj)) return true
    if (a.alive != b.alive || a.name != b.name) return false
    val aobj:Obj = a
    val bobj:Obj = b.inflate(model)
    aobj match {
      case alst:Lst[Obj] =>
        bobj match {
          case blst:Lst[Obj] =>
            //Poly.sameSep(alst, blst) &&
            alst.size == blst.size &&
              alst.glist.zip(blst.glist).forall(p => finalStructureTest(p._1, p._2))
          case _ => false
        }
      case arec:Rec[Obj, Obj] =>
        bobj match {
          case brec:Rec[Obj, Obj] =>
            Poly.sameSep(arec, brec) &&
              brec.isEmpty == arec.isEmpty &&
              arec.gmap.forall(p => qStar.equals(p.q) || brec.gmap.exists(q => finalStructureTest(p._1, q._1) && finalStructureTest(p._2, q._2)))
          case _ => false
        }
      case _:Value[Obj] if bobj.isInstanceOf[Value[Obj]] => aobj.equals(bobj)
      case _ => true
    }
  }
}
