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
import org.mmadt.language.obj._
import org.mmadt.language.obj.value.IntValue
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
abstract class VInst[S <: Obj, E <: Obj](val name: String = Tokens.inst, val value: InstTuple, val q: IntQ = qOne, val via: ViaTuple = base()) extends Inst[S, E] {
  def this(value: InstTuple, q: IntQ) = this(Tokens.inst, value, q, base())
  def this(value: InstTuple) = this(Tokens.inst, value, qOne, base())
  def test(other: Obj): Boolean = false //  TODO: GUT WHEN VINST JOINS HEIRARCHY
  override def clone(_name: String, _value: Any, _quantifier: (IntValue, IntValue), _via: ViaTuple): this.type = this
  override def equals(other: Any): Boolean = other match {
    case inst: Inst[_, _] => inst.value == this.value && eqQ(this, inst)
    case _ => false
  }
}
