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
import org.mmadt.machine.obj.theory.obj.Inst
import org.mmadt.machine.obj.theory.obj.`type`.{StrType, Type}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TStr(insts: List[(Type[_], Inst)], quantifier: TQ) extends TObj[StrType](insts, quantifier) with StrType {
  def this() = this(Nil, qOne) //
  override def push(inst: Inst): StrType = str(inst, quantifier) //
  override def pop(): this.type = new TStr(insts.tail, quantifier).asInstanceOf[this.type] //
  override def q(quantifier: TQ): this.type = new TStr(insts, quantifier).asInstanceOf[this.type] //
}
