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
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.{GraphTraversalSource, __ => ___}
import org.apache.tinkerpop.gremlin.structure._
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.mmadt.language.Tokens
import org.mmadt.language.obj.Obj.{symbolToToken, _}
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.`type`.{RecType, Type, __}
import org.mmadt.language.obj.op.trace.ModelOp.{Model, NOMAP, NOREC, NOROOT}
import org.mmadt.language.obj.op.trace.{ModelOp, NoOp}
import org.mmadt.language.obj.value.Value
import org.mmadt.storage
import org.mmadt.storage.StorageFactory.{bool, int, lst, qStar, real, rec, str, zeroObj}
import org.mmadt.storage.obj.graph.ObjGraph.{CTYPE, G, NAME, NONE, OBJ, ObjEdge, ObjTraversalSource, ObjVertex, Q, ROOT, TYPE, VALUE}

import scala.collection.JavaConverters
import scala.util.Try

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object ObjGraph2 {
  def create(model:Symbol):ObjGraph2 = create(storage.model(model))
  def create(model:Model):ObjGraph2 = new ObjGraph2(model)

  def main(args:Array[String]):Unit = {
    val graph2 = ObjGraph2.create('digraph)
    graph2.g.E().forEachRemaining(x => println(x))
    println("\n ####### \n")
    println(graph2.coerce('nat(1) `;` 'nat(int(2)), 'vertex `;` 'vertex))
  }
}
class ObjGraph2(val model:Model, val graph:Graph = TinkerGraph.open()) {
  val g:GraphTraversalSource = graph.traversal()

  if (model.name.equals(NONE)) {
    List(bool, int, real, str, lst, rec).foreach(c => this.createObj(c))
  } else {
    Option(Option(model.g._2).getOrElse(NOROOT).fetchOrElse(ModelOp.TYPE, NOREC).g._2).getOrElse(NOMAP)
      .filter(x => !x._2.glist.exists(y => y.domainObj.name == Tokens.lift_op)) // little optimization hack that will go away as model becomes more cleverly organized
      .flatMap(x => x._2.glist.filter(y => y.root) :+ x._1) // ctype + token ctype
      .distinct
      .foreach(c => this.createObj(c.asInstanceOf[Type[Obj]]))
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
      target.addEdge(Tokens.noop, g.R.has(NAME, aobj.rangeObj.name).next(), OBJ, NoOp())
    } else {
      bindObj(toBaseName(aobj.rangeObj)).addEdge(Tokens.noop, target, OBJ, NoOp())
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

  def coerce(source:Obj, target:Obj):Stream[Obj] = {
    if (target.name.equals(model.coreName)) return Stream(model)
    def lstTest(alst:Lst[Obj], bobj:Obj):Boolean = bobj match {
      case blst:Lst[Obj] => blst.ctype || (Poly.sameSep(alst, blst) && alst.size == blst.size &&
        !alst.glist.zip(blst.glist).forall(p => __.isAnon(p._1) || (p._1.name.equals(p._2.name))))
      case _ => false
    }

    val sroot:Obj = bindObj(asType(source.rangeObj)).obj
    val troot:Vertex = bindObj(asType(target.domainObj))
    JavaConverters.asScalaIterator(
      g.withSack(sroot)
        .R
        .has(CTYPE, NAME, sroot.name)
        ///////// ALL MATCHING PERMUTATIONS OF DOMAIN /////////
        .flatMap((t:Traverser[Vertex]) => {
          JavaConverters.asJavaIterator((t.sack[Obj] match {
            case alst:Lst[Obj] if lstTest(alst, t.get.obj) =>
              alst.glist.zip(t.get.obj.asInstanceOf[Lst[Obj]].glist).flatMap(pair => coerce(pair._1, pair._2))
                .foldLeft(List.empty[Obj])((a, b) => a :+ b)
                .combinations(alst.size).toList.distinct
                .filter(x => x.size == alst.size)
                .filter(x => x.forall(_.alive))
                .map(x => alst.clone(_ => x).named(t.get.obj.name))
                .map(x => {
                  if (x.glist.zip(alst.glist).forall(pair => finalStructureTest(pair._1, pair._2))) x
                  else alst.combine(x)
                })
                .iterator
            case alst:Lst[Obj] if t.get.obj.asInstanceOf[Lst[Obj]].ctype => Iterator(alst)
            case alst:Lst[Obj] if alst.size != t.get.obj.asInstanceOf[Lst[Obj]].size => Iterator(zeroObj)
            case arec:Rec[Obj, Obj] => t.get.obj match {
              case brec:RecType[Obj, Obj] if brec.ctype => Iterator(arec.named(brec.name))
              case brec:Rec[Obj, Obj] =>
                val z = arec.clone(name = brec.name, g = (brec.gsep,
                  arec.gmap.flatMap(a => brec.gmap
                    .filter(b => finalStructureTest(a._1, b._1))
                    .map(b => (coerce(a._1, b._1).headOption.getOrElse(zeroObj), coerce(a._2, b._2).headOption.getOrElse(zeroObj))))
                    .filter(b => b._1.alive && b._2.alive)))
                if (z.gmap.size < brec.gmap.count(x => x._2.q._1.g > 0)) Iterator(zeroObj) else Iterator(z)
              case _ => Iterator(zeroObj)
            }
            case aobj => Iterator(aobj)
          })
            .filter(_.alive)
            .map(x => source.trace.reconstruct[Obj](x, source.name))
            .map(sack => (t.get, sack)))
            .asInstanceOf[java.util.Iterator[Vertex]] // hack on typing (necessary because TP3 doesn't have flatmap on traverser)
        })
        .sideEffect((t:Traverser[Vertex]) => {
          val sack = t.get.asInstanceOf[(Vertex, Obj)]._2
          t.asAdmin().sack(sack)
          t.asAdmin().set(t.get.asInstanceOf[(Vertex, Obj)]._1)
        })
        .until((t:Traverser[Vertex]) => { // acyclic walk of obj graph, applying edge instructions, until target is reached
          t.get.obj.root && (__.isAnon(troot.obj) || t.get.obj.name == troot.obj.name || (baseName(troot.obj).equals(baseName(t.sack[Obj]()))))
          // finalStructureTest(t.sack[Obj].rangeObj,troot.obj.domainObj)
        })
        .repeat(___
          .simplePath() // no cycles allowed
          .outE()
          .sideEffect((t:Traverser[Edge]) => t.sack(t.get.inst.exec(t.sack[Obj]))) // evaluate edge instruction
          .filter((t:Traverser[_]) => t.sack[Obj] match { // filter out any {0} results
            case apoly:Poly[Obj] => apoly.alive && apoly.glist.forall(_.alive)
            case x => x.alive
          })
          .inV()
          .sideEffect((t:Traverser[Vertex]) => {
            val sack = t.sack[Obj]
            if (!__.isAnonToken(t.get.obj.rangeObj) && sack.root)
              t.sack(t.get.obj.rangeObj <= sack)
            else
              t.sack(sack.named(t.get.obj.name))
          })) // name type accordingly
        .sack[Obj]
    ).toStream
      .map(obj => target.trace.reconstruct[Obj](obj, target.name).hardQ(target.q))
      // if source was a value, compute the value against the derived type
      .map(obj => {
        source match {
          case _:Value[_] => Try[Obj](source.update(model).compute(obj, withAs = false).named(target.name)).getOrElse(zeroObj)
          case _:Type[_] => obj
        }
      })
      .filter(x => finalStructureTest(x.rangeObj, target.rangeObj))
      .distinct
  }

  def finalStructureTest(a:Obj, b:Obj):Boolean = {
    if (a.alive != b.alive || a.name != b.name) return false
    if (__.isAnon(b)) return true
    val aobj:Obj = a
    val bobj:Obj = Obj.resolveToken(__.update(model), b)
    aobj match {
      case alst:Lst[Obj] =>
        bobj match {
          case blst:Lst[Obj] =>
            Poly.sameSep(alst, blst) &&
              alst.size == blst.size &&
              alst.glist.zip(blst.glist).forall(p => finalStructureTest(p._1, p._2))
          case _ => false
        }
      case arec:Rec[Obj, Obj] =>
        bobj match {
          case brec:Rec[Obj, Obj] =>
            Poly.sameSep(arec, brec) &&
              brec.isEmpty == arec.isEmpty &&
              arec.gmap.forall(p => qStar.equals(p.q) || brec.gmap.exists(q => finalStructureTest(p._1, q._1) && p._2.test(q._2))) //finalStructureTest(p._1,q._1) && finalStructureTest(p._2,q._2)))
          case _ => false
        }
      case _:Value[Obj] if bobj.isInstanceOf[Value[Obj]] => aobj.equals(bobj)
      case _ => true
    }
  }
}
