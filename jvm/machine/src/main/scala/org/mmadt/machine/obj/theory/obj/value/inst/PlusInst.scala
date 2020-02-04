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
import org.mmadt.machine.obj.theory.obj.value.Value
import org.mmadt.machine.obj.theory.operator.`type`.TypePlus
import org.mmadt.machine.obj.theory.operator.value.ValuePlus
import org.mmadt.machine.obj.theory.traverser.Traverser

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait PlusInst[V <: Value[V], T <: Type[T]] extends Inst {

  type LV = ValuePlus[_, V, T] with V
  type RT = TypePlus[_, V, T] with T
  type LEFT = Left[LV, RT]
  type RIGHT = Right[LV, RT]
  private lazy val wrappedArg = VorT.wrap[LV, RT](arg())

  override def apply(traverser: Traverser): Traverser = {
    VorT.wrap[LV, RT](traverser.obj()) match {
      case v: LEFT => wrappedArg match {
        case argV: LEFT => traverser.split[V](v.value.plus(argV.value))
        case argT: RIGHT => traverser.split[V](v.value.plus(traverser.split(v.value).apply(argT.value).obj[V]()))
      }
      case t: RIGHT => wrappedArg match {
        case argV: LEFT => traverser.split[T](t.value.plus(argV.value))
        case argT: RIGHT => traverser.split[T](t.value.plus(traverser.split(t.value).apply(argT.value).obj[T]()))
      }
    }
  }
}
