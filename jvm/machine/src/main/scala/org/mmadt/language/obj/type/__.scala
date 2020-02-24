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

import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.op.ChooseOp
import org.mmadt.language.obj.value.{IntValue, RecValue}
import org.mmadt.language.obj.{Inst, O, OType, Obj}
import org.mmadt.language.{Stringer, Tokens}
import org.mmadt.storage.obj._
import org.mmadt.storage.obj.value.VRec

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class __(insts:List[Inst] = Nil) extends Type[__] {

  override def toString:String = insts.foldLeft("")((a,i) => a + Stringer.instString(i))
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

  def apply[T <: Type[T]](obj:Obj):T = insts.foldLeft(asType(obj).asInstanceOf[Obj])((a,i) => i match {
    case x:Inst if x.op() == Tokens.choose => applyChoose(a.asInstanceOf[OType],x.arg())(a)
    case _ => i(a)
  }).asInstanceOf[T]

  private def applyChoose(a:OType,branches:RecValue[OType,O]):Inst ={ // [choose] branches need to be resolved (thus, a new rec is constructed)
    ChooseOp(new VRec[OType,O](branches.value().map(entry => (entry._1 match {
      case y:__ => y(a.range())
      case y => y
    },entry._2 match {
      case y:__ => y(a.range())
      case y => y
    }))))
  }
}

object __ extends __(Nil) {
  def apply(insts:Inst*):__ = new __(insts.toList)
}

