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
import org.mmadt.language.obj.{IntQ, Obj, Real, ViaTuple, base}
import org.mmadt.language.obj.value.RealValue
import org.mmadt.storage.StorageFactory._

/**
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VReal(name:String,java:Double,quantifier:IntQ, via:ViaTuple=base()) extends AbstractVObj(name,java,quantifier,via) with RealValue {
  def this(java:Double) = this(Tokens.real,java,qOne)
  override  def clone(_name:String = this.name,_value:Any = this.value,_quantifier:IntQ = this.q,_via:ViaTuple=base()):this.type = new VReal(_name,_value.asInstanceOf[Double],_quantifier).asInstanceOf[this.type]
  override val value:Double = java
}
