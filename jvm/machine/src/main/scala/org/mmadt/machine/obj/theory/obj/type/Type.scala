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

package org.mmadt.machine.obj.theory.obj.`type`

import org.mmadt.language.{Stringer, Tokens}
import org.mmadt.machine.obj.impl.value.VInst
import org.mmadt.machine.obj.theory.obj.Obj
import org.mmadt.machine.obj.theory.obj.value.BoolValue
import org.mmadt.machine.obj.{Inst, TQ}

/**
  * @author Marko A. Rodriguez (http://markorodriguez.com)
  */
trait Type[T <: Type[T]] extends Obj {

  def _jvm(): List[Inst]

  def copy(inst: List[Inst], q: TQ): T

  def is(bool: BoolValue): T = if (bool._jvm()) this.asInstanceOf[T] else this.asInstanceOf[T] //.q(int(0), int(0))

  def is(bool: BoolType): T = copy(this._jvm() ++ List(new VInst((Tokens.is, List(bool)))), q())

  override def toString: String = Stringer.typeString(this)

  // def domain(): A  (and Type[A]) where A is the domain and the Type itself is the range.
  // def ++(inst: Inst): A = copy(this._jvm() ++ List(inst), this.q())


}
