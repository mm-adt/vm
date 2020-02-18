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

import org.mmadt.language.obj.`type`.{BoolType,RecType,Type}
import org.mmadt.language.obj.{OType,Obj,Rec}
import org.mmadt.processor.obj.value.IteratorProcessor
import org.mmadt.storage.obj._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait RecValue[A <: Obj,B <: Obj] extends Rec[A,B]
  with Value[RecValue[A,B]] {

  override def value():Map[A,B] //
  override def start():RecType[A,B] //

  override def to(label:StrValue):RecType[A,B] = this.start().to(label)
  //override def eqs(other: RecType[A,B]): BoolType = this.start().eqs(other) //
  //override def eqs(other: RecValue[A,B]): BoolValue = this.value() == other.value() //
  override def plus(other:RecType[A,B]):RecType[A,B] = this.start().plus(other) //
  override def plus(other:RecValue[A,B]):this.type = rec(name,other.value() ++ this.value(),this.q()).asInstanceOf[this.type] //
  override def is(bool:BoolType):RecType[A,B] = this.start().is(bool) //
  override def is(bool:BoolValue):this.type = if (bool.value()) this else this.q(0) //


  override def get(key:A):B = this.value().get(key) match {
    case Some(bvalue:Value[_] with B) => bvalue
    case Some(btype:Type[_] with B) => IteratorProcessor(key,btype).next().obj()
    case None => throw new NoSuchElementException("The rec does not have a value for the key: " + key)
    case _ => throw new RuntimeException()
  }

  override def put(key:A,value:B):RecValue[A,B] = rec(this.name,Map(key -> value) ++ this.value(),this.q())

  override def get[BT <: OType](key:A,btype:BT):BT = this.get(key).asInstanceOf[BT]
}
