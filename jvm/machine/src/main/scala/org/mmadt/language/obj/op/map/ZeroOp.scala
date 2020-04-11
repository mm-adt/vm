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
import org.mmadt.language.obj.{IntQ, Obj, multQ}
import org.mmadt.storage.StorageFactory.qOne
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ZeroOp {
  this: Obj =>
  def zero(): this.type
}

object ZeroOp {
  //protected type S <: Obj with ZeroOp

  def apply[S <: Obj with ZeroOp](): ZeroInst[S] = new ZeroInst[S]

  class ZeroInst[S <: Obj with ZeroOp](q: IntQ = qOne) extends VInst[S, S]((Tokens.zero, Nil), q) {
    override def q(quantifier: IntQ): this.type = new ZeroInst(quantifier).asInstanceOf[this.type]
    override def exec(start: S): S = start match {
      case atype: Type[_] => atype.compose(start, this)
      case _ => start.zero().via(start,this)
    }

  }

}