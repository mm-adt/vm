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
import org.mmadt.language.obj.IntQ
import org.mmadt.language.obj.`type`.RealType
import org.mmadt.language.obj.op.initial.StartOp
import org.mmadt.language.obj.value.RealValue
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VReal(name:String,java:Double,quantifier:IntQ) extends AbstractVObj(name,java,quantifier) with RealValue {
  def this(java:Double) = this(Tokens.real,java,qOne)
  def this(name:String,java:Double) = this(name,java,qOne)

  override val value:Double = java
  override def value(java:Double):this.type = new VReal(this.name,java,quantifier).asInstanceOf[this.type]
  override def start():RealType = treal(name,quantifier,List((treal(name,qZero,Nil),StartOp(this))))
  override def q(quantifier:IntQ):this.type = new VReal(name,java,quantifier).asInstanceOf[this.type]
}
