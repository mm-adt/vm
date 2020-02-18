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

package org.mmadt.language.obj.value.strm

import org.mmadt.language.obj.`type`.{BoolType, RecType}
import org.mmadt.language.obj.value.{BoolValue, RecValue, StrValue, Value}
import org.mmadt.language.obj.{OType, Obj, Rec}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait RecStrm[A <: Obj,B <: Obj] extends Rec[A,B]
  with Value[RecStrm[A,B]] {

  override def value():Iterator[RecValue[A,B]] //
  override def start():RecType[A,B] //

  override def to(label:StrValue):RecType[A,B] = this.start().to(label) //
  //override def eqs(other: RecType[A,B]): BoolType = this.start().eqs(other) //
  //override def eqs(other: RecValue[A,B]): BoolValue = this.value() == other.value() //
  override def plus(other:RecType[A,B]):RecType[A,B] = this.start().plus(other) //
  override def plus(other:RecValue[A,B]):this.type = throw new IllegalAccessException() //
  override def is(bool:BoolType):RecType[A,B] = this.start().is(bool) //
  override def is(bool:BoolValue):this.type = throw new IllegalAccessException() //
  override def get(key:A):B = throw new IllegalAccessException() //
  override def put(key:A,value:B):RecValue[A,B] = throw new IllegalAccessException() //
  override def get[BT <: OType](key:A,btype:BT):BT = throw new IllegalAccessException() //
}
