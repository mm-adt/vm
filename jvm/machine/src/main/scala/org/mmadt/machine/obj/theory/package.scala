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

package org.mmadt.machine.obj

import org.mmadt.machine.obj.theory.obj.`type`.Type
import org.mmadt.machine.obj.theory.obj.value.Value
import org.mmadt.machine.obj.theory.operator.`type`._
import org.mmadt.machine.obj.theory.operator.value._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
package object theory {

  ///////////////////
  // Type Algebra  //
  ///////////////////

  trait TypeSemigroupMult[J, V <: Value[V], T <: Type[T]] extends TypeMult[J, V, T] //
  trait TypeSemigroupPlus[J, V <: Value[V], T <: Type[T]] extends TypePlus[J, V, T] //
  trait TypeMonoidMult[J, V <: Value[V], T <: Type[T]] extends TypeSemigroupMult[J, V, T] // with One[A]
  trait TypeMonoidPlus[J, V <: Value[V], T <: Type[T]] extends TypeSemigroupPlus[J, V, T] // with Zero[A]
  trait TypeGroupPlus[J, V <: Value[V], T <: Type[T]] extends TypeMonoidPlus[J, V, T] // with Neg[A]
  trait TypeGroupMult[J, V <: Value[V], T <: Type[T]] extends TypeMonoidMult[J, V, T] // with Neg[A]
  trait TypeRng[J, V <: Value[V], T <: Type[T]] extends TypeGroupPlus[J, V, T] with TypeMonoidMult[J, V, T] //
  trait TypeRing[J, V <: Value[V], T <: Type[T]] extends TypeGroupPlus[J, V, T] with TypeMonoidMult[J, V, T] // with Minus[J, V, T]
  trait TypeField[J, V <: Value[V], T <: Type[T]] extends TypeGroupMult[J, V, T] with TypeGroupPlus[J, V, T] //
  trait TypeOrder[J, V <: Value[V], T <: Type[T]] extends TypeGt[J, V, T] //
  trait TypeLogical[T <: Type[T]] extends TypeAnd[T] with TypeOr[T] //
  trait TypeCommon[T <: Type[T]] extends TypeIs[T] with TypeTo[T] with TypeFrom[T] //

  ////////////////////
  // Value Algebra  //
  ////////////////////

  trait ValueRing[J, V <: Value[V], T <: Type[T]] extends ValuePlus[J, V, T] with ValueMult[J, V, T] //
  trait ValueOrder[J, V <: Value[V], T <: Type[T]] extends ValueGt[J, V, T] // with Gte[A] with Lt[A] with Lte[A]
  trait ValueLogical[V <: Value[V]] extends ValueAnd[V] with ValueOr[V] //
  trait ValueCommon[V <: Value[V], T <: Type[T]] extends ValueIs[V, T] with ValueTo[V, T] with ValueFrom[V, T] //

  // trait CommutativePlus[A]
  // trait CommutativeMult[A] extends Mult[A]
  // trait Commutative[A] extends CommutativePlus[A] with CommutativeMult[A]

}
