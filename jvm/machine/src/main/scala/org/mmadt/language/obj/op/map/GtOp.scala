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
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory.bool
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait GtOp[T <: Type[Obj],V <: Value[Obj]] {
  this:Obj =>
  def gt(other:V):Bool
  def gt(other:T):BoolType = this match {
    case atype:Type[_] => atype.compose(bool,GtOp(other))
    case avalue:Value[_] => avalue.start().compose(bool,GtOp(other))
  }
  final def >(other:V):Bool = this.gt(other)
  final def >(other:T):BoolType = this.gt(other)
}

object GtOp {
  def apply[O <: Obj with GtOp[Type[O],Value[O]]](other:Obj):Inst[O,Bool] = new GtInst[O](other.asInstanceOf[O])

  class GtInst[O <: Obj with GtOp[Type[O],Value[O]]](other:O) extends VInst[O,Bool]((Tokens.gt,List(other))) {
    override def apply(trav:Traverser[O]):Traverser[Bool] = trav.split((Traverser.resolveArg(trav,other) match {
      case avalue:Value[O] => trav.obj().gt(avalue)
      case atype:Type[O] => trav.obj().gt(atype)
    }).q(multQ(trav.obj().q,this.q)))
  }

}
