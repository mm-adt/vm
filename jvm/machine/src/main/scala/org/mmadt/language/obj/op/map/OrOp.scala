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
import org.mmadt.language.obj.`type`.{BoolType, __}
import org.mmadt.language.obj.value.BoolValue
import org.mmadt.language.obj.{Bool, Inst, Obj}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait OrOp {
  def or(bool:BoolType):BoolType
  def or(bool:BoolValue):this.type
  final def ||(bool:BoolType):BoolType = this.or(bool)
  final def ||(bool:BoolValue):this.type = this.or(bool)
}

object OrOp {
  def apply(bool:BoolValue):Inst = new VInst((Tokens.or,List(bool)),qOne,((a:Bool,b:List[Obj]) => a.or(bool)).asInstanceOf[(Obj,List[Obj]) => Obj])
  def apply(bool:BoolType):Inst = new VInst((Tokens.or,List(bool)),qOne,((a:Bool,b:List[Obj]) => b.head match {
    case avalue:BoolValue => a.or(avalue)
    case atype:BoolType => a.or(atype)
  }).asInstanceOf[(Obj,List[Obj]) => Obj])

  def apply[O <: Obj with OrOp](other:__):Inst = new VInst((Tokens.or,List(other)),qOne,((a:O,b:List[Obj]) => a.or(other(a.asInstanceOf[BoolType].range).asInstanceOf[BoolType])).asInstanceOf[(Obj,List[Obj]) => Obj])

}