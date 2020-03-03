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
import org.mmadt.language.obj.`type`.{BoolType, Type, __}
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait GtOp[O <: Obj] {
  this:O =>
  def gt(other:Type[O]):BoolType
  def gt(other:Value[O]):Bool
  final def >(other:Type[O]):BoolType = this.gt(other)
  final def >(other:Value[O]):Bool = this.gt(other)
}

object GtOp {
  def apply[O <: Obj with GtOp[O]](other:Value[O]):Inst = new VInst((Tokens.gt,List(other)),qOne,((a:O,b:List[Obj]) => a.gt(other)).asInstanceOf[(Obj,List[Obj]) => Obj]) //
  def apply[O <: Obj with GtOp[O]](other:Type[O]):Inst = new VInst((Tokens.gt,List(other)),qOne,((a:O,b:List[Obj]) => b.head match {
    case avalue:Value[O] => a.gt(avalue)
    case atype:Type[O] => a.gt(atype)
  }).asInstanceOf[(Obj,List[Obj]) => Obj])

  def apply[O <: Obj with GtOp[O]](other:__):Inst = new VInst((Tokens.gt,List(other)),qOne,((a:O,b:List[Obj]) => a.gt(other(a.asInstanceOf[Type[O]].range()).asInstanceOf[Type[O]])).asInstanceOf[(Obj,List[Obj]) => Obj])
}
