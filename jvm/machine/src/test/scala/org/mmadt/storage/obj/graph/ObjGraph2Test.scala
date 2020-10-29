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

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Obj.{intToInt, stringToStr, symbolToToken, tupleToRecYES}
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp
import org.mmadt.language.obj.{Obj, toBaseName}
import org.mmadt.storage
import org.mmadt.storage.StorageFactory.{?, bool, int, lst, qStar, real, rec, str}
import org.mmadt.storage.obj.graph.ObjGraph.OBJ
import org.scalatest.FunSuite

import scala.collection.convert.ImplicitConversions.`iterator asScala`

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ObjGraph2Test extends FunSuite {

  test("type existence w/ pair") {
    val graph:ObjGraph2 = ObjGraph2.create(ModelOp.MM
      .defining('apair <= (int.to('m) `;` int.to('n)).is(from('m, int).lt(from('n, int))))
      .defining('pair <= (str `;` str).to('x).:=(plus('x.get(1)) `;` plus('x.get(0)))))
    //  assertResult(Stream('apair(1 `;` 2)))(graph.coerce(1 `;` 2, 'apair))
    assertResult(Nil)(graph.coerce(10 `;` 2, 'apair))
    // assertResult(Stream('pair("ab" `;` "ba")))(graph.coerce("a" `;` "b", 'pair))
  }

  test("type existence w/ pg_*") {
    List('pg_1, 'pg_2, 'pg_3).foreach(symbol => {
      val graph:ObjGraph2 = ObjGraph2.create(symbol)
      assert(graph.exists(bool))
      assert(graph.exists(int))
      assert(graph.exists(real))
      assert(graph.exists(str))
      assert(graph.exists(lst))
      assert(graph.exists(rec))
      assert(graph.exists('poly))
      assert(graph.exists('vertex))
      assert(graph.exists('edge))
      if (symbol.name.contains("_1")) {
        assert(graph.exists('vertex(str("id") -> int)))
        assert(graph.exists('edge(str("outV") -> __('vertex) `_,` str("inV") -> 'vertex)))
      }
      if (symbol.name.contains("_2")) {
        assert(graph.exists('vertex(str("id") -> int)))
        assert(graph.exists('edge(str("outV") -> __('vertex) `_,` str("inV") -> 'vertex)))
        assert(graph.exists('vertex `;` 'vertex))
      }
      if (symbol.name.contains("_3")) {
        assert(graph.exists('vertex(str("id") -> int `_,` str("label") -> str)))
        assert(graph.exists('edge(str("outV") -> __('vertex) `_,` str("label") -> str `_,` str("inV") -> __('vertex))))
        assert(graph.exists('vertex `;` 'vertex))
      }
      //////////////////////////////////
      assert(!graph.exists(int.plus(2)))
      assert(!graph.exists('vertex.get(str("id"))))
    })
  }

  test("type construction w/ none") {
    val graph:ObjGraph2 = ObjGraph2.create('none)
    assertResult(Stream(int))(graph.coerce(int, int))
    assertResult(Stream(int(45)))(graph.coerce(45, int))
    assertResult(Stream(int.plus(11)))(graph.coerce(int.plus(11), int))
    assertResult(Stream(int.plus(10)))(graph.coerce(int, int.plus(10)))
    // assertResult(Stream(int(32)))(graph.coerce(22, int.plus(10)))
    assertResult(Nil)(graph.coerce(int(35), str))
  }

  test("type construction w/ pg_1") {
    val graph:ObjGraph2 = ObjGraph2.create(storage.model('pg_1))
    assertResult(Seq('vertex(str("id") -> int(5))))(graph.coerce(rec(str("id") -> int(5)), 'vertex))
    assertResult(Seq('vertex(str("id") -> int(6)) `;` 'vertex(str("id") -> int(7))))(graph.coerce(rec(str("id") -> int(6)) `;` rec(str("id") -> int(7)), 'vertex `;` 'vertex))
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> int) `_,` str("inV") -> 'vertex(str("id") -> int))))(graph.coerce(str("outV") -> rec(str("id") -> int) `_,` str("inV") -> rec(str("id") -> int), 'edge))
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> int(8)) `_,` str("inV") -> 'vertex(str("id") -> int(9)))))(graph.coerce(str("outV") -> rec(str("id") -> int(8)) `_,` str("inV") -> rec(str("id") -> int(9)), 'edge))
  }

  test("type construction w/ pg_2") {
    val graph:ObjGraph2 = ObjGraph2.create(storage.model('pg_2).defining('nat <= int.is(gt(0))).defining(int <= (int `;` int `;` int).get(1)))
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> int(5)) `_,` str("inV") -> 'vertex(str("id") -> int(5)))))(graph.coerce(graph.model.s(int(5)) -< (__ `;` __), 'edge))
    assertResult('edge(str("outV") -> 'vertex(str("id") -> int(5)) `_,` str("inV") -> 'vertex(str("id") -> int(5))))(graph.model.s(int(5)) -< ('vertex `;` 'vertex) `=>` 'edge)
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> int(5)) `_,` str("inV") -> 'vertex(str("id") -> int(5)))))(graph.coerce(graph.model.s(int(5)) -< ('vertex `;` 'vertex), 'edge))
    //    assertResult('edge<=int.split(('vertex<=int-<(str("id")->int))`;`('vertex<=int-<(str("id")->int))).split((str("outV") -> ('vertex <= get(0))) `_,`(str("inV") -> ('vertex <= get(1)))))(graph.coerce(graph.model.s(int)-<('vertex`;`'vertex), 'edge))
    assertResult(Seq(int <= (int `;` int `;` int).get(1)))(graph.coerce((int `;` int `;` int), int))
    assertResult(Seq('nat <= int.is(gt(0))))(graph.coerce(int, 'nat))
    assertResult(Seq('nat <= int.plus(10).is(gt(0))))(graph.coerce(int.plus(10), 'nat))
    assertResult(Seq('nat <= (int `;` int `;` int).get(1).is(gt(0))))(graph.coerce((int `;` int `;` int), 'nat))
    assertResult(Seq('vertex(str("id") -> int) <= (int `;` int `;` int).get(1).-<(str("id") -> __)))(graph.coerce((int `;` int `;` int), 'vertex))
    assertResult(Seq('vertex(str("id") -> int(2))))(graph.coerce((1 `;` 2 `;` 3), 'vertex))
    assertResult(Seq('vertex(str("id") -> int)))(graph.coerce('vertex, 'vertex))
    assertResult(Seq('vertex(str("id") -> int) <= int.-<(str("id") -> __)))(graph.coerce(int, 'vertex))
    assertResult(Seq('vertex(str("id") -> int(6))))(graph.coerce(6, 'vertex))
    assertResult(Seq(int(6)))(graph.coerce(1 `;` 6 `;` 3, int))
    assertResult(Seq('edge <= ('vertex `;` 'vertex).-<((str("outV") -> get(0)) `_,`(str("inV") -> get(1)))))(Stream('edge <= graph.coerce('vertex `;` 'vertex, 'edge).head))
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> int(8)) `_,` str("inV") -> 'vertex(str("id") -> int(9)))))(graph.coerce('vertex(str("id") -> int(8)) `;` 'vertex(str("id") -> int(9)), 'edge))
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> int(81)) `_,` str("inV") -> 'vertex(str("id") -> int(91)))))(graph.coerce(81 `;` 91, 'edge))
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> int(81)) `_,` str("inV") -> 'vertex(str("id") -> int(91)))))(graph.coerce(lst(g = (Tokens.`;`, List((1 `;` 81 `;` 2), (3 `;` 91 `;` 24)))), 'edge))
    // assertResult(Seq('edge <= (int `;` int).combine(('vertex <= int.-<(str("id") -> int)) `;`('vertex <= int.-<(str("id") -> int))).-<((str("outV") -> (get(0))) `_,`(str("inV") -> (get(1))))))(Stream('edge <= graph.coerce(int `;` int, 'edge).head))
  }

  test("type construction w/ digraph") {
    val graph:ObjGraph2 = ObjGraph2.create('digraph)
    // GraphSONWriter.build().create().writeGraph(new FileOutputStream(new File("/Users/marko/Desktop/digraph.json")),graph.graph)
    assertResult(str("id") -> __('nat) `_,` str("attrs") -> __('attr).q(qStar))(toBaseName(storage.model('digraph).findCtype("vertex").get))
    val tokens:List[Obj] = graph.g.V().values[Obj](OBJ).toSeq.filter(x => __.isTokenRoot(x)).toList
    println(tokens)
    assertResult(2)(tokens.length) // TODO: I don't like the ambiguousness of tokens vs. their canonical form (this needs to be settled)
    assert(tokens.contains(__('nat)))
    assert(tokens.contains(__('poly)))
    //
    assertResult(Stream(graph.model))(graph.coerce('digraph, 'digraph))
    assertResult(Stream(int))(graph.coerce(int, int))
    assertResult(Stream(int(45)))(graph.coerce(45, int))
    assertResult(Nil)(graph.coerce(str("bad_id") -> int(12), 'vertex))
    assertResult(Nil)(graph.coerce(0, 'vertex))
    assertResult(Nil)(graph.coerce("0", 'vertex))
    assertResult(Nil)(graph.coerce((20 `;` "marko"), 'attr))
    assertResult(Seq(int(23)))(graph.coerce('vertex(str("id") -> 'nat(23)), int))
    assertResult(Seq('vertex(str("id") -> 'nat(1)) `;` 'vertex(str("id") -> 'nat(2))))(graph.coerce('nat(1) `;` 'nat(2), 'vertex `;` 'vertex))
    assertResult(Seq('attr(str("key") -> str("a") `_,` str("value") -> str("b"))))(graph.coerce(str("a") `;` "b", 'attr))
    assertResult(Seq('attr <= (str `;` str).combine(str `;` str.id).-<(str("key") -> (str `;` id).get(0) `_,` str("value") -> (str `;` id).get(1))))(Stream('attr <= graph.coerce(str `;` str, 'attr).head))
    assertResult(Seq('vertex(str("id") -> 'nat(23))))(graph.coerce('nat(23), 'vertex))
    assertResult(Seq('vertex(str("id") -> 'nat(23))))(graph.coerce(23, 'vertex))
    assertResult(Seq('vertex(str("id") -> 'nat(23)).q(3)))(graph.coerce(23.q(3), 'vertex))
    //assertResult(Seq('vertex(str("id") -> 'nat(23) `_,` str("attrs") -> 'attr(str("key") -> str("no") `_,` str("value") -> str("data")))))(graph.coerce(-23, 'vertex))
    assertResult(Seq('attr(str("key") -> str("marko") `_,` str("value") -> int(29))))(graph.coerce(str("key") -> str("marko") `_,` str("value") -> int(29), 'attr))
    assertResult(Seq('vertex(str("id") -> 'nat(55) `_,` str("attrs") -> 'attr(str("key") -> str("marko") `_,` str("value") -> int(29)))))(graph.coerce('nat(55) `;` 'attr(str("key") -> str("marko") `_,` str("value") -> int(29)), 'vertex))
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> 'nat(100)) `_,` str("inV") -> 'vertex(str("id") -> 'nat(200)))))(graph.coerce('vertex(str("id") -> 'nat(100)) `;` 'vertex(str("id") -> 'nat(200)), 'edge))
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> 'nat(100)) `_,` str("inV") -> 'vertex(str("id") -> 'nat(200)))))(graph.coerce('nat(100) `;` 'nat(200), 'edge))
    //    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> 'nat(100)) `_,` str("inV") -> 'vertex(str("id") -> 'nat(200)))))(List(('nat(100) `;` 'nat(200)) ==>[Obj] graph.coerce('nat `;` 'nat, 'edge).head))
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> 'nat(100)) `_,` str("inV") -> 'vertex(str("id") -> 'nat(200)))))(graph.coerce(100 `;` 200, 'edge))
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> 'nat(100)) `_,` str("inV") -> 'vertex(str("id") -> 'nat(200)))))(graph.coerce(100 `;` 'nat(200), 'edge))
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> 'nat(100)) `_,` str("inV") -> 'vertex(str("id") -> 'nat(200)))))(List((100 `;` 200) ==>[Obj] graph.coerce(int `;` int, 'edge).last)) // COERCIONS STREAMS NEED To KNOWN BY RUNTIME METHODS
    //    assertResult(Seq('edge <= ('nat `;` 'nat).combine(('vertex <= 'nat.split(str("id") -> __('nat))) `;`('vertex <= 'nat.split(str("id") -> __('nat)))).split(str("outV") -> ('vertex `;` 'vertex).get(0) `_,` str("inV") -> ('vertex `;` 'vertex).get(1))))(graph.coerce('nat `;` 'nat, 'edge))
    /*    assertResult(Seq(
          'edge(
            str("outV") -> 'vertex(str("id") -> 'nat(1) `_,` str("attrs") -> 'attr(str("key") -> str("age") `_,` str("value") -> int(29))) `_,`
              str("inV") -> 'vertex(str("id") -> 'nat(2) `_,` str("attrs") -> 'attr(str("key") -> str("age") `_,` str("value") -> int(27))))))(
          graph.coerce(
            lst(g = (Tokens.`;`, List(
              'nat(1) `;` 'attr(str("key") -> str("age") `_,` str("value") -> int(29)),
              'nat(2) `;` 'attr(str("key") -> str("age") `_,` str("value") -> int(27))))), 'edge))*/
    val natattr = lst(g = (Tokens.`;`, List(('nat `;` 'attr), ('nat `;` 'attr))))
    /* assertResult(Seq('edge <= natattr
       .combine(
         ('vertex <= ('nat `;` 'attr).split((str("id") -> get(0)) `_,` str("attrs") -> get(1))) `;`
           ('vertex <= ('nat `;` 'attr).split(str("id") -> get(0) `_,` str("attrs") -> get(1))))
       .split(str("outV") -> ('vertex `;` 'vertex).get(0) `_,` str("inV") -> ('vertex `;` 'vertex).get(1))))(Stream('edge<=graph.coerce(natattr, 'edge).head))*/
  }

  test("type construction w/ time") {
    val graph:ObjGraph2 = ObjGraph2.create('time)
    assertResult(Seq('date <= (int `;` int `;` int).combine(('nat.q(?) <= int.is(gt(0))).is(lte(12)) `;` ('nat.q(?) <= int.is(gt(0))).is(lte(31)) `;`('nat <= int.is(gt(0))))))(Stream('date <= graph.coerce(int `;` int `;` int, 'date).head))
    assertResult(Seq('date('nat(8) `;` 'nat(26) `;` 'nat(2020))))(graph.coerce(8 `;` 26 `;` 2020, 'date))
    assertResult(Seq('date('nat(8) `;` 'nat(26) `;` 'nat(2020))))(graph.coerce(8 `;` 26, 'date))
    assertResult(Nil)(graph.coerce(8, 'date))
  }

  test("coercion of base types") {
    assertResult(Nil)(ObjGraph2.create(storage.model('none)).coerce(4, str))
    val graph = ObjGraph2.create(storage.model('none).defining(str <= int).defining(real <= int).defining(lst))
    assertResult(Stream(str <= int))(graph.coerce(int, str))
    assertResult(Stream(real(6.0)))(graph.coerce(6, real))
    assertResult(Stream(str("4")))(graph.coerce(4, str))
    assertResult(Stream(str <= int.plus(10)))(graph.coerce(int.plus(10), str))
    // assertResult((2`;`5))((1`;`2)`=>`(int.plus(1)`;`int.plus(3)))
    assertResult(Stream(2 `;` 5))(graph.coerce((1 `;` 2), (int.plus(1) `;` int.plus(3))))
  }

  test("dependent sum construction w/ custom types") {
    val graph = ObjGraph2.create(storage.model('num).defining('apair <= (int.to('m) `;` int.to('n)).is(from('m, int).lt(from('n, int)))).defining(str <= int))
    // graph.paths(__, __).foreach(x => println(x))
    assertResult(Stream(int(45)))(graph.coerce(45, int))
    assertResult(Stream(int))(graph.coerce(int, int))
    assertResult(Stream(int.q(5)))(graph.coerce(int.q(5), int.q(5)))
    assertResult(Stream(int.q(5)))(graph.coerce(int.q(5), int))
    // assertResult(Stream(int))(graph.coerce(int, int.q(5)))  TODO: decide on the algebra of coercion (is it just a monoidal operation) -- determines quantifier evoluation
    assertResult(Stream(int))(graph.coerce(int, int <= int))
    assertResult(Stream('nat <= int.is(gt(0))))(graph.coerce(int, 'nat))
    assertResult(List(int <=[__] 'nat))(graph.coerce('nat, int))
    assertResult(List(int(2)))(graph.coerce('nat(2), int))
    //assertResult(List(int(2).q(30)))(graph.coerce('nat(2).q(5), int.q(6)))
    assertResult(List(str <= int))(graph.coerce(int, str))
    //    assertResult(List(str("2")))(graph.coerce(int(2), str))
    assertResult(List('nat(566)))(graph.coerce(566, 'nat))
    //    assertResult(List('apair(5 `;` 6)))(graph.coerce((5 `;` 6), 'apair))
    //    assertResult(List('apair(5 `;` 6).q(10)))(graph.coerce((5 `;` 6).q(10), 'apair))
    // assertResult(List('apair(5 `;` 6).q(20)))(graph.coerce((5 `;` 6).q(10), __('apair).q(2)))
    assertResult(Nil)(graph.coerce((6 `;` 5), 'apair))
  }

  /* test("coercion on recursive types") {
     val rmodel = storage.model('mm)
       .defining('tree <= branch(??(0) `|`(int `;` 'tree `;` int)))
       .defining('ctree <= branch(??(0) `|` (int.to('x) `;` 'ctree `;` int.to('y)).is('x.gt('y))))
     val graph = ObjGraph2.create(rmodel)
     // single level
     assertResult(btrue)(lst(int(0)).model(rmodel) ==> a('tree))
     assertResult(btrue)((1 `;` 0 `;` 1).model(rmodel) ==> a('tree))
     assertResult(bfalse)((1 `;` 1).model(rmodel) ==> a('tree))
     assertResult(bfalse)((1 `;` 0 `;` "a").model(rmodel) ==> a('tree))
     assertResult(bfalse)((1 `;` 0 `;` 1 `;` 1).model(rmodel) ==> a('tree))
     // multi-level
     assertResult(btrue)((1 `;`(2 `;` 0 `;` 2) `;` 1).model(rmodel) ==> a('tree))
     assertResult(bfalse)((1 `;`(2 `;` 2 `;` 2) `;` 1).model(rmodel) ==> a('tree))
     assertResult(btrue)((1 `;`(2 `;`(3 `;` 0 `;` 3) `;` 2) `;` 1).model(rmodel) ==> a('tree))
     // coercion
     assertResult(Stream('tree(1 `;` 'tree(2 `;` 'tree(3 `;` 'tree(0) `;` 3) `;` 2) `;` 1)))(graph.coerce(1 `;`(2 `;`(3 `;` 0 `;` 3) `;` 2) `;` 1, 'tree))
     assertResult('tree(1 `;` 'tree(2 `;` 'tree(3 `;` 'tree(0) `;` 3) `;` 2) `;` 1))((1 `;`(2 `;`(3 `;` 0 `;` 3) `;` 2) `;` 1).model(rmodel) ==>[Obj] 'tree)
     ///////////
     // ctree //
     ///////////
     //  assertResult(btrue)(lst(int(0)).model(rmodel) ==> a('ctree))
     assertResult(btrue)((2 `;` 0 `;` 1).model(rmodel) ==> a('ctree))
     assertResult(bfalse)((1 `;` 0 `;` 1).model(rmodel) ==> a('ctree))
     /*  assertResult(bfalse)((3`;`(2`;`0`;`4)`;`1).model(rmodel) ==> a('ctree))
         assertResult(bfalse)((1`;`(2`;`0`;`4)`;`1).model(rmodel) ==> a('ctree))
         assertResult(btrue)((3`;`(2`;`0`;`1)`;`4).model(rmodel) ==> a('ctree)) */
   }*/


}
