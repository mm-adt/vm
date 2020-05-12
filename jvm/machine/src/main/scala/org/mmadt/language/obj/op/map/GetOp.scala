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
import org.mmadt.language.obj.Inst.Func
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
object GetOp extends Func[Obj with GetOp[Obj, Obj], Obj] {
  def apply[A <: Obj, B <: Obj](key: A, typeHint: B = obj.asInstanceOf[B]): Inst[A, B] = new VInst[A, B](g = (Tokens.get, List(key)), func = this)
  override def apply(start: Obj with GetOp[Obj, Obj], inst: Inst[Obj with GetOp[Obj, Obj], Obj]): Obj = start.get(inst.arg0[Obj]).via(start, inst)
}