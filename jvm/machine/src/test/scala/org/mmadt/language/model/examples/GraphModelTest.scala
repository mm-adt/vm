/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 *  mm-ADT is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU Affero General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 *  License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 *  You can be released from the requirements of the license by purchasing a
 *  commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language.model.examples

import org.mmadt.language.model.Model
import org.mmadt.language.obj.value.{RecValue, StrValue, Value}
import org.mmadt.language.obj.{Obj, Rec}
import org.mmadt.storage.StorageFactory._
import org.scalatest.FunSuite

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class GraphModelTest extends FunSuite {
  private type Vertex = RecValue[StrValue,Value[Obj]]
  private type Graph = Rec[StrValue,Value[Obj]]
  val model:Model = Model.from(
    tobj("vertex") -> trec(str("id") -> int,str("outE") -> tobj("edge").q(*),str("inE") -> tobj("edge").q(*)),
    tobj("edge") -> trec(str("outV") -> tobj("vertex"),str("label") -> str,str("inV") -> tobj("vertex")),
    tobj("graph") -> tobj("vertex").q(*)
  )

  private def makeEdge(outV:RecValue[StrValue,Value[Obj]],label:String,inV:RecValue[StrValue,Value[Obj]]) ={
    vrec(str("outV") -> outV,str("label") -> str(label),str("inV") -> inV)
  }

  test("model types"){
    val marko :Vertex = vrec(str("id") -> int(1))
    val vadas :Vertex = vrec(str("id") -> int(2))
    val lop   :Vertex = vrec(str("id") -> int(3))
    val josh  :Vertex = vrec(str("id") -> int(4))
    val ripple:Vertex = vrec(str("id") -> int(5))
    val peter :Vertex = vrec(str("id") -> int(6))
    val graph :Graph  = vrec(marko,vadas,lop,josh,ripple,peter)
    //
    assert(graph.test(model.get("graph").get))
    graph.toList.foreach(v => assert(v.test(model.get("vertex").get)))
    graph.toList.foreach(v => assert(!v.test(model.get("edge").get)))
  }

  test("connected values"){
    var marko :Vertex = vrec(str("id") -> int(1))
    val vadas :Vertex = vrec(str("id") -> int(2))
    val lop   :Vertex = vrec(str("id") -> int(3))
    val josh  :Vertex = vrec(str("id") -> int(4))
    val ripple:Vertex = vrec(str("id") -> int(5))
    val peter :Vertex = vrec(str("id") -> int(6))
    marko = marko.put(str("outE"),vrec(makeEdge(marko,"knows",vadas),makeEdge(marko,"created",lop),makeEdge(marko,"knows",josh)))

    val graph:Graph = vrec(marko,vadas,lop,josh,ripple,peter)
    println(marko)
    //
    assert(graph.test(model.get("graph").get))
    graph.toList.foreach(v => assert(v.test(model.get("vertex").get)))
    graph.toList.foreach(v => assert(!v.test(model.get("edge").get)))
  }

}