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

package org.mmadt.storage.obj.value

import org.mmadt.language.Tokens
import org.mmadt.language.obj.value.{RecValue, Value}
import org.mmadt.language.obj.{IntQ, Obj, ViaTuple, base}
import org.mmadt.storage.StorageFactory._

import scala.collection.mutable

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VRec[A <: Value[Obj], B <: Value[Obj]](val name: String = Tokens.rec, val ground: collection.Map[A, B], val q: IntQ = qOne, val via: ViaTuple = base()) extends RecValue[A, B] {
  def this(seq: Seq[(A, B)]) = {
    this(name = Tokens.rec, ground = seq.foldLeft(new mutable.LinkedHashMap[A, B]())((b, a) => {
      b.put(a._1, a._2)
      b
    }), q = qOne, via = base())
  }
  override def clone(name: String = this.name,
                     ground: Any = this.ground,
                     q: IntQ = this.q,
                     via: ViaTuple = base()): this.type = new VRec[A, B](name, ground.asInstanceOf[collection.Map[A, B]], q, via).asInstanceOf[this.type]
}
