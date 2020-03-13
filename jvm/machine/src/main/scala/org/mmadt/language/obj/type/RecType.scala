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
import org.mmadt.language.obj.op.map.{EqsOp, GetOp, PlusOp}
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.op.traverser.ToOp
import org.mmadt.language.obj.value.{BoolValue, RecValue, StrValue, Value}
import org.mmadt.language.obj.{Obj, Rec, minZero}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VRec

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait RecType[A <: Obj,B <: Obj] extends Rec[A,B]
  with Type[Rec[A,B]]
  with ObjType {

  def apply(value:(Value[A],Value[B])*):RecValue[Value[A],Value[B]] = new VRec[Value[A],Value[B]](this.name,value.toMap,this.q)
  def apply(value:RecValue[Value[A],Value[B]]):RecValue[Value[A],Value[B]] = new VRec[Value[A],Value[B]](this.name,value.value(),this.q)
  def value():Map[A,B]

  override def eqs(other:Type[Rec[A,B]]):BoolType = this.compose(bool,EqsOp(other))
  override def eqs(other:Value[Rec[A,B]]):BoolType = this.compose(bool,EqsOp(other))
  override def to(label:StrValue):this.type = this.compose(ToOp(label))
  override def get[BB <: Obj](key:A,btype:BB):BB = this.compose(btype,GetOp(key))
  override def get(key:A):B = this.compose(this.value()(key),GetOp[A,B](key))
  override def put(key:A,value:B):RecType[A,B] = this.compose(trec(this.name,this.value() + (key -> value),this.q,this.insts),PutOp(key,value))
  override def plus(other:Type[Rec[A,B]]):RecType[A,B] = this.compose(trec(name,this.value() ++ other.asInstanceOf[RecType[A,B]].value(),this.q,this.insts),PlusOp(other))
  override def plus(other:Value[Rec[A,B]]):this.type = this.compose(trec(name,this.value() ++ other.asInstanceOf[RecValue[_,_]].value().asInstanceOf[Map[A,B]],this.q,this.insts),PlusOp(other)).asInstanceOf[this.type]
  override def is(bool:BoolType):RecType[A,B] = this.compose(IsOp(bool)).q(minZero(this.q))
  override def is(bool:BoolValue):this.type = this.compose(IsOp(bool)).q(minZero(this.q))

  override def hashCode():scala.Int = this.name.hashCode ^ this.value().toString().hashCode() ^ this.insts.hashCode() ^ this.q.hashCode()
  override def equals(other:Any):Boolean = other match {
    case atype:RecType[A,B] => this.name == atype.name && this.q == atype.q && this.value() == atype.value() && this.insts.map(_._2) == atype.insts.map(_._2)
    case _ => false
  }
}
