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

package org.mmadt.language.model

import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.{IntType, Type}
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object Algebra {

  def universal(atype:Type[Obj]):Model = Model.from(atype.id() -> atype)

  type MultOne[T <: Obj] = Type[T] with MultOp[Type[T],Value[T]] with OneOp
  type PlusZero[T <: Obj] = Type[T] with PlusOp[Type[T],Value[T]] with ZeroOp
  def monoid[O <: Type[O]](monoid:Obj)(op:String):Model ={
    op match {
      case "*" =>
        val m = monoid.asInstanceOf[MultOne[O]]
        Model.from(m.mult(m.one()) -> m)
      case "+" =>
        val p = monoid.asInstanceOf[PlusZero[O]]
        Model.from(p.plus(p.zero()) -> p)
    }
  }

  type MultDivOne[T <: Obj] = MultOne[T] // with InverseOp with DivOp
  type PlusMinusZero[T <: Obj] = PlusZero[T] with NegOp // with MinusOp
  def group[O <: Type[O]](group:Obj)(op:String,identity:Value[Obj]):Model ={
    op match {
      case "*" =>
        val m = group.asInstanceOf[MultOne[O]]
        Model.from(
          //m.one() -> identity.start(),
          m.one().one() -> m.one(),
          m.mult(m.one()) -> m)
      case "+" =>
        val p = group.asInstanceOf[PlusMinusZero[O]]
        Model.from(
          //p.zero() -> identity.start(),
          p + p.zero() -> p,
          p + -p -> p.zero(),
          p.zero().zero() -> p.zero(),
          p.neg().zero() -> p.zero(),
          -(-p) -> p)
    }
  }

  def ring(ring:IntType):Model ={
    group(ring)("+",int(0)).put(
      group(ring)("*",int(1))).put(
      Model.from(
        ring.mult(ring.one().neg()) -> ring.neg(),
        ring.to("x").mult(ring.to("y").plus(ring.to("z"))) -> ring.from[IntType]("x").plus(ring.from[IntType]("y")).mult(ring.from[IntType]("x").plus(ring.from[IntType]("z")))
      ))
  }
}
