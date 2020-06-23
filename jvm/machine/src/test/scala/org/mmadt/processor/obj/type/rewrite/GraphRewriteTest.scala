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

package org.mmadt.processor.obj.`type`.rewrite

import org.mmadt.language.obj.value.strm.RecStrm
import org.mmadt.language.obj.value.{RecValue, StrValue, Value}
import org.mmadt.language.obj.{Obj, Rec, Str}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class GraphRewriteTest extends FunSuite {
  private type Vertex = RecValue[StrValue, Value[Obj]]
  private type Graph = RecValue[StrValue, Value[Obj]]

  val vertex: Rec[Str, Obj] = rec(str("id") -> int, str("outE") -> tobj("edge").q {
    *
  })
  val edge: Rec[Str, Obj] = rec(str("id") -> int, str("inV") -> tobj("vertex"), str("outV") -> tobj("vertex"), str("label") -> str)

  /*val model: Model = Model.simple().
    put(rec(str("id") -> int, str("outE") -> tobj("edge").q(*), str("inE") -> tobj("edge").q(*)), rec(str("id") -> int, str("outE") -> tobj("edge").q(*), str("inE") -> tobj("edge").q(*)).named("vertex")).
    put(rec(str("outV") -> tobj("vertex"), str("label") -> str, str("inV") -> tobj("vertex")), rec(str("outV") -> tobj("vertex"), str("label") -> str, str("inV") -> tobj("vertex")).named("edge")).
    put(rec(str("id") -> int, str("outE") -> tobj("edge").q(*), str("inE") -> tobj("edge").q(*)).named("vertex").q(*), rec(str("id") -> int, str("outE") -> tobj("edge").q(*), str("inE") -> tobj("edge").q(*)).q(*).named("graph"))
  println(model)

 */

  test("model types") {
    val marko: Vertex = rec(str("id") -> int(1)).asInstanceOf[RecValue[StrValue,Value[Obj]]]
    val vadas: Vertex = rec(str("id") -> int(2)).asInstanceOf[RecValue[StrValue,Value[Obj]]]
    val lop: Vertex = rec(str("id") -> int(3)).asInstanceOf[RecValue[StrValue,Value[Obj]]]
    val josh: Vertex = rec(str("id") -> int(4)).asInstanceOf[RecValue[StrValue,Value[Obj]]]
    val ripple: Vertex = rec(str("id") -> int(5)).asInstanceOf[RecValue[StrValue,Value[Obj]]]
    val peter: Vertex = rec(str("id") -> int(6)).asInstanceOf[RecValue[StrValue,Value[Obj]]]
    val graph: RecStrm[StrValue, Value[Obj]] = rec(marko, vadas, lop, josh, ripple, peter)

    assertResult(6)(graph.values.length)
    graph.values.foreach(v => assert(v.test(vertex)))
    graph.values.foreach(v => assert(!v.test(edge)))

    assertResult(6)(graph.get("id").is(int.gt(0)).is(int.lt(7)).toStrm.values.length)
    assertResult(5)(graph.get("id").is(int.gt(0)).is(int.lt(6)).toStrm.values.length)
    assertResult(4)(graph.get("id").is(int.gt(1)).is(int.lt(6)).toStrm.values.length)
  }

  test("connected values") {
    def makeEdge(outV: Vertex, label: String, inV: Vertex) = {
      rec(str("outV") -> outV, str("label") -> str(label), str("inV") -> inV)
    }
    var marko: Vertex = rec(str("id") -> int(1)).asInstanceOf[RecValue[StrValue,Value[Obj]]]
    val vadas: Vertex = rec(str("id") -> int(2)).asInstanceOf[RecValue[StrValue,Value[Obj]]]
    val lop: Vertex = rec(str("id") -> int(3)).asInstanceOf[RecValue[StrValue,Value[Obj]]]
    val josh: Vertex = rec(str("id") -> int(4)).asInstanceOf[RecValue[StrValue,Value[Obj]]]
    val ripple: Vertex = rec(str("id") -> int(5)).asInstanceOf[RecValue[StrValue,Value[Obj]]]
    val peter: Vertex = rec(str("id") -> int(6)).asInstanceOf[RecValue[StrValue,Value[Obj]]]

   // marko = marko.put(str("outE"), rec(makeEdge(marko, "knows", vadas), makeEdge(marko, "created", lop), makeEdge(marko, "knows", josh)))
   // val graph: RecStrm[StrValue, Value[Obj]] = rec(marko, vadas, lop, josh, ripple, peter)

    /*assertResult(6)(graph.values.length)
    graph.values.foreach(v => assert(v.test(vertex)))
    graph.values.foreach(v => assert(!v.test(edge)))
    assertResult(str(str("knows").q(2), "created"))(graph.is(vertex.get("id").eqs(int(1))).get("outE", edge).get("label"))
    *///assertResult(str("created"))(graph.is(__.get("id").eqs(int(1))).get("outE", edge).is(__.get("label", str).eqs("created")).get("label"))
  }

}