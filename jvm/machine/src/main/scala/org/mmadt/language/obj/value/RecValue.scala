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

import org.mmadt.language.obj.`type`.{BoolType, RecType, Type}
import org.mmadt.language.obj.op.initial.StartOp
import org.mmadt.language.obj.{Obj, Rec}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait RecValue[A <: Value[Obj],B <: Value[Obj]] extends Rec[A,B]
  with Value[Rec[A,B]]
  with StartOp[RecType[A,B]] {

  override def value():Map[A,B]
  override def start():RecType[A,B]
  def value(java:Map[A,B]):this.type

  override def to(label:StrValue):RecType[A,B] = this.start().to(label)
  override def eqs(other:Type[Rec[A,B]]):BoolType = this.start().eqs(other)
  override def eqs(other:Value[Rec[A,B]]):BoolValue = bool(this.value().equals(other.value()))
  override def plus(other:Type[Rec[A,B]]):RecType[A,B] = this.start().plus(other)
  override def plus(other:Value[Rec[A,B]]):this.type = this.value(this.value() ++ other.asInstanceOf[RecValue[A,B]].value())
  override def is(bool:BoolType):RecType[A,B] = this.start().is(bool)
  override def is(bool:BoolValue):this.type = if (bool.value()) this else this.q(qZero)
  override def get(key:A):B = this.value()(key)
  override def get[BB <: Obj](key:A,btype:BB):BB = this.value()(key).asInstanceOf[BB]
  override def put(key:A,value:B):RecValue[A,B] = this.value(this.value + (key -> value))
}
