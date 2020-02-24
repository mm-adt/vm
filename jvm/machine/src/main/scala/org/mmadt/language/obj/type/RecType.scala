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

import org.mmadt.language.obj.op._
import org.mmadt.language.obj.value.{BoolValue,RecValue,StrValue}
import org.mmadt.language.obj.{OType,Obj,Rec}
import org.mmadt.storage.obj.`type`.TRec

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait RecType[A <: Obj,B <: Obj] extends Rec[A,B]
  with Type[RecType[A,B]] {

  def value():Map[A,B] //
  def apply(values:(A,B)*):RecType[A,B] = new TRec[A,B](this.name,values.toMap,this.insts(),this.q())

  //override def eqs(other: RecType[A, B]): BoolType = this.bool(EqOp(other))
  //override def eqs(other: RecValue[A, B]): BoolType = this.bool(EqOp(other))
  override def to(label:StrValue):this.type = this.compose(ToOp(label))
  override def get[BT <: OType](key:A,btype:BT):BT = this.compose(btype,GetOp(key))
  override def get(key:A):B = this.compose(this.value()(key),GetOp(key))
  override def put(key:A,value:B):RecType[A,B] = new TRec[A,B](this.name,this.value() + (key -> value),this.insts() :+ (this,PutOp(key,value)),this.q())
  override def plus(other:RecType[A,B]):RecType[A,B]
  override def plus(other:RecValue[A,B]):this.type
  override def is(bool:BoolType):RecType[A,B] = this.compose(IsOp(bool)).q(0,q()._2)
  override def is(bool:BoolValue):this.type = this.compose(IsOp(bool)).q(0,q()._2)
}