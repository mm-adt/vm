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
import org.mmadt.language.obj.{ViaTuple, IntQ, Obj, base}
import org.mmadt.language.obj.value.ObjValue
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VObj(name:String,val _value:Any,quantifier:IntQ,via:ViaTuple) extends AbstractVObj(name,quantifier,via) with ObjValue {
  def this(java:Any) = this(Tokens.obj,java,qOne,base())
  override val value:Any = _value
  override def clone(_name:String = this.name,_value:Any = this.value,_quantifier:IntQ = this.q,_via:ViaTuple=base()):this.type = new VObj(_name,_value,_quantifier).asInstanceOf[this.type]
}