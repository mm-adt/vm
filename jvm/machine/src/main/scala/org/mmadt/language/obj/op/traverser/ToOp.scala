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

package org.mmadt.language.obj.op.traverser

import org.mmadt.language.Tokens
import org.mmadt.language.obj.op.TraverserInstruction
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Inst, OType, Obj}
import org.mmadt.processor.Traverser
import org.mmadt.storage.obj.value.{VInst, VStr}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ToOp[O <: Obj] {
  this:O =>
  def to(label:String):OType[O] = this.to(new VStr(label))
  def to(label:StrValue):OType[O]
}

object ToOp {
  def apply[O <: Obj](label:StrValue):Inst[O,O] = new ToInst(label)

  class ToInst[O <: Obj](label:StrValue) extends VInst[O,O]((Tokens.to,List(label))) with TraverserInstruction {
    override def apply(trav:Traverser[O]):Traverser[O] ={
      trav.split[O](composeInstruction(trav.obj()),trav.state + (this.arg0[StrValue]().value() -> trav.obj()))
    }
  }

}
