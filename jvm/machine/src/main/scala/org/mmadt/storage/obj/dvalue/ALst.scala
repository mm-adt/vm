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

package org.mmadt.storage.obj.dvalue

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.Type
import org.mmadt.storage.StorageFactory._

class ALst[A <: Obj](val name: String = Tokens.lst, val value: (Lst[A], A) = null, val q: IntQ = qOne, val via: ViaTuple = base())
  extends Lst[A]
    with Type[Lst[A]] {

  override def clone(name: String = this.name,
    value: Any = this.value,
    q: IntQ = this.q,
    via: ViaTuple = this.via): this.type = new ALst[A](name, value.asInstanceOf[(Lst[A], A)], q, via).asInstanceOf[this.type]

  override def test(other: Obj): Boolean = other match {
    case alst: Lst[_] => Lst.decode(alst).equals(Lst.decode(this)) && this.lineage.equals(alst.lineage)
    case _ => false
  }
}