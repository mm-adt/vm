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

import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.value.IntValue
import org.mmadt.language.obj.{Int, Lst, Obj}
import org.mmadt.storage.StorageFactory._

trait LstType[A <: Obj] extends Lst[A]
  with Type[Lst[A]]
  with ObjType {

  val ground: List[A]

  override def get(key: Int): A = {
    val valueType: A = key match {
      case avalue: IntValue if this.ground.length > avalue.ground => asType[A](this.ground(avalue.ground.toInt))
      case avalue: IntValue if this.ground.nonEmpty =>
        Lst.checkIndex(this, avalue.ground.toInt)
        this.ground(avalue.ground.toInt)
      case _ => obj.asInstanceOf[A]
    }
    valueType.via(this, GetOp[Int, A](key, valueType))
  }
  override def get[BB <: Obj](key: Int, btype: BB): BB = btype.via(this, GetOp[Int, BB](key, btype))

  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.ground.toString().hashCode() ^ this.trace.hashCode() ^ this.q.hashCode()
  override def equals(other: Any): Boolean = other match {
    case atype: LstType[A] => this.name == atype.name && this.q == atype.q && this.ground == atype.ground && this.via == atype.via
    case _ => false
  }
}
