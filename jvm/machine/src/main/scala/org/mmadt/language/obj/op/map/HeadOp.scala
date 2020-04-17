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

import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.language.obj.{IntQ, Lst, Obj}
import org.mmadt.storage.StorageFactory.qOne
import org.mmadt.storage.obj.value.VInst

trait HeadOp[O <: Obj] {
  this: Lst[O] =>
  def head(): O = if(null == this.value()) throw new LanguageException("no head on empty list") else this.value()._2.via(this,HeadOp[O]()).asInstanceOf[O]
}

object HeadOp {
  def apply[O <: Obj](): HeadInst[O] = new HeadInst[O]

  class HeadInst[O <: Obj](q: IntQ = qOne) extends VInst[Lst[O], O]((Tokens.head, Nil), q) {
    override def q(quantifier: IntQ): this.type = new HeadInst[O](quantifier).asInstanceOf[this.type]
    override def exec(start: Lst[O]): O = start.head().via(start, new HeadInst[O](q))
  }

}