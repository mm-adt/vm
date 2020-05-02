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
import org.mmadt.language.obj.value.{LstValue, Value}
import org.mmadt.language.obj.{IntQ, Obj, ViaTuple, base}
import org.mmadt.storage.StorageFactory.qOne

class VLst[A <: Value[Obj]](val name: String = Tokens.lst, val ground: List[A] = List.empty[A], val q: IntQ = qOne, val via: ViaTuple = base()) extends LstValue[A] {
  def this(list: List[A]) = {
    this(name = Tokens.lst, ground = list, q = qOne, via = base())
  }
  override def clone(name: String = this.name,
                     ground: Any = this.ground,
                     q: IntQ = this.q,
                     via: ViaTuple = this.via): this.type = new VLst[A](name, ground.asInstanceOf[List[A]], q, via).asInstanceOf[this.type]
}