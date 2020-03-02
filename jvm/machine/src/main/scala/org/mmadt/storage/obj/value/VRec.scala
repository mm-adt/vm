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

package org.mmadt.storage.obj.value

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.RecType
import org.mmadt.language.obj.op.initial.StartOp
import org.mmadt.language.obj.value.{RecValue, Value}
import org.mmadt.language.obj.{IntQ, Obj}
import org.mmadt.storage.obj._
import org.mmadt.storage.obj.`type`.TRec

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VRec[A <: Value[Obj],B <: Value[Obj]](name:String,java:Map[A,B],quantifier:IntQ) extends AbstractVObj(name,java,quantifier) with RecValue[A,B] {

  def this(java:Map[A,B]) = this(Tokens.rec,java,qOne)
  def this(name:String,java:Map[A,B]) = this(name,java,qOne)

  override def value():Map[A,B] = java
  override def value(java:Map[A,B]):this.type = new VRec(this.name,java,quantifier).asInstanceOf[this.type]
  override def start():RecType[A,B] = new TRec(name,java,List((new TRec(name,java,Nil,qZero),StartOp(this))),quantifier)
  override def q(quantifier:IntQ):this.type = new VRec(name,java,quantifier).asInstanceOf[this.type]
  override def as[O <: Obj](name:String):O = new VRec(name,java,quantifier).asInstanceOf[O]

}
