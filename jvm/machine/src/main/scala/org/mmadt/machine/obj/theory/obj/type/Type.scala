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

package org.mmadt.machine.obj.theory.obj.`type`

import org.mmadt.language.Stringer
import org.mmadt.machine.obj.TQ
import org.mmadt.machine.obj.theory.obj.value.{BoolValue, IntValue, StrValue}
import org.mmadt.machine.obj.theory.obj.{Inst, Obj}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Type[T <: Type[T]] extends Obj {

  def insts(): List[(Type[_], Inst)] //
  def pop(): T //
  def push(inst: Inst): T //
  def push[O <: Obj, T2 <: Type[T2]](t2: O, inst: Inst): Type[T2] = t2 match {
    case _: IntType => int(inst).asInstanceOf[Type[T2]]
    case _: IntValue => int(inst).asInstanceOf[Type[T2]]
    case _: BoolType => bool(inst).asInstanceOf[Type[T2]]
    case _: BoolValue => bool(inst).asInstanceOf[Type[T2]]
    case _: StrType => str(inst).asInstanceOf[Type[T2]]
    case _: StrValue => str(inst).asInstanceOf[Type[T2]]
  }

  def int(): IntType = int(null) //
  def int(inst: Inst): IntType = int(inst, this.q()) //
  def int(inst: Inst, q: TQ): IntType //

  def bool(): BoolType = bool(null) //
  def bool(inst: Inst): BoolType = bool(inst, this.q()) //
  def bool(inst: Inst, q: TQ): BoolType //

  def str(): StrType = str(null) //
  def str(inst: Inst): StrType = str(inst, this.q()) //
  def str(inst: Inst, q: TQ): StrType //

  def rec(): RecType[Obj, Obj] = rec[Obj, Obj](null, null) //
  def rec[K <: Obj, V <: Obj](tvalue: Map[K, V], inst: Inst): RecType[K, V] = rec(tvalue, inst, this.q()) //
  def rec[K <: Obj, V <: Obj](tvalue: Map[K, V], inst: Inst, q: TQ): RecType[K, V] //

  override def toString: String = Stringer.typeString(this) //

  final def <=[TT <: Type[TT]](mapFrom: Type[TT]): Type[TT] = mapFrom.q(this.q())

}
