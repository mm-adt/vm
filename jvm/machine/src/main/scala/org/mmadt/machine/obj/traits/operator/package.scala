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

import org.mmadt.machine.obj.{Bool, Obj}

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
    def div(other: A): A

    final def /(other: A): A = this.div(other)
  }

  trait Gt[A] {
    def gt(other: A): Bool

    final def >(other: A): Bool = this.gt(other)
  }

  trait Lt[A] {
    def lt(other: A): Bool

    final def <(other: A): Bool = this.lt(other)
  }

  trait Gte[A] {
    def gte(other: A): Bool

    final def >=(other: A): Bool = this.gte(other)
  }

  trait Lte[A] {
    def lte(other: A): Bool

    final def =<(other: A): Bool = this.lte(other)
  }

  trait Eq {
    def eq(other: Obj): Bool

    final def ==(other: Obj): Bool = this == other
  }

  // Unary operators

  trait Neg[A] {
    def neg(): A

    final def -(): A = this.neg()
  }

  trait Not[A] {
    def not(): A

    final def !(): A = this.not()
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
