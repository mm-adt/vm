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
import org.mmadt.language.obj.Obj.{intToInt, symbolToToken, tupleToRecYES}
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.`type`.__._
import org.mmadt.storage
import org.mmadt.storage.StorageFactory.{bool, int, lst, real, rec, str}
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ObjGraph2Test extends FunSuite {

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
    assertResult(Stream(int.plus(10)))(graph.coerce(int.plus(10), int))
    // assertResult(Stream(int.plus(10)))(graph.coerce(int,int.plus(10)))
    assertResult(Nil)(graph.coerce(int(35), str))
  }

  test("type construction w/ pg_1") {
    val graph:ObjGraph2 = ObjGraph2.create(storage.model('pg_1))
    //assertResult(Seq('vertex(str("id") -> int(5))))(graph.coerce(rec(str("id") -> int(5)), 'vertex))
    //assertResult(Seq('vertex(str("id") -> int(6)) `;` 'vertex(str("id") -> int(7))))(graph.coerce(rec(str("id") -> int(6)) `;` rec(str("id") -> int(7)), 'vertex `;` 'vertex))
    //assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> int(8)) `_,` str("inV") -> 'vertex(str("id") -> int(9)))))(graph.coerce(str("outV") -> rec(str("id") -> int(8)) `_,` str("inV") -> rec(str("id") -> int(9)), 'edge))
  }

  test("type construction w/ pg_2") {
    val graph:ObjGraph2 = ObjGraph2.create(storage.model('pg_2).defining('nat <= int.is(gt(0))).defining(int <= (int `;` int `;` int).get(1)))
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> int(5)) `_,` str("inV") -> 'vertex(str("id") -> int(5)))))(graph.coerce(graph.model.s(int(5)) -< (__ `;` __), 'edge))
    assertResult('edge(str("outV") -> 'vertex(str("id") -> int(5)) `_,` str("inV") -> 'vertex(str("id") -> int(5))))(graph.model.s(int(5)) -< ('vertex `;` 'vertex) `=>` 'edge)
    assertResult(Seq('edge(str("outV") -> 'vertex(str("id") -> int(5)) `_,` str("inV") -> 'vertex(str("id") -> int(5)))))(graph.coerce(graph.model.s(int(5)) -< ('vertex `;` 'vertex), 'edge))
    //assertResult('edge<=int.split(('vertex<=int-<(str("id")->int))`;`('vertex<=int-<(str("id")->int))).split((str("outV") -> ('vertex <= get(0))) `_,`(str("inV") -> ('vertex <= get(1)))))(graph.coerce(graph.model.s(int)-<('vertex`;`'vertex), 'edge))
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
    //assertResult(Seq('edge <= (int `;` int).combine(('vertex <= int.-<(str("id") -> int)) `;`('vertex <= int.-<(str("id") -> int))).-<((str("outV") -> (get(0))) `_,`(str("inV") -> (get(1))))))(graph.coerce(int `;` int, 'edge))
  }

  test("type construction w/ time") {
    val graph:ObjGraph2 = ObjGraph2.create('time)
    //assertResult(Seq('date('nat(8) `;` 'nat(26) `;` 'nat(2020))))(graph.coerce(8 `;` 26 `;` 2020, 'date))
    // assertResult(Seq('date('nat(8) `;` 'nat(26) `;` 'nat(2020))))(graph.coerce(8 `;` 26, 'date))
    assertResult(Nil)(graph.coerce(8, 'date))
  }

  test("coercion of base types") {
    assertResult(Nil)(ObjGraph2.create(storage.model('none)).coerce(4, str))
    val graph = ObjGraph2.create(storage.model('none).defining(str <= int))
    //assertResult(Stream(str <= int))(graph.coerce(int, str))
    //assertResult(Stream(str("4")))(graph.coerce(4, str))
    assertResult(Stream(str <= int.plus(10)))(graph.coerce(int.plus(10), str))
    // assertResult((2`;`5))((1`;`2)`=>`(int.plus(1)`;`int.plus(3)))
    // assertResult(Stream(2`;`5))(graph.coerce((1`;`2),(int.plus(1)`;`int.plus(3))))

  }

}
