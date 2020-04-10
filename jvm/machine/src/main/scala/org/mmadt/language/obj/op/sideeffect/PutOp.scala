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

package org.mmadt.language.obj.op.sideeffect

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait PutOp[A <: Obj, B <: Obj] {
  this: Obj =>
  def put(key: A, value: B): this.type
}

object PutOp {
  def apply[A <: Obj, B <: Obj](key: A, value: B): Inst[Rec[A, B], Rec[A, B]] = new PutInst[A, B](key, value)

  class PutInst[A <: Obj, B <: Obj](key: A, value: B, q: IntQ = qOne) extends VInst[Rec[A, B], Rec[A, B]]((Tokens.put, List(key, value)), q) {
    override def q(quantifier: IntQ): this.type = new PutInst[A, B](key, value, quantifier).asInstanceOf[this.type]
    override def exec(start: Rec[A, B]): Rec[A, B] = start.put(key, value).q(multQ(start, this)).via(start,this)
  }

}
