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
import org.mmadt.language.obj.value.IntValue
import org.mmadt.storage.StorageFactory._


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VInt(name:String,java:Long,quantifier:IntQ) extends AbstractVObj(name,java,quantifier) with IntValue {
  def this(java:Long) = this(Tokens.int,java,VInt.q1)
  override def clone(_name:String = this.name,_value:Any = this.value,_quantifier:IntQ = this.q):this.type = new VInt(_name,_value.asInstanceOf[Long],_quantifier).asInstanceOf[this.type]
  override val value:Long = java
}

object VInt {
  val q1:(IntValue,IntValue) = (int(1),int(1)) // prevent stackoverflow on object construction with int quantifiers
}
