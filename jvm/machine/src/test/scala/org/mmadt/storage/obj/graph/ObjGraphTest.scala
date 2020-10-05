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

import org.mmadt.language.obj.Obj.{intToInt, stringToStr, symbolToToken, tupleToRecYES}
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__._
import org.mmadt.language.obj.op.trace.ModelOp
import org.mmadt.language.obj.{Obj, toBaseName}
import org.mmadt.storage
import org.mmadt.storage.StorageFactory.{bool, int, lst, qStar, real, rec, str}
import org.mmadt.storage.obj.graph.ObjGraph.{OBJ, ObjGraph, ObjTraversal, ObjTraversalSource}
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ObjGraphTest extends FunSuite {
  test("type existence w/ pair") {
    val graph:ObjGraph = ObjGraph.create(ModelOp.MM
      .defining('apair <= (int.to('m) `;` int.to('n)).is(from('m, int).lt(from('n, int))))
      .defining('pair <= (str `;` str).to('x).:=(plus('x.get(1)) `;` plus('x.get(0)))))
    assertResult(Stream('apair(1 `;` 2)))(graph.fpath(1 `;` 2, 'apair))
    assertResult(Nil)(graph.fpath(10 `;` 2, 'apair))
    // assertResult(Stream('pair("ab" `;` "ba")))(graph.fpath("a" `;` "b", 'pair))

  }
  test("type existence w/ pg_2") {
    val graph:ObjGraph = ObjGraph.create('pg_2)
    assert(graph.exists(bool))
    assert(graph.exists(int))
    assert(graph.exists(real))
    assert(graph.exists(str))
    assert(graph.exists(lst))
    assert(graph.exists(rec))
    assert(graph.exists('poly))
    assert(graph.exists('vertex))
    assert(graph.exists('edge))
  }

  test("type construction w/ none") {
    val graph:ObjGraph = ObjGraph.create('none)
    assertResult(Stream(int))(graph.fpath(int, int))
    assertResult(Stream(int(45)))(graph.fpath(45, int))
  }

  test("type construction w/ pg_2") {
    val graph:ObjGraph = ObjGraph.create('pg_2)
    println(graph.fpath(int `;` int, 'edge))
    assertResult(Seq(__('vertex)))(graph.fpath('vertex, 'vertex))
    assertResult(Seq('vertex(str("id") -> int) <= int.-<(rec(str("id") -> __))))(graph.fpath(int, 'vertex))
    assertResult(Seq('edge <= ('vertex `;` 'vertex).-<((str("outV") -> ('vertex <= ('vertex `;` 'vertex).get(0))) `_,`(str("inV") -> ('vertex <= ('vertex `;` 'vertex).get(1))))))(Stream('edge <= graph.fpath('vertex `;` 'vertex, 'edge).head))
    assertResult(Seq('edge <= (('vertex `;` 'vertex) <= (int `;` int).-<((str("outV") -> ('vertex <= ('vertex `;` 'vertex).get(0))) `_,`(str("inV") -> ('vertex <= ('vertex `;` 'vertex).get(1)))))))(Stream('edge <= graph.fpath(int `;` int, 'edge).head))
  }

  test("type construction w/ digraph") {
    val graph:ObjGraph = ObjGraph.create('digraph)
    println(graph.g.R.values(OBJ).toList)
    assertResult(str("id") -> __('nat) `_,` str("attrs") -> __('attr).q(qStar))(toBaseName(storage.model('digraph).findCtype("vertex").get))
    val tokens:List[Obj] = graph.g.V().values[Obj](OBJ).toSeq.filter(x => __.isTokenRoot(x)).toList
    assertResult(2)(tokens.length)
    assert(tokens.contains(__('nat)))
    assert(tokens.contains(__('poly)))
    //
    assertResult(Stream(graph.model))(graph.fpath('digraph, 'digraph))
    assertResult(Stream(int))(graph.fpath(int, int))
    assertResult(Stream(int(45)))(graph.fpath(45, int))
    assertResult(Nil)(graph.fpath(str("bad_id") -> int(12), 'vertex))
    assertResult(Nil)(graph.fpath(0, 'vertex))
    assertResult(Seq('vertex(str("id") -> 'nat(1)) `;` 'vertex(str("id") -> 'nat(2))))(graph.fpath('nat(1) `;` 'nat(2), 'vertex `;` 'vertex))
    assertResult(Seq('attr(str("key") -> str("a") `_,` str("value") -> str("b"))))(graph.fpath(str("a") `;` "b", 'attr))
    // assertResult(Seq('attr <= (str `;` str).-<(str("key") -> (str `;` id).get(0) `_,` str("value") -> (str `;` id).get(1))))(Stream('attr <= graph.fpath(str `;` str, 'attr).head))
    assertResult(Seq('vertex(str("id") -> 'nat(23))))(graph.fpath('nat(23), 'vertex))
    assertResult(Seq('vertex(str("id") -> 'nat(23))))(graph.fpath(23, 'vertex))
    // assertResult(Seq('vertex(str("id") -> 'nat(23) `_,` str("attrs") -> 'attr(str("key") -> str("no") `_,` str("value") -> str("data")))))(graph.fpath(-23, 'vertex))
    assertResult(Seq('attr(str("key") -> str("marko") `_,` str("value") -> int(29))))(graph.fpath(str("key") -> str("marko") `_,` str("value") -> int(29), 'attr))
    assertResult(Seq('vertex(str("id") -> 'nat(55) `_,` str("attrs") -> 'attr(str("key") -> str("marko") `_,` str("value") -> int(29)))))(graph.fpath('nat(55) `;` 'attr(str("key") -> str("marko") `_,` str("value") -> int(29)), 'vertex))
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> 'nat(100)) `_,` str("inV") -> 'vertex(str("id") -> 'nat(200)))))(graph.fpath('vertex(str("id") -> 'nat(100)) `;` 'vertex(str("id") -> 'nat(200)), 'edge))
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> 'nat(100)) `_,` str("inV") -> 'vertex(str("id") -> 'nat(200)))))(graph.fpath('nat(100) `;` 'nat(200), 'edge))
  }

  test("type construction w/ time") {
    val graph:ObjGraph = ObjGraph.create('time)
    println(graph.g.R.values(OBJ).toList)
    assertResult(Seq('date('nat(8) `;` 'nat(26) `;` 'nat(2020))))(graph.fpath(8 `;` 26 `;` 2020, 'date))
    assertResult(Seq('date('nat(8) `;` 'nat(26) `;` 'nat(2020))))(graph.fpath(8 `;` 26, 'date))
    assertResult(Nil)(graph.fpath(8, 'date))
  }

}
