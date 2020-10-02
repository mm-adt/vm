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

import org.apache.tinkerpop.gremlin.process.traversal.Traversal
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.mmadt.language.obj.Obj

import scala.collection.JavaConverters

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object ObjGraphUtil {
  val OBJ:String = "obj"
  val TYPE:String = "type"
  val VALUE:String = "value"
  val ROOT:String = "root"
  val RANGE:String = "range"

  @inline implicit class ObjVertex(val vertex:Vertex) {
    def obj:Obj = vertex.property[Obj](OBJ).value()
    def iso:Obj = vertex.property[Obj]("range").value()
  }

  /*@inline implicit class ObjTraversal(val traversal:Traversal[_,Object]) {
    def toSeq:Seq[Object] = JavaConverters.asScalaIterator(traversal.toList.iterator()).toSeq
  }*/
}
