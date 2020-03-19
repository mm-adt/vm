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
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait LteOp[O <: Obj] {
  this:O =>
  def lte(other:Type[O]):OType[Bool]
  def lte(other:Value[O]):Bool
  final def =<(other:Type[O]):OType[Bool] = this.lte(other)
  final def =<(other:Value[O]):Bool = this.lte(other)
}

object LteOp {
  def apply[O <: Obj with LteOp[O]](other:Obj):Inst[O,Bool] = new LteInst[O](other.asInstanceOf[O])

  class LteInst[O <: Obj with LteOp[O]](other:O) extends VInst[O,Bool]((Tokens.lte,List(other))) {
    override def apply(trav:Traverser[O]):Traverser[Bool] = trav.split((Traverser.resolveArg(trav,other) match {
      case avalue:Value[O] => trav.obj().lte(avalue)
      case atype:Type[O] => trav.obj().lte(atype)
    }).q(multQ(trav.obj().q,this.q)))
  }

}
