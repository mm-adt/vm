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
import org.mmadt.language.obj.op.OpInstResolver
import org.mmadt.language.obj.op.map.NegOp
import org.mmadt.language.obj.value.IntValue
import org.mmadt.language.obj.{DomainInst,Inst,IntQ,OType,Obj,_}
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class __(_insts:List[(Type[Obj],Inst[Obj,Obj])] = Nil,val _quantifier:IntQ = qOne) extends Type[__] {
  override      val name :String                          = Tokens.empty
  lazy override val insts:List[(Type[Obj],Inst[Obj,Obj])] = this._insts
  override      val via  :DomainInst[__]                 = (if (_insts.isEmpty) base() else _insts.last).asInstanceOf[DomainInst[__]]
  override      val q    :(IntValue,IntValue)             = this._quantifier
  override def q(quantifier:IntQ):this.type = new __(this._insts,quantifier).asInstanceOf[this.type]


  def apply[T <: Obj](obj:Obj):OType[T] = _insts.foldLeft[Traverser[Obj]](Traverser.standard(asType(obj)))((a,i) => i._2(a)).obj().asInstanceOf[OType[T]]
  // type-agnostic monoid supporting all instructions
  def get(key:Obj):this.type = this.compose(OpInstResolver.resolve(Tokens.get,List(key)))
  def and(other:Obj):this.type = this.compose(OpInstResolver.resolve(Tokens.and,List(other)))
  def plus(other:Obj):this.type = this.compose(OpInstResolver.resolve(Tokens.plus,List(other)))
  def mult(other:Obj):this.type = this.compose(OpInstResolver.resolve(Tokens.mult,List(other)))
  def gt(other:Obj):BoolType = this.compose(bool,OpInstResolver.resolve(Tokens.gt,List(other)))
  def neg():this.type = this.compose(NegOp())
}

object __ extends __(Nil,qOne) {
  def apply(insts:List[Inst[_,_]]):__ = new __(insts.map(i => (__,i.asInstanceOf[Inst[Obj,Obj]])))
}

