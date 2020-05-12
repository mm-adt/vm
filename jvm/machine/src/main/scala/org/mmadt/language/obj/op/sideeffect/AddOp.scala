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

package org.mmadt.language.obj.op.sideeffect

import org.mmadt.language.Tokens
import org.mmadt.language.obj.op.SideEffectInstruction
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait AddOp {
  this: Obj =>
  def add[O <: Obj](obj: O): O
}

object AddOp {
  def apply[O <: Obj with AddOp](obj: Obj): Inst[O, O] = new AddInst[O](obj)

  class AddInst[O <: Obj with AddOp](obj: Obj) extends VInst[O, O](g=(Tokens.add, List(obj))) with SideEffectInstruction {
    override def exec(start: O): O = start.add(Inst.resolveArg(start, obj).asInstanceOf[O])
  }

}
