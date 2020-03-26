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
import org.mmadt.language.obj.`type`.{BoolType, Type}
import org.mmadt.language.obj.op.FilterInstruction
import org.mmadt.language.obj.value.{BoolValue, Value}
import org.mmadt.language.obj.{Inst, OType, Obj, minZero}
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait IsOp {
  this:Obj =>
  def is(bool:BoolType):OType[this.type] = (this match {
    case atype:Type[_] => atype.compose(asType(this),IsOp(bool)).q(minZero(this.q))
    case avalue:Value[_] => avalue.start().is(bool)
  }).asInstanceOf[OType[this.type]]

  def is(bool:BoolValue):this.type = this match {
    case atype:Type[_] => atype.compose(this.q(minZero(this.q)),IsOp(bool.start()))
    case _ => if (bool.value) this else this.q(0)
  }
}

object IsOp {
  def apply[O <: Obj with IsOp](other:Obj):Inst[O,O] = new IsInst[O](other)

  class IsInst[O <: Obj with IsOp](other:Obj) extends VInst[O,O]((Tokens.is,List(other))) with FilterInstruction {
    override def apply(trav:Traverser[O]):Traverser[O] = trav.split(Traverser.resolveArg(trav,other) match {
      case avalue:BoolValue => trav.obj().is(avalue)
      case atype:BoolType => trav.obj().is(atype)
    })
  }

}

