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

package org.mmadt.language.obj.op.map

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{BoolType, Type}
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait LtOp[T <: Type[Obj],V <: Value[Obj]] {
  this:Obj =>
  def lt(other:V):Bool
  def lt(other:T):BoolType = this match {
    case atype:Type[_] => atype.compose(bool,LtOp(other))
    case avalue:Value[_] => avalue.start().compose(bool,LtOp(other))
  }
  final def <(other:V):Bool = this.lt(other)
  final def <(other:T):BoolType = this.lt(other)
}

object LtOp {
  def apply[O <: Obj with LtOp[Type[O],Value[O]]](other:Obj):Inst[O,Bool] = new LtInst[O](other.asInstanceOf[O])

  class LtInst[O <: Obj with LtOp[Type[O],Value[O]]](other:O,q:IntQ = qOne) extends VInst[O,Bool]((Tokens.lt,List(other)),q) {
    override def q(quantifier:IntQ):this.type = new LtInst[O](other,quantifier).asInstanceOf[this.type]
    override def exec(start: O): Bool = start match {
      case atype: Type[_] => atype.compose(bool, new LtInst(Inst.resolveArg(start, other), q))
      case avalue: Value[_] => Inst.resolveArg(start, other) match {
        case _: Type[_] => avalue.start[O]().compose(bool, new LtInst(other, q))
        case bvalue: Value[O] => avalue.lt(bvalue).via(start,this)
      }
    }
  }

}
