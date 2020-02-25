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
import org.mmadt.language.obj.`type`.BoolType
import org.mmadt.language.obj.value.BoolValue
import org.mmadt.language.obj.{Bool, Inst, Obj}
import org.mmadt.storage.obj.qOne
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait AndOp {
  def and(bool:BoolType):BoolType
  def and(bool:BoolValue):this.type
  final def &&(bool:BoolType):BoolType = this.and(bool)
  final def &&(bool:BoolValue):this.type = this.and(bool)
}

object AndOp {
  def apply(bool:BoolValue):Inst = new VInst((Tokens.and,List(bool)),qOne,((a:Bool,b:List[Obj]) => a.and(bool)).asInstanceOf[(Obj,List[Obj]) => Obj])
  def apply(bool:BoolType):Inst = new VInst((Tokens.and,List(bool)),qOne,((a:Bool,b:List[Obj]) => b.head match {
    case avalue:BoolValue => a.and(avalue)
    case atype:BoolType => a.and(atype)
  }).asInstanceOf[(Obj,List[Obj]) => Obj])
}
