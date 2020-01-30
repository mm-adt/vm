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

import org.mmadt.machine.obj.Bool

/**
  * @author Marko A. Rodriguez (http://markorodriguez.com)
  */
package object operator {

  // Binary operators

  trait And[A] {
    def and(other: A): A

    final def &(other: A): A = this.and(other)
  }

  trait Or[A] {
    def or(other: A): A

    final def |(other: A): A = this.or(other)
  }

  trait Plus[A] {
    def plus(other: A): A

    final def +(other: A): A = this.plus(other)
  }

  trait Minus[A] {
    def minus(other: A): A

    final def -(other: A): A = this.minus(other)
  }

  trait Mult[A] {
    def mult(other: A): A

    final def *(other: A): A = this.mult(other)
  }

  trait Div[A] {
    def /(other: A): A
  }

  trait Gt[A] {
    def >(other: A): Bool
  }

  trait Lt[A] {
    def <(other: A): Bool
  }

  trait Gte[A] {
    def >=(other: A): Bool
  }

  trait Lte[A] {
    def =<(other: A): Bool
  }

  trait Eq[A] {
    def eq(other: A): Bool

    final def ==(other: A): Bool = this == other
  }

  // Unary operators

  trait Neg[A] {
    def neg(): A

    final def -(): A = this.neg()
  }

  trait Not[A] {
    def !(): A
  }

  // Singleton operators

  trait Zero[A] {
    def zero(): A

    final def _O(): A = zero()
  }

  trait One[A] {
    def one(): A

    final def _1(): A = one()
  }


}
