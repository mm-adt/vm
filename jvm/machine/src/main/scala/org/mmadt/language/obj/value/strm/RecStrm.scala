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

import org.mmadt.language.obj.`type`.{BoolType, RecType, Type}
import org.mmadt.language.obj.op.sideeffect.AddOp
import org.mmadt.language.obj.value.{BoolValue, RecValue, StrValue, Value}
import org.mmadt.language.obj.{Obj, Rec}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait RecStrm[A <: Value[Obj],B <: Value[Obj]] extends Strm[Rec[A,B]] with Rec[A,B] with AddOp {
  override val value:Iterator[RecValue[A,B]]
  override def start():RecType[A,B]

  override def add[O<:Obj](obj:O):O = throw new UnsupportedOperationException
  override def to(label:StrValue):RecType[A,B] = throw new UnsupportedOperationException
  override def eqs(other:Type[Rec[A,B]]):BoolType = throw new UnsupportedOperationException
  override def eqs(other:Value[Rec[A,B]]):BoolValue = throw new UnsupportedOperationException
  override def plus(other:Type[Rec[A,B]]):RecType[A,B] = throw new UnsupportedOperationException
  override def plus(other:Value[Rec[A,B]]):this.type = throw new UnsupportedOperationException
  override def is(bool:BoolType):RecType[A,B] = throw new UnsupportedOperationException
  override def is(bool:BoolValue):this.type = throw new UnsupportedOperationException
  override def get(key:A):B = throw new UnsupportedOperationException
  override def get[BB <: Obj](key:A,btype:BB):BB = throw new UnsupportedOperationException
  override def put(key:A,value:B):RecValue[A,B] = throw new UnsupportedOperationException

}

