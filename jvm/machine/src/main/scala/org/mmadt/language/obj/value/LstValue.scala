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

package org.mmadt.language.obj.value

import org.mmadt.language.obj.`type`.LstType
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.{Int, Lst, Obj}

trait LstValue[A <: Value[Obj]] extends Lst[A]
  with Value[Lst[A]]
  with ObjValue {

  override val value: List[A]
  override def get(key: Int): A = key match {
    case avalue: IntValue =>
      Lst.checkIndex(this, avalue.value.toInt)
      this.value(avalue.value.toInt).via(this, GetOp[Int, A](key))
    case _ => this.start[LstType[A]]().get(key)
  }
  override def get[BB <: Obj](key: Int, btype: BB): BB = key match {
    case avalue: IntValue =>
      Lst.checkIndex(this, avalue.value.toInt)
      this.value(avalue.value.toInt).via(this, GetOp[Int, BB](key, btype)).asInstanceOf[BB]
    case _ => this.start[LstType[A]]().get(key, btype)
  }
}
