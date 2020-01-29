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

package org.mmadt.machine.obj.feature

import org.mmadt.machine.obj.Bool

/**
  * @author Marko A. Rodriguez (http://markorodriguez.com)
  */
package object operator {

  // binary operators

  trait And[A] {
    def &(other: A): A
  }

  trait Or[A] {
    def |(other: A): A
  }

  trait Plus[A] {
    def +(other: A): A
  }

  trait Minus[A] {
    def -(other: A): A
  }

  trait Mult[A] {
    def *(other: A): A
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
    def ==(other: A): Bool
  }

  // Unary operators

  trait Neg[A] {
    def -(): A
  }

  trait Not[A] {
    def !(): A
  }


}
