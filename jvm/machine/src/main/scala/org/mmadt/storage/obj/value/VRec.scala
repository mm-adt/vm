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
import org.mmadt.language.obj.value.{RecValue, Value}
import org.mmadt.language.obj.{Bool, IntQ, Obj, Rec, ViaTuple, base}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VRec[A <: Value[Obj], B <: Value[Obj]](name: String, java: Map[A, B], quantifier: IntQ, via: ViaTuple = base()) extends AbstractVObj(name, java, quantifier, via) with RecValue[A, B] {
  def this(java: Map[A, B]) = this(Tokens.rec, java, qOne)
  override def clone(_name: String = this.name, _value: Any = this.value, _quantifier: IntQ = this.q, _via: ViaTuple= base()): this.type = new VRec[A, B](_name, _value.asInstanceOf[Map[A, B]], _quantifier).asInstanceOf[this.type]
  override val value: Map[A, B] = java
}
