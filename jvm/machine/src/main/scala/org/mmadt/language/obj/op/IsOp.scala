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

package org.mmadt.language.obj.op

import org.mmadt.language.Tokens
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.language.obj.`type`.{BoolType, Type}
import org.mmadt.language.obj.value.{BoolValue, Value}
import org.mmadt.storage.obj.qOne
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait IsOp[O <: Obj with IsOp[O, V, T], V <: Value[V], T <: Type[T]] {
  this: O =>

  def is(other: BoolType): T //
  def is(other: BoolValue): O //

}
object IsOp {
  def apply[O <: Obj with IsOp[O, V, T], V <: Value[V], T <: Type[T]](bool: BoolValue): Inst = new VInst((Tokens.is, List(bool)), qOne, ((a: O, b: List[Obj]) => a.is(bool)).asInstanceOf[(Obj, List[Obj]) => Obj]) //
  def apply[O <: Obj with IsOp[O, V, T], V <: Value[V], T <: Type[T]](bool: BoolType): Inst = new VInst((Tokens.is, List(bool)), qOne, ((a: O, b: List[Obj]) => b.head match {
    case v: BoolValue with V => a.is(v)
    case t: BoolType with T => a.is(t)
  }).asInstanceOf[(Obj, List[Obj]) => Obj])
}

