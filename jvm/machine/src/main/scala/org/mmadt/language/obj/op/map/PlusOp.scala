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
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.Value
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait PlusOp[T <: Type[Obj],V <: Value[Obj]] {
  this:Obj =>
  def plus(other:T):T
  def plus(other:V):this.type
  def plus(other:Obj):this.type = (other match {
    case atype:T => this.plus(atype)
    case avalue:V => this.plus(avalue)
  }).asInstanceOf[this.type]
  final def +(other:T):T = this.plus(other)
  final def +(other:V):this.type = this.plus(other)
}

object PlusOp {
  def apply[O <: Obj with PlusOp[Type[O],Value[O]]](other:Obj):Inst[O,O] = new PlusInst[O](other)

  class PlusInst[O <: Obj with PlusOp[Type[O],Value[O]]](other:Obj,q:IntQ = qOne) extends VInst[O,O]((Tokens.plus,List(other)),q) {
    override def q(quantifier:IntQ):this.type = new PlusInst[O](other,quantifier).asInstanceOf[this.type]
    override def apply(trav:Traverser[O]):Traverser[O] = trav.split((trav.obj() match {
      case atype:Type[_] => atype.compose(new PlusInst[O](Traverser.resolveArg(trav,other),q))
      case avalue:Value[_] => avalue.plus(Traverser.resolveArg(trav,other)).q(multQ(avalue,this))
    }).asInstanceOf[O])
  }

}

