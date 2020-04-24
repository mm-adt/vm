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

import org.mmadt.language.obj.op.map.ZeroOp.ZeroInst
import org.mmadt.language.obj.op.map.{AppendOp, GetOp, HeadOp, PlusOp, TailOp}
import org.mmadt.language.obj.value.{IntValue, LstValue, RecValue}
import org.mmadt.language.obj.{Int, Lst, Obj}
import org.mmadt.storage.StorageFactory._

trait LstType[A <: Obj] extends Lst[A]
  with Type[Lst[A]]
  with ObjType {

  val value: List[A]

  override def head(): A with Type[A] =  (if (this.value.isEmpty) obj.asInstanceOf[A] else asType(this.value.head)).via(this, HeadOp[A]()).asInstanceOf[A with Type[A]]
  override def tail(): this.type = if (this.value.isEmpty) this.clone(via = (this, TailOp())) else this.clone(value = this.value.tail, via = (this, TailOp()))
  override def append(element: A): this.type = this.clone(value = this.value :+ element, via = (this, AppendOp[A](element)))
  override def get(key: Int): A = {
    val valueType: A = key match {
      case avalue: IntValue if this.value.length > avalue.value => asType[A](this.value(avalue.value.toInt))
      case avalue: IntValue if this.value.nonEmpty =>
        Lst.checkIndex(this, avalue.value.toInt)
        this.value(avalue.value.toInt)
      case _ => obj.asInstanceOf[A]
    }
    valueType.via(this, GetOp[Int, A](key, valueType))
  }
  override def get[BB <: Obj](key: Int, btype: BB): BB = btype.via(this, GetOp[Int, BB](key, btype))
  override def plus(other: LstType[A]): LstType[A] = this.clone(value = this.value ++ other.value).via(this, PlusOp(other))
  override def plus(other: LstValue[_]): this.type = this.clone(value = this.value ++ other.value).via(this, PlusOp(other))
  override def zero(): this.type = this.via(this, new ZeroInst())

  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.value.toString().hashCode() ^ this.lineage.hashCode() ^ this.q.hashCode()
  override def equals(other: Any): Boolean = other match {
    case atype: LstType[A] => this.name == atype.name && this.q == atype.q && this.value == atype.value && this.via == atype.via
    case _ => false
  }
}
