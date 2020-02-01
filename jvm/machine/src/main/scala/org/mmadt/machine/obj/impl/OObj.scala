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

package org.mmadt.machine.obj.impl

import org.mmadt.machine.obj.impl.`type`.{TBool, TInt}
import org.mmadt.machine.obj.impl.value.{VBool, VInst, VInt}
import org.mmadt.machine.obj.theory.obj.Obj
import org.mmadt.machine.obj.theory.obj.`type`.{BoolType, IntType}
import org.mmadt.machine.obj.theory.obj.value.{BoolValue, IntValue}
import org.mmadt.machine.obj.{Inst, TQ}

/**
  * @author Marko A. Rodriguez (http://markorodriguez.com)
  */
abstract class OObj(val quantifier: TQ) extends Obj {

  override def q(): (IntValue, IntValue) = quantifier

  override def int(inst: List[Inst], q: (IntValue, IntValue)): IntType = new TInt(inst) //
  override def int(value: Long): IntValue = new VInt(value) //

  override def bool(inst: List[Inst], q: (IntValue, IntValue)): BoolType = new TBool(inst, q) //
  override def bool(value: Boolean): BoolValue = new VBool(value) //

  override def inst(op: String, args: List[Obj]): Inst = new VInst((op, args))
}
