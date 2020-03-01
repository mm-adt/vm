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

import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.op.map.{EqsOp,GetOp,PlusOp}
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.op.traverser.ToOp
import org.mmadt.language.obj.value.{BoolValue,RecValue,StrValue,Value}
import org.mmadt.language.obj.{Obj,Rec,minZero}
import org.mmadt.storage.obj.`type`.TRec
import org.mmadt.storage.obj.value.VRec

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait RecType[A <: Obj,B <: Obj] extends Rec[A,B]
  with Type[Rec[A,B]]
  with ObjType {

  def apply(value:(Value[A],Value[B])*):RecValue[Value[A],Value[B]] = new VRec[Value[A],Value[B]](this.name,value.toMap,this.q())
  def apply(value:RecValue[Value[A],Value[B]]):RecValue[Value[A],Value[B]] = new VRec[Value[A],Value[B]](this.name,value.value(),this.q())
  def value():Map[A,B]

  override def eqs(other:Type[Rec[A,B]]):BoolType = this.bool(EqsOp(other))
  override def eqs(other:Value[Rec[A,B]]):BoolType = this.bool(EqsOp(other))
  override def to(label:StrValue):this.type = this.compose(ToOp(label))
  override def get[BB <: Obj](key:A,btype:BB):BB = this.compose(btype,GetOp(key,btype.asInstanceOf[Type[BB]])).asInstanceOf[BB]
  override def get(key:A):B = this.compose(this.value()(key),GetOp[A,B](key)).asInstanceOf[B]
  override def put(key:A,value:B):RecType[A,B] = new TRec[A,B](this.name,this.value() + (key -> value),this.insts() :+ (this,PutOp(key,value)),this.q())
  override def plus(other:Type[Rec[A,B]]):RecType[A,B] =
    new TRec[A,B](name,
      this.value() ++ other.asInstanceOf[RecType[A,B]].value(),
      this.insts :+ (this,PlusOp(other.asInstanceOf[RecType[A,B]])),this.q())
  override def plus(other:Value[Rec[A,B]]):this.type =
    new TRec[A,B](name,
      this.value() ++ other.asInstanceOf[RecValue[_,_]].value().asInstanceOf[Map[A,B]],
      this.insts :+ (this,PlusOp(other.asInstanceOf[RecValue[Value[A],Value[B]]])),this.q()).asInstanceOf[this.type]
  override def is(bool:BoolType):RecType[A,B] = this.compose(IsOp(bool)).q(minZero(this.q()))
  override def is(bool:BoolValue):this.type = this.compose(IsOp(bool)).q(minZero(this.q()))

  /*override def get(key:A):B = this.value().get(key) match {
    case Some(bvalue:Value[_] with B) => bvalue
    case Some(btype:Type[_] with B) => key ==> btype
    case None => throw new NoSuchElementException("The rec does not have a value for the key: " + key)
    case _ => throw new RuntimeException()
  }*/
}
