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

package org.mmadt.language.obj.`type`

import org.mmadt.language.LanguageException
import org.mmadt.language.obj.op.map.{AppendOp, HeadOp, TailOp}
import org.mmadt.language.obj.{Lst, Obj}
import org.mmadt.storage.StorageFactory._

trait LstType[A <: Obj] extends Lst[A]
  with Type[Lst[A]]
  with ObjType {

  def value(): List[A]

  override def head(): A = if (this.value().isEmpty) throw new LanguageException("no head on empty list") else asType(this.value().head).via(this, HeadOp[A]())
  override def tail(): this.type = if (this.value().isEmpty) throw new LanguageException("no tail on empty list") else this.clone(value = this.value().tail, via = (this, TailOp[A]()))
  override def append(element: A): this.type = this.clone(value = this.value() :+ element, via = (this, AppendOp[A](element)))

  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.value().toString().hashCode() ^ this.lineage.hashCode() ^ this.q.hashCode()
  override def equals(other: Any): Boolean = other match {
    case atype: LstType[A] => this.name == atype.name && this.q == atype.q && this.value() == atype.value() && this.via == atype.via
    case _ => false
  }
}
