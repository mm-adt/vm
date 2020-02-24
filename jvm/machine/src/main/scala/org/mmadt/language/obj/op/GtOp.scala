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

package org.mmadt.language.obj.op

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{BoolType, Type, __}
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.obj.qOne
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait GtOp[O <: Obj with GtOp[O,V,T],V <: Value[V],T <: Type[T]] {
  this:O =>

  def gt(other:T):BoolType //
  def gt(other:V):Bool //
  def gt():BoolType //
  final def >(other:T):BoolType = this.gt(other) //
  final def >(other:V):Bool = this.gt(other) //
}

object GtOp {
  def apply[O <: Obj with GtOp[O,V,T],V <: Value[V],T <: Type[T]](other:V):Inst = new VInst((Tokens.gt,List(other)),qOne,((a:O,b:List[Obj]) => a.gt(other)).asInstanceOf[(Obj,List[Obj]) => Obj]) //
  def apply[O <: Obj with GtOp[O,V,T],V <: Value[V],T <: Type[T]](other:T):Inst = new VInst((Tokens.gt,List(other)),qOne,((a:O,b:List[Obj]) => b.head match {
    case avalue:OValue with V => a.gt(avalue)
    case atype:OType with T => a.gt(atype)
  }).asInstanceOf[(Obj,List[Obj]) => Obj])

  def apply[O <: Obj with GtOp[O,V,T],V <: Value[V],T <: Type[T]](other:__):Inst = new VInst((Tokens.gt,List(other)),qOne,((a:O,b:List[Obj]) => a.gt(other[T](a.asInstanceOf[T].range()))).asInstanceOf[(Obj,List[Obj]) => Obj])
}
