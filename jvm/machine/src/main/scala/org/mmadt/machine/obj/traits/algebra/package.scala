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

package org.mmadt.machine.obj.traits

import org.mmadt.machine.obj.traits.operator._

/**
  * @author Marko A. Rodriguez (http://markorodriguez.com)
  */
package object algebra {

  trait SemigroupMult[A] extends Mult[A]

  trait SemigroupPlus[A] extends Plus[A]

  trait MonoidMult[A] extends SemigroupMult[A] with One[A]

  trait MonoidPlus[A] extends SemigroupPlus[A] with Zero[A]

  trait GroupPlus[A] extends MonoidPlus[A] with Neg[A]

  trait GroupMult[A] extends MonoidMult[A] with Neg[A]

  trait Rng[A] extends GroupPlus[A] with MonoidMult[A]

  trait Ring[A] extends GroupPlus[A] with MonoidMult[A] with Minus[A]

  trait Field[A] extends GroupMult[A] with GroupPlus[A]

  trait Order[A] extends Gt[A] with Gte[A] with Lt[A] with Lte[A]

  trait Logical[A] extends And[A] with Or[A]

  trait CommutativePlus[A] extends Plus[A]

  trait CommutativeMult[A] extends Mult[A]

  trait Commutative[A] extends CommutativePlus[A] with CommutativeMult[A]

}
