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

package org.mmadt.storage.obj.`type`

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.RecType
import org.mmadt.storage.StorageFactory._

import scala.collection.mutable

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TRec[A <: Obj, B <: Obj](val name: String = Tokens.rec, val value: collection.Map[A, B] = Map[A, B](), val q: IntQ = qOne, val via: ViaTuple = base[Rec[A, B]]()) extends RecType[A, B] {
  def this(seq: Seq[(A, B)]) = {
    this(name = Tokens.rec, value = seq.foldLeft(new mutable.LinkedHashMap[A, B]())((b, a) => {
      b.put(a._1, a._2)
      b
    }), q = qOne, via = base())
  }
  override def clone(name: String = this.name,
                     value: Any = this.value,
                     q: IntQ = this.q,
                     via: ViaTuple = this.via): this.type = new TRec[A, B](name, value.asInstanceOf[collection.Map[A, B]], q, via).asInstanceOf[this.type]
}