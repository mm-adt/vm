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
import org.mmadt.language.obj.`type`.{BoolType, Type, __}
import org.mmadt.language.obj.op.FilterInstruction
import org.mmadt.language.obj.value.BoolValue
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.obj.qOne
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait IsOp[O <: Obj] {
  this:O =>
  def is(bool:BoolType):O with Type[O]
  def is(bool:BoolValue):this.type
}

object IsOp {
  def apply[O <: Obj with IsOp[O]](bool:BoolValue):Inst = new VInst((Tokens.is,List(bool)),qOne,((a:O,b:List[Obj]) => a.is(bool)).asInstanceOf[(Obj,List[Obj]) => Obj]) with FilterInstruction
  def apply[O <: Obj with IsOp[O]](bool:BoolType):Inst = new VInst((Tokens.is,List(bool)),qOne,((a:O,b:List[Obj]) => b.head match {
    case avalue:BoolValue => a.is(avalue)
    case atype:BoolType => a.is(atype)
  }).asInstanceOf[(Obj,List[Obj]) => Obj]) with FilterInstruction

  def apply[O <: Obj with IsOp[O],T <: Type[T]](bool:__):Inst = new VInst((Tokens.is,List(bool)),qOne,((a:O,b:List[Obj]) => a.is(bool[T](a.asInstanceOf[T].range()).asInstanceOf[BoolType])).asInstanceOf[(Obj,List[Obj]) => Obj]) with FilterInstruction
}

