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

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.op.initial.{IntOp, StrOp}
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.value.{IntValue, ObjValue}
import org.mmadt.language.obj.{DomainInst, Inst, IntQ, OType, Obj, _}
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class __(val _quantifier:IntQ = qStar,_insts:List[(Type[Obj],Inst[Obj,Obj])] = Nil) extends Type[__]
  with IntOp // TODO: persue this path?
  with StrOp
  with PlusOp[__,ObjValue]
  with MultOp[__,ObjValue]
  with AndOp
  with OrOp
  with GetOp[Obj,Obj]
  with PutOp[Obj,Obj]
  with NegOp
  with GtOp[__,ObjValue]
  with GteOp[__,ObjValue]
  with LtOp[__,ObjValue]
  with LteOp[__,ObjValue]
  with ZeroOp
  with OneOp {
  override      val name :String                          = Tokens.obj
  lazy override val insts:List[(Type[Obj],Inst[Obj,Obj])] = this._insts
  override      val via  :DomainInst[__]                  = (if (_insts.isEmpty) base() else _insts.last).asInstanceOf[DomainInst[__]]
  override      val q    :(IntValue,IntValue)             = this._quantifier
  override def q(quantifier:IntQ):this.type = if (this.isCanonical) this.hardQ(quantifier) else new __(quantifier,(this._insts.head._1,this._insts.head._2.q(quantifier)) :: this._insts.tail).asInstanceOf[this.type]
  override def hardQ(quantifier:IntQ):this.type = new __(quantifier,this._insts).asInstanceOf[this.type]

  override def domain[D <: Obj]():Type[D] = obj.q(qStar).asInstanceOf[Type[D]]

  def apply[T <: Obj](obj:Obj):OType[T] = _insts.foldLeft[Traverser[Obj]](Traverser.standard(asType(obj)))((a,i) => i._2(a)).obj().asInstanceOf[OType[T]]

  // type-agnostic monoid supporting all instructions
  override def plus(other:__):this.type = this.compose(PlusOp(other))
  override def plus(other:ObjValue):this.type = this.compose(PlusOp(other))
  override def mult(other:__):this.type = this.compose(MultOp(other))
  override def mult(other:ObjValue):this.type = this.compose(MultOp(other))
  def is(other:Obj):BoolType = this.compose(bool,IsOp(other))
  override def neg():this.type = this.compose(NegOp())
  override def get(key:Obj):this.type = this.compose(GetOp(key))
  override def get[BB <: Obj](key:Obj,btype:BB):BB = this.compose(btype, GetOp(key,btype))
  override def put(key:Obj,value:Obj):this.type = this.compose(PutOp(key,value))
  override def gt(other:ObjValue):BoolType = this.compose(bool,GtOp(other))
  override def lt(other:ObjValue):BoolType = this.compose(bool,LtOp(other))
  override def lte(other:ObjValue):BoolType = this.compose(bool,LteOp(other))
  override def gte(other:ObjValue):BoolType = this.compose(bool,GteOp(other))
  override def zero():this.type = this.compose(ZeroOp())
  override def one():this.type = this.compose(OneOp())
}

object __ extends __(qStar,Nil) {
  def apply(insts:List[Inst[_,_]]):__ = new __(qStar,insts.map(i => (__,i.asInstanceOf[Inst[Obj,Obj]])))
}

