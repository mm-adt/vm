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
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, IntQ, Obj, multQ}
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory.qOne
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait MultOp[T <: Type[Obj],V <: Value[Obj]] {
  this:Obj =>
  def mult(other:T):T
  def mult(other:V):this.type
  final def *(other:T):T = this.mult(other)
  final def *(other:V):this.type = this.mult(other)
  def mult(other:Obj):this.type = (other match {
    case atype:T => this.mult(atype)
    case avalue:V => this.mult(avalue)
  }).asInstanceOf[this.type]
}

object MultOp {
  def apply[O <: Obj with MultOp[T,V],T <: Type[O],V <: Value[O]](other:Obj):Inst[O,O] = new MultInst[O,T,V](other)

  class MultInst[O <: Obj with MultOp[T,V],T <: Type[O],V <: Value[O]](other:Obj,q:IntQ = qOne) extends VInst[O,O]((Tokens.mult,List(other)),q) {
    override def q(quantifier:IntQ):this.type = new MultInst[O,T,V](other,quantifier).asInstanceOf[this.type]
    override def apply(trav:Traverser[O]):Traverser[O] = trav.split((trav.obj() match {
      case atype:Type[_] => atype.compose(new MultInst[O,T,V](Traverser.resolveArg(trav,other),q))
      case avalue:Value[_] => avalue.mult(Traverser.resolveArg(trav,other)).q(multQ(avalue,this))
    }).asInstanceOf[O])
  }

}

