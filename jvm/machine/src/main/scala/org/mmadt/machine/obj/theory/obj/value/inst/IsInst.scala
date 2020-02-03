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

import org.mmadt.machine.obj.theory.obj.`type`.{BoolType, Type}
import org.mmadt.machine.obj.theory.obj.value.{BoolValue, Value}
import org.mmadt.machine.obj.theory.obj.{Bool, Inst, Obj}
import org.mmadt.machine.obj.theory.operator.`type`.TypeIs
import org.mmadt.machine.obj.theory.operator.value.ValueIs
import org.mmadt.machine.obj.theory.traverser.Traverser

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait IsInst[V <: Value[V], T <: Type[T]] extends Inst {
  override def apply(traverser: Traverser): Traverser = {
    traverser.obj[Obj]() match {
      case v: ValueIs[V, T] => arg[Bool]() match {
        case argV: BoolValue => traverser.split[V](v.is(argV))
        case argT: BoolType => traverser.split[V](v.is(traverser.split(v).apply(argT).obj().asInstanceOf[BoolValue]))
      }
      case t: TypeIs[T] => arg[Bool]() match {
        case argV: BoolValue => traverser.split[T](t.is(argV))
        case argT: BoolType => traverser.split[T](t.is(traverser.split(t).apply(argT).obj().asInstanceOf[BoolType]))
      }
    }
  }
}
