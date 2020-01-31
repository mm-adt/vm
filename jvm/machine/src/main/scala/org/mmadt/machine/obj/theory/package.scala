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
import org.mmadt.machine.obj.theory.operator.`type`.{TypeGt, TypeMult, TypePlus}
import org.mmadt.machine.obj.theory.operator.value.{ValueGt, ValueMult, ValuePlus}
import org.mmadt.machine.obj.traits.operator._

/**
  * @author Marko A. Rodriguez (http://markorodriguez.com)
  */
package object theory {

  ///////////////////
  // Type Algebra  //
  ///////////////////

  trait TypeSemigroupMult[J, V <: Value[J], T <: Type[T]] extends TypeMult[J, V, T] //
  trait TypeSemigroupPlus[J, V <: Value[J], T <: Type[T]] extends TypePlus[J, V, T] //
  trait TypeMonoidMult[J, V <: Value[J], T <: Type[T]] extends TypeSemigroupMult[J, V, T] // with One[A]
  trait TypeMonoidPlus[J, V <: Value[J], T <: Type[T]] extends TypeSemigroupPlus[J, V, T] // with Zero[A]
  trait TypeGroupPlus[J, V <: Value[J], T <: Type[T]] extends TypeMonoidPlus[J, V, T] // with Neg[A]
  trait TypeGroupMult[J, V <: Value[J], T <: Type[T]] extends TypeMonoidMult[J, V, T] // with Neg[A]
  trait TypeRng[J, V <: Value[J], T <: Type[T]] extends TypeGroupPlus[J, V, T] with TypeMonoidMult[J, V, T] //
  trait TypeRing[J, V <: Value[J], T <: Type[T]] extends TypeGroupPlus[J, V, T] with TypeMonoidMult[J, V, T] // with Minus[J, V, T]
  trait TypeField[J, V <: Value[J], T <: Type[T]] extends TypeGroupMult[J, V, T] with TypeGroupPlus[J, V, T] //
  trait TypeOrder[J, V <: Value[J], T <: Type[T]] extends TypeGt[J, V, T]

  trait TypeLogical[J, V <: Value[J], T <: Type[T]] // extends And[A] with Or[A]

  ////////////////////
  // Value Algebra  //
  ////////////////////


  ////////// OLD -- TO BE CONVERTED

  trait SemigroupMult[A] // extends Mult[A]

  trait SemigroupPlus[A] //extends Plus[A]

  trait MonoidMult[A] extends SemigroupMult[A] with One[A]

  trait MonoidPlus[A] extends SemigroupPlus[A] with Zero[A]

  trait GroupPlus[A] extends MonoidPlus[A] with Neg[A]

  trait GroupMult[A] extends MonoidMult[A] with Neg[A]

  trait Rng[A] extends GroupPlus[A] with MonoidMult[A]

  trait Ring[A] extends GroupPlus[A] with MonoidMult[A] // with Minus[A]

  trait ValueRing[J, V <: Value[J], T <: Type[T]] extends ValuePlus[J, V, T] with ValueMult[J, V, T]

  trait Field[A] extends GroupMult[A] with GroupPlus[A]

  trait ValueOrder[J, V <: Value[J], T <: Type[T]] extends ValueGt[J, V, T] // with Gte[A] with Lt[A] with Lte[A]

  trait Logical[A] extends And[A] with Or[A]

  trait CommutativePlus[A]

  // extends Plus[A]

  trait CommutativeMult[A] extends Mult[A]

  trait Commutative[A] extends CommutativePlus[A] with CommutativeMult[A]

}
