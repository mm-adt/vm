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
import org.mmadt.language.obj.`type`.ObjType
import org.mmadt.language.obj.op.initial.StartOp
import org.mmadt.language.obj.value.ObjValue
import org.mmadt.language.obj.{IntQ, Obj}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.`type`.{TInt, TObj}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VObj(name:String,java:Any,quantifier:IntQ) extends AbstractVObj(name,java,quantifier) with ObjValue {

  def this(java:Any) = this(Tokens.obj,java,qOne)

  override def value():Any = java
  def value(java:Any):this.type = new VObj(this.name,java,quantifier).asInstanceOf[this.type]
  override def start():ObjType = new TObj(name,List((new TInt(name,Nil,qZero),StartOp(this))),quantifier)
  override def q(quantifier:IntQ):this.type = new VObj(name,java,quantifier).asInstanceOf[this.type]
  override def as[O <: Obj](name:String):O = new VObj(name,java,quantifier).asInstanceOf[O]

}