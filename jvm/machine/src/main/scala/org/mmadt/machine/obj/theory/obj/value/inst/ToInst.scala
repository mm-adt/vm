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

package org.mmadt.machine.obj.theory.obj.value.inst

import org.mmadt.machine.obj.theory.obj.Inst
import org.mmadt.machine.obj.theory.obj.`type`.Type
import org.mmadt.machine.obj.theory.obj.util.VorT
import org.mmadt.machine.obj.theory.obj.value.{StrValue, Value}
import org.mmadt.machine.obj.theory.operator.`type`.TypeTo
import org.mmadt.machine.obj.theory.operator.value.ValueTo
import org.mmadt.machine.obj.theory.traverser.Traverser

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ToInst[V <: Value[V], T <: Type[T]] extends Inst {

  type LV = ValueTo[V, T] with V
  type RT = TypeTo[T] with T
  type LEFT = Left[LV, RT]
  type RIGHT = Right[LV, RT]

  override def apply(traverser: Traverser): Traverser = {
    VorT.wrap[LV, RT](traverser.obj()) match {
      case v: LEFT => traverser.to(arg[StrValue](), v.value)
      case t: RIGHT => traverser.to(arg[StrValue](), t.value)
    }
  }
}