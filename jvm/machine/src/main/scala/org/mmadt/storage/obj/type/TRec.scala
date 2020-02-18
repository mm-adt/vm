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

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.RecType
import org.mmadt.language.obj.op.PlusOp
import org.mmadt.language.obj.value.RecValue
import org.mmadt.language.obj.{Inst,OType,Obj,TQ}
import org.mmadt.storage.obj._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TRec[A <: Obj,B <: Obj](name:String,java:Map[A,B],insts:List[(OType,Inst)],quantifier:TQ) extends TObj[RecType[A,B]](name,insts,quantifier) with RecType[A,B] {
  def this() = this(Tokens.rec,Map[A,B](),Nil,qOne) //
  override def compose(inst:Inst):this.type = rec[A,B](this,inst,quantifier).asInstanceOf[this.type] //
  override def range():this.type = new TRec[A,B](name,java,Nil,quantifier).asInstanceOf[this.type] //
  override def q(quantifier:TQ):this.type = new TRec[A,B](name,java,insts,quantifier).asInstanceOf[this.type] //
  override def value():Map[A,B] = java

  override def plus(other:RecType[A,B]):RecType[A,B] ={
    new TRec[A,B](name,other.value() ++ this.value(),insts,quantifier).compose(PlusOp(other))
  } //
  override def plus(other:RecValue[A,B]):this.type ={
    new TRec[A,B](name,other.value() ++ this.value(),insts,quantifier).compose(PlusOp(other)).asInstanceOf[this.type]
  } //
}