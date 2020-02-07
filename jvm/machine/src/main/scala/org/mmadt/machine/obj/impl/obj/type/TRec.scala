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

package org.mmadt.machine.obj.impl.obj.`type`

import org.mmadt.machine.obj.TQ
import org.mmadt.machine.obj.impl.obj.qOne
import org.mmadt.machine.obj.theory.obj.`type`.{RecType, Type}
import org.mmadt.machine.obj.theory.obj.{Inst, Obj}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TRec[A <: Obj, B <: Obj](java: Map[A, B], insts: List[(Type[_], Inst)], quantifier: TQ) extends TObj[RecType[A, B]](insts, quantifier) with RecType[A, B] {
  def this() = this(Map[A, B](), Nil, qOne) //
  override def push(inst: Inst): RecType[A, B] = rec[A, B](java, inst, quantifier) //
  override def pop(): RecType[A, B] = new TRec[A, B](java, insts.tail, quantifier) //
  override def q(quantifier: TQ): this.type = new TRec[A, B](java, insts, quantifier).asInstanceOf[this.type] //
  // override def typeValue(): Map[K, V] = java
}