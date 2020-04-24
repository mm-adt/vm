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
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait GetOp[A <: Obj, B <: Obj] {
  this: Obj =>
  def get(key: A): B
  def get[BB <: Obj](key: A, btype: BB): BB
}

object GetOp {
  def apply[A <: Obj, B <: Obj](key: A): GetInst[A, B] = new GetInst[A, B](key)
  def apply[A <: Obj, B <: Obj](key: A, typeHint: B): GetInst[A, B] = new GetInst(key, typeHint)

  type GetType[A <: Obj, B <: Obj] = Obj with GetOp[A, B]

  class GetInst[A <: Obj, B <: Obj](key: A, typeHint: B = obj.asInstanceOf[B], q: IntQ = qOne) extends VInst[GetType[A, B], B]((Tokens.get, List(key)), q) {
    override def q(q: IntQ): this.type = new GetInst[A, B](key, typeHint, q).asInstanceOf[this.type]
    override def exec(start: GetType[A, B]): B = {
      val inst = new GetInst[A, B](Inst.resolveArg(start, key), typeHint, q)
      start.get(inst.arg0[A]()).via(start, inst)
    }
  }

}
