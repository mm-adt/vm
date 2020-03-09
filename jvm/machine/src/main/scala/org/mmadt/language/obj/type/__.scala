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
import org.mmadt.language.obj.{Inst, IntQ, Obj, _}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.`type`.TObj

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class __(_insts:List[(Type[Obj],Inst)] = Nil,_quantifier:IntQ = qOne) extends Type[__] with Obj {
  override def toString:String = _insts.foldLeft(Tokens.empty)((a,i) => a + i._2)
  override def q():(IntValue,IntValue) = qOne
  override def q(quantifier:IntQ):this.type = new __(this._insts,multQ(this._quantifier,quantifier)).asInstanceOf[this.type]
  override val name:String = Tokens.__
  override def test(other:Obj):Boolean = throw new IllegalArgumentException()
  override def as[O <: Obj](name:String):O = throw new IllegalArgumentException()
  override val insts:List[(Type[Obj],Inst)] = _insts
  override def count():IntType = throw new IllegalArgumentException()

  def apply[T <: Type[T]](obj:Obj):T = _insts.foldLeft(asType(obj).asInstanceOf[Obj])((a,i) => i._2(a)).asInstanceOf[T]

  def get(key:Obj):this.type = this.compose(OpInstResolver.resolve(Tokens.get,List(key)))
  def and(other:Obj):this.type = this.compose(OpInstResolver.resolve(Tokens.and,List(other)))
  def plus(other:Obj):this.type = this.compose(OpInstResolver.resolve(Tokens.plus,List(other)))
  def mult(other:Obj):this.type = this.compose(OpInstResolver.resolve(Tokens.mult,List(other)))
  def neg():this.type = this.compose(NegOp())


}

object __ extends __(Nil,qOne) {
  def apply(insts:Inst*):__ = new __(insts.map(i => (new TObj(),i)).toList,qOne)
}

