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

package org.mmadt.storage.obj.`type`

import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.value.IntValue
import org.mmadt.language.obj.{Inst, OType, Obj}
import org.mmadt.storage.obj.qOne

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class __(insts:List[Inst] = Nil) extends Type[__] {

  override def toString:String = "[" + insts.toString().replace("List(","").dropRight(1) + "]"
  override def q():(IntValue,IntValue) = qOne
  override def q(quantifier:(IntValue,IntValue)):__.this.type = this
  override val name:String = "xxx"
  override def test(other:Obj):Boolean = false
  override def id():__.this.type = this
  override def as[O <: Obj](name:String):O = throw new IllegalArgumentException()
  override def range():__.this.type = this
  override def insts():List[(OType,Inst)] = Nil
  override def compose(inst:Inst):__.this.type = this
  override def int(inst:Inst,q:(IntValue,IntValue)):IntType = null
  override def bool(inst:Inst,q:(IntValue,IntValue)):BoolType = null
  override def str(inst:Inst,q:(IntValue,IntValue)):StrType = null
  override def rec[A <: Obj,B <: Obj](atype:RecType[A,B],inst:Inst,q:(IntValue,IntValue)):RecType[A,B] = null

  def apply[T <: Type[T]](obj:Obj):T = insts.foldLeft(obj)((a,i) => i(a)).asInstanceOf[T]
}

object __ extends __(Nil) {
  def apply(insts:Inst*):__ = new __(insts.toList)
}

