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

package org.mmadt.machine.obj.theory.obj

import org.mmadt.machine.obj.TQ
import org.mmadt.machine.obj.theory.obj.value.{BoolValue, IntValue}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Obj {

  def value[J, V](java: J): V = java match {
    case j: Long => int(j).asInstanceOf[V]
    case j: Boolean => bool(j).asInstanceOf[V]
    case _ => throw new RuntimeException("Unknown Java object: " + java)
  }

  def bool(value: Boolean): BoolValue //
  def int(value: Long): IntValue //

  def inst(op: String): Inst = inst(op, Nil) //
  def inst(op: String, arg1: Obj): Inst = inst(op, List(arg1)) //
  def inst(op: String, arg1: Obj, arg2: Obj): Inst = inst(op, List(arg1, arg2)) //
  def inst(op: String, arg1: Obj, arg2: Obj, arg3: Obj): Inst = inst(op, List(arg1, arg2, arg3)) //
  def inst(op: String, args: List[Obj]): Inst //

  def q(): TQ

}
