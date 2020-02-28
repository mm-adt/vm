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
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.obj.qOne
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait PlusOp[O <: Obj] {
  this:O =>

  def plus(other:Type[O]):Type[O]
  def plus(other:Value[O]):this.type
  final def +(other:Type[O]):Type[O] = this.plus(other)
  final def +(other:Value[O]):this.type = this.plus(other)
}

object PlusOp {
  def apply[O <: Obj with PlusOp[O]](other:Value[O]):Inst = new VInst((Tokens.plus,List(other)),qOne,((a:O,b:List[Obj]) => a.plus(other)).asInstanceOf[(Obj,List[Obj]) => Obj]) //
  def apply[O <: Obj with PlusOp[O]](other:Type[O]):Inst = new VInst((Tokens.plus,List(other)),qOne,((a:O,b:List[Obj]) => b.head match {
    case avalue:Value[O] => a.plus(avalue)
    case atype:Type[O] => a.plus(atype)
  }).asInstanceOf[(Obj,List[Obj]) => Obj])

  def apply[O <: Obj with PlusOp[O]](other:__):Inst = new VInst((Tokens.plus,List(other)),qOne,
    ((a:O,b:List[Obj]) => a.plus(other(a.asInstanceOf[Type[O]].range()).asInstanceOf[Type[O]])).asInstanceOf[(Obj,List[Obj]) => Obj])
}

