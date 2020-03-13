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
import org.mmadt.language.obj.{Bool, Inst, Obj, multQ}
import org.mmadt.processor.Traverser
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait OrOp {
  this:Bool =>
  def or(bool:BoolType):BoolType
  def or(bool:BoolValue):this.type
  final def ||(bool:BoolType):BoolType = this.or(bool)
  final def ||(bool:BoolValue):this.type = this.or(bool)
}

object OrOp {
  def apply(other:Obj):Inst[Bool,Bool] = new OrInst(other)

  class OrInst(other:Obj) extends VInst[Bool,Bool]((Tokens.or,List(other))) {
    override def apply(trav:Traverser[Bool]):Traverser[Bool] ={
      trav.split((Traverser.resolveArg(trav,other) match {
        case avalue:BoolValue => trav.obj().or(avalue)
        case atype:BoolType => trav.obj().or(atype)
      }).q(multQ(trav.obj().q,this.q)))
    }
  }

}