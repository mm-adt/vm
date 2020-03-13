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

package org.mmadt.language.obj.op.filter

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.BoolType
import org.mmadt.language.obj.op.FilterInstruction
import org.mmadt.language.obj.value.BoolValue
import org.mmadt.language.obj.{Inst, OType, Obj}
import org.mmadt.processor.Traverser
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait IsOp[O <: Obj] {
  this:O =>
  def is(bool:BoolType):OType[O]
  def is(bool:BoolValue):this.type
}

object IsOp {
  def apply[O <: Obj with IsOp[O]](other:Obj):Inst[O,O] = new IsInst[O](other)

  class IsInst[O <: Obj with IsOp[O]](other:Obj) extends VInst[O,O]((Tokens.is,List(other))) with FilterInstruction {
    override def apply(trav:Traverser[O]):Traverser[O] = trav.split(Traverser.resolveArg(trav,other) match {
      case avalue:BoolValue => trav.obj().is(avalue)
      case atype:BoolType => trav.obj().is(atype)
    })
  }

}

