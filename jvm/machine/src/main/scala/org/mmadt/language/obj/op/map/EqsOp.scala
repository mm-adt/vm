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
import org.mmadt.processor.Traverser
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait EqsOp[O <: Obj] {
  this:O =>
  def eqs(other:Type[O]):BoolType
  def eqs(other:Value[O]):Bool
  // final def ===(other: T): BoolType = this.eq(other)
  // final def ===(other: V): Bool = this.eq(other)
}

object EqsOp {
  def apply[O <: Obj with EqsOp[O]](other:Obj):Inst = new EqsInst[O](other)

  class EqsInst[O <: Obj with EqsOp[O]](other:Obj) extends VInst((Tokens.eqs,List(other))) {
    override def apply(trav:Traverser[Obj]):Traverser[Obj] ={
      trav.split(Traverser.resolveArg(trav,other) match {
        case avalue:Value[O] => trav.obj().asInstanceOf[O].eqs(avalue)
        case atype:Type[O] => trav.obj().asInstanceOf[O].eqs(atype)
        case anon:__ => trav.obj().asInstanceOf[O].eqs(anon[OType[O]](trav.obj().asInstanceOf[O]))
      })
    }
  }

}