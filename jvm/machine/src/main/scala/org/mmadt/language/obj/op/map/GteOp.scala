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
import org.mmadt.storage.StorageFactory.{bool, qOne}
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait GteOp[T <: Type[Obj],V <: Value[Obj]] {
  this:Obj =>
  def gte(other:V):Bool
  def gte(other:T):BoolType = this match {
    case atype:Type[_] => atype.compose(bool,GteOp(other))
    case avalue:Value[_] => avalue.start().compose(bool,GteOp(other))
  }
  final def >=(other:V):Bool = this.gte(other)
  final def >=(other:T):BoolType = this.gte(other)
}

object GteOp {
  def apply[O <: Obj with GteOp[Type[O],Value[O]]](other:Obj):Inst[O,Bool] = new GteInst[O](other.asInstanceOf[O])

  class GteInst[O <: Obj with GteOp[Type[O],Value[O]]](other:O,q:IntQ = qOne) extends VInst[O,Bool]((Tokens.gte,List(other)),q) {
    override def q(quantifier:IntQ):this.type = new GteInst[O](other,quantifier).asInstanceOf[this.type]
    override def exec(start: O): Bool = start match {
      case atype: Type[_] => atype.compose(bool, new GteInst(Inst.resolveArg(start, other), q))
      case avalue: Value[_] => (Inst.resolveArg(start, other) match {
        case _: Type[_] => avalue.start[O]().compose(bool, new GteInst(other, q))
        case bvalue: Value[O] => avalue.gte(bvalue).clone(_via = (avalue,this))
      }).q(multQ(avalue, this)._2)
    }
  }

}
