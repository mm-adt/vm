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

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.{outE, simplePath}
import org.mmadt.language.obj.Obj.{intToInt, stringToStr, symbolToToken, tupleToRecYES}
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp
import org.mmadt.language.obj.{Obj, toBaseName}
import org.mmadt.storage
import org.mmadt.storage.StorageFactory.{bool, int, lst, qStar, real, rec, str, strm}
import org.mmadt.storage.obj.graph.ObjGraph.{ISO, OBJ}
import org.scalatest.FunSuite

import scala.collection.convert.ImplicitConversions.`iterator asScala`

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ObjGraphTest extends FunSuite {
  test("type existence w/ pair") {
    val graph:ObjGraph = ObjGraph.create(ModelOp.MM
      .defining('apair <= (int.to('m) `;` int.to('n)).is(from('m, int).lt(from('n, int))))
      .defining('pair <= (str `;` str).to('x).:=(plus('x.get(1)) `;` plus('x.get(0)))))
    assertResult(Stream('apair(1 `;` 2)))(graph.coerce(1 `;` 2, 'apair))
    assertResult(Nil)(graph.coerce(10 `;` 2, 'apair))
    // assertResult(Stream('pair("ab" `;` "ba")))(graph.fpath("a" `;` "b", 'pair))
  }
  test("type existence w/ pg_*") {
    List('pg_2, 'pg_2, 'pg_3).foreach(symbol => { // 'pg_1 bad
      val graph:ObjGraph = ObjGraph.create(symbol)
      assert(graph.exists(bool))
      assert(graph.exists(int))
      assert(graph.exists(real))
      assert(graph.exists(str))
      assert(graph.exists(lst))
      assert(graph.exists(rec))
      assert(graph.exists('poly))
      assert(graph.exists('vertex))
      assert(graph.exists('edge))
      assert(graph.exists('vertex `;` 'vertex))
      assert(graph.exists('vertex(str("id") -> __)))
      //
      assert(!graph.exists(int.plus(2)))
      assert(!graph.exists('vertex.get(str("id"))))
    })
  }

  test("type construction w/ none") {
    val graph:ObjGraph = ObjGraph.create('none)
    assertResult(Stream(int))(graph.coerce(int, int))
    assertResult(Stream(int(45)))(graph.coerce(45, int))
    assertResult(Nil)(graph.coerce(int(35), str))
  }

  test("type construction w/ pg_2") {
    val graph:ObjGraph = ObjGraph.create(storage.model('pg_2).defining('nat <= int.is(gt(0))).defining(int <= (int `;` int `;` int).get(1)))
    graph.g.V().repeat(outE().inV()).until(outE().count().is(0).and(simplePath())).path().by(ISO).forEachRemaining(x => println(x))
    println(graph.coerce(int, str))
    println(graph.coerce(int `;` int, 'edge))
    assertResult(Seq(int <= (int `;` int `;` int).get(1)))(graph.coerce((int `;` int `;` int), int))
    assertResult(Seq('nat <= int.is(gt(0))))(graph.coerce(int, 'nat))
    assertResult(Seq('nat <= int.plus(10).is(gt(0))))(graph.coerce(int.plus(10), 'nat))
    assertResult(Seq('nat <= (int `;` int `;` int).get(1).is(gt(0))))(graph.coerce((int `;` int `;` int), 'nat))
    assertResult(Seq(__('vertex)))(graph.coerce('vertex, 'vertex))
    assertResult(Seq('vertex(str("id") -> int) <= int.-<(rec(str("id") -> __))))(graph.coerce(int, 'vertex))
    assertResult(Seq('vertex(str("id") -> int(6))))(graph.coerce(6, 'vertex))
    assertResult(Seq(int(6)))(graph.coerce((1 `;` 6 `;` 3), int))
    assertResult(Seq('edge <= ('vertex `;` 'vertex).-<((str("outV") -> ('vertex <= ('vertex `;` 'vertex).get(0))) `_,`(str("inV") -> ('vertex <= ('vertex `;` 'vertex).get(1))))))(Stream('edge <= graph.coerce('vertex `;` 'vertex, 'edge).head))
    //assertResult(Seq('edge <= (('vertex `;` 'vertex) <= (int `;` int).-<((str("outV") -> ('vertex <= ('vertex `;` 'vertex).get(0))) `_,`(str("inV") -> ('vertex <= ('vertex `;` 'vertex).get(1)))))))(Stream('edge <= graph.fpath(int `;` int, 'edge).head))
  }

  test("type construction w/ digraph") {
    val graph:ObjGraph = ObjGraph.create('digraph)
    // GraphSONWriter.build().create().writeGraph(new FileOutputStream(new File("/Users/marko/Desktop/digraph.json")),graph.graph)
    graph.paths(__, __, OBJ).foreach(x => println(x))
    assertResult(str("id") -> __('nat) `_,` str("attrs") -> __('attr).q(qStar))(toBaseName(storage.model('digraph).findCtype("vertex").get))
    val tokens:List[Obj] = graph.g.V().values[Obj](OBJ).toSeq.filter(x => __.isTokenRoot(x)).toList
    println(tokens)
    assertResult(3)(tokens.length) // TODO: I don't like the ambiguousness of tokens vs. their canonical form (this needs to be settled)
    assert(tokens.contains(__('nat)))
    assert(tokens.contains(__('poly)))
    //

    assertResult(Stream(graph.model))(graph.coerce('digraph, 'digraph))
    assertResult(Stream(int))(graph.coerce(int, int))
    assertResult(Stream(int(45)))(graph.coerce(45, int))
    assertResult(Nil)(graph.coerce(str("bad_id") -> int(12), 'vertex))
    assertResult(Nil)(graph.coerce(0, 'vertex))
    assertResult(Nil)(graph.coerce("0", 'vertex))
    assertResult(Seq(int(23)))(graph.coerce('vertex(str("id") -> 'nat(23)), int))
    assertResult(Seq('vertex(str("id") -> 'nat(1)) `;` 'vertex(str("id") -> 'nat(2))))(graph.coerce('nat(1) `;` 'nat(2), 'vertex `;` 'vertex))
    assertResult(Seq('attr(str("key") -> str("a") `_,` str("value") -> str("b"))))(graph.coerce(str("a") `;` "b", 'attr))
    assertResult(Seq('attr <= (str `;` str).-<(str("key") -> (str `;` id).get(0) `_,` str("value") -> (str `;` id).get(1))))(Stream('attr <= graph.coerce(str `;` str, 'attr).head))
    assertResult(Seq('vertex(str("id") -> 'nat(23))))(graph.coerce('nat(23), 'vertex))
    assertResult(Seq('vertex(str("id") -> 'nat(23))))(graph.coerce(23, 'vertex))
    assertResult(Seq('vertex(str("id") -> 'nat(23) `_,` str("attrs") -> 'attr(str("key") -> str("no") `_,` str("value") -> str("data")))))(graph.coerce(-23, 'vertex))
    assertResult(Seq('attr(str("key") -> str("marko") `_,` str("value") -> int(29))))(graph.coerce(str("key") -> str("marko") `_,` str("value") -> int(29), 'attr))
    assertResult(Seq('vertex(str("id") -> 'nat(55) `_,` str("attrs") -> 'attr(str("key") -> str("marko") `_,` str("value") -> int(29)))))(graph.coerce('nat(55) `;` 'attr(str("key") -> str("marko") `_,` str("value") -> int(29)), 'vertex))
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> 'nat(100)) `_,` str("inV") -> 'vertex(str("id") -> 'nat(200)))))(graph.coerce('vertex(str("id") -> 'nat(100)) `;` 'vertex(str("id") -> 'nat(200)), 'edge))
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> 'nat(100)) `_,` str("inV") -> 'vertex(str("id") -> 'nat(200)))))(graph.coerce('nat(100) `;` 'nat(200), 'edge))
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> 'nat(100)) `_,` str("inV") -> 'vertex(str("id") -> 'nat(200)))))(List(('nat(100) `;` 'nat(200)) ==>[Obj] graph.coerce('nat `;` 'nat, 'edge).head))
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> 'nat(100)) `_,` str("inV") -> 'vertex(str("id") -> 'nat(200)))))(graph.coerce(100 `;` 200, 'edge))
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> 'nat(100)) `_,` str("inV") -> 'vertex(str("id") -> 'nat(200)))))(List((100 `;` 200) ==>[Obj] graph.coerce(int `;` int, 'edge).head))
    /*    assertResult(8)(
          graph.fpath(
            (('nat(1) `;` 'attr(str("key") -> str("age") `_,` str("value") -> int(29))) `;`
              ('nat(2) `;` 'attr(str("key") -> str("age") `_,` str("value") -> int(27)))), 'edge))*/
    // assertResult(8)(graph.fpath((('nat `;` 'attr) `;`('nat `;` 'attr)), 'edge))
    // TODO: .... I don't think this is a good idea
    assertResult(Seq('edge <= ('nat `;` 'nat).combine(('vertex `;` 'vertex)).split(str("outV") -> ('vertex `;` 'vertex).get(0) `_,` str("inV") -> ('vertex `;` 'vertex).get(1))))(graph.coerce('nat `;` 'nat, 'edge))
    assertResult(Seq('edge <= ('nat `;` 'nat).combine(('vertex `;` 'vertex)).split(str("outV") -> ('vertex `;` 'vertex).get(0) `_,` str("inV") -> ('vertex `;` 'vertex).get(1))))(graph.coerce('nat `;` 'nat, 'edge))
  }

  test("play") {
    val graph:ObjGraph = ObjGraph.create('digraph)
    println("----")
    graph.paths(-6, 'vertex).foreach(x => println(x))
    println(graph.coerce(-23, 'vertex))
  }

  test("type construction w/ time") {
    val graph:ObjGraph = ObjGraph.create('time)
    graph.paths(8 `;` 24, 'date).foreach(x => println(x))
    assertResult(Seq('date('nat(8) `;` 'nat(26) `;` 'nat(2020))))(graph.coerce(8 `;` 26 `;` 2020, 'date))
    assertResult(Seq('date('nat(8) `;` 'nat(26) `;` 'nat(2020))))(graph.coerce(8 `;` 26, 'date))
    assertResult(Nil)(graph.coerce(8, 'date))
  }

  test("dependent sum construction w/ custom types") {
    val graph = ObjGraph.create(storage.model('num).defining('apair <= (int.to('m) `;` int.to('n)).is(from('m, int).lt(from('n, int)))).defining(str <= int))
    graph.paths(__, __, OBJ).foreach(x => println(x))
    assertResult(Stream(int(45)))(graph.coerce(45,int))
    assertResult(Stream(int))(graph.coerce(int,int))
    assertResult(Stream(int))(graph.coerce(int,int <=int))
    assertResult(Stream('nat<=int.is(gt(0))))(graph.coerce(int,'nat))
    assertResult(List(int <=[__] 'nat))(graph.coerce('nat, int))
    assertResult(List(int(2)))(graph.coerce('nat(2), int))
    assertResult(List(str <= int))(graph.coerce(int, str))
    assertResult(List(str("2")))(graph.coerce(int(2), str))
    assertResult(List('nat(566)))(graph.coerce(566, 'nat))
    assertResult(List('apair(5 `;` 6)))(graph.coerce((5 `;` 6), 'apair))
    assertResult(Nil)(graph.coerce((6 `;` 5), 'apair))
  }

  test("obj coercion directly") {
    val model = ObjGraph.create(storage.model('num)).model
    assertResult(strm[Obj]('nat(5)))(5.update(model).coerce(__('nat)))
  }
}
