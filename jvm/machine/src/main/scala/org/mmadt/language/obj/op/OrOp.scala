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
import org.mmadt.language.obj.`type`.BoolType
import org.mmadt.language.obj.value.BoolValue
import org.mmadt.language.obj.{Bool, Inst, Obj}
import org.mmadt.storage.obj.qOne
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait OrOp {
  this: Bool =>
  def or(bool: BoolType): BoolType //
  def or(bool: BoolValue): Bool //
  final def ||(bool: BoolType): BoolType = this.or(bool) //
  final def ||(bool: BoolValue): Bool = this.or(bool) //
}

object OrOp {
  def apply(other: BoolValue): Inst = new VInst((Tokens.and, List(other)), qOne, ((a: Bool, b: List[Obj]) => a.or(other)).asInstanceOf[(Obj, List[Obj]) => Obj]) //
  def apply(other: BoolType): Inst = new VInst((Tokens.and, List(other)), qOne, ((a: Bool, b: List[Obj]) => b.head match {
    case v: BoolValue => a.or(v)
    case t: BoolType => a.or(t)
  }).asInstanceOf[(Obj, List[Obj]) => Obj])
}