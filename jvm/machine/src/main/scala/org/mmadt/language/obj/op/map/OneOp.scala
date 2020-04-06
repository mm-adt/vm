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
import org.mmadt.language.obj.{Inst, IntQ, Obj, multQ}
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait OneOp[O <: Obj] {
  this:O =>
  def one():O with OneOp[O]
}

object OneOp {
  def apply[O <: Obj with OneOp[O]]():OneInst[O] = new OneInst[O]

  class OneInst[O <: Obj with OneOp[O]](q:IntQ = qOne) extends VInst[O,O]((Tokens.one,Nil),q) {
    override def q(quantifier:IntQ):this.type = new OneInst[O](quantifier).asInstanceOf[this.type]
    override def apply(trav:Traverser[O]):Traverser[O] = trav.split(trav.obj() match {
      case atype:Type[_] => atype.compose(trav.obj(),this)
      case _ => trav.obj().one().q(multQ(trav.obj(),this)).asInstanceOf[O]
    })
  }

}