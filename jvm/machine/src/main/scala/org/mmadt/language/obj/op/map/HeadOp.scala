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

import org.mmadt.language.obj.{IntQ, Obj, Poly}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageFactory.qOne
import org.mmadt.storage.obj.value.VInst

trait HeadOp[A <: Obj] {
  this: Poly[A] =>
  def head(): A = HeadOp().exec(this)
}

object HeadOp {
  def apply[A <: Obj](): HeadInst[A] = new HeadInst[A]

  class HeadInst[A <: Obj](q: IntQ = qOne) extends VInst[Poly[A], A]((Tokens.head, Nil), q) {
    override def q(q: IntQ): this.type = new HeadInst[A](q).asInstanceOf[this.type]
    override def exec(start: Poly[A]): A =
      if (!start.groundList.exists(_.alive))
        throw new LanguageException("no head on empty poly")
      else
        start.groundList.filter(_.alive).head.via(start, this)
  }

}