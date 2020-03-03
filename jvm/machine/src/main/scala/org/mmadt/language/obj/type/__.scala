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
import org.mmadt.language.obj.op.map.{IdOp, MultOp, PlusOp}
import org.mmadt.language.obj.value.{IntValue, Value}
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.`type`.TObj
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class __(insts:List[(Type[Obj],Inst)] = Nil) extends Type[__] with Obj
  with PlusOp[Obj]
  with MultOp[Obj] {

  def this(insts:Inst*) = this(insts.map(i => (new TObj(),i)).toList)

  override def toString:String = insts.foldLeft(Tokens.empty)((a,i) => a + i)
  override def q():(IntValue,IntValue) = qOne
  override def q(quantifier:(IntValue,IntValue)):__.this.type = throw new IllegalArgumentException()
  override val name:String = Tokens.__
  override def test(other:Obj):Boolean = throw new IllegalArgumentException()
  override def id():this.type = this.compose(IdOp())
  override def as[O <: Obj](name:String):O = throw new IllegalArgumentException()
  override def range():__.this.type = this
  override def insts():List[(Type[Obj],Inst)] = insts
  override def compose(inst:Inst):this.type = new __(insts :+ (new TObj(),inst)).asInstanceOf[this.type]
  override def int(inst:Inst,q:(IntValue,IntValue)):IntType = throw new IllegalArgumentException()
  override def bool(inst:Inst,q:(IntValue,IntValue)):BoolType = throw new IllegalArgumentException()
  override def str(inst:Inst,q:(IntValue,IntValue)):StrType = throw new IllegalArgumentException()
  override def rec[A <: Obj,B <: Obj](atype:RecType[A,B],inst:Inst,q:(IntValue,IntValue)):RecType[A,B] = throw new IllegalArgumentException()
  override def obj(inst:Inst,q:(IntValue,IntValue)):ObjType = throw new IllegalArgumentException()
  override def count():IntType = throw new IllegalArgumentException()

  def apply[T <: Type[T]](obj:Obj):T = insts.foldLeft(asType(obj).asInstanceOf[Obj])((a,i) => i._2(a)).asInstanceOf[T]

  override def plus(other:Type[Obj]):__ = this.compose(new VInst((Tokens.plus,List(other)),qOne,null))
  override def plus(other:Value[Obj]):this.type = this.compose(new VInst((Tokens.plus,List(other)),qOne,null))
  override def mult(other:Type[Obj]):__ = this.compose(new VInst((Tokens.mult,List(other)),qOne,null))
  override def mult(other:Value[Obj]):this.type = this.compose(new VInst((Tokens.mult,List(other)),qOne,null))
}

object __ extends __(Nil) {
  def apply(insts:Inst*):__ = new __(insts.map(i => (new TObj(),i)).toList)
}

