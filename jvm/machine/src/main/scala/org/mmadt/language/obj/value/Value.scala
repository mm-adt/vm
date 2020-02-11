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

package org.mmadt.language.obj.value

import org.mmadt.language.Stringer
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.{Type, TypeChecker}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Value[V <: Value[V]] extends Obj {

  def value(): Any //
  def start(): Type[_] //

  override def toString: String = Stringer.valueString(this) //

  override def map[O <: Obj](other: O): O = other match {
    case _: Value[_] => other
    case t: O with Type[_] => (this ==> t).asInstanceOf[O] //
  }

  override def from[O <: Obj](label: StrValue): O = this.start().from(label) //

  override def equals(other: Any): Boolean = other match {
    case v: Value[V] => v.value() == this.value()
    case _ => false
  }

  // pattern matching methods
  override def test(other: Obj): Boolean = other match {
    case argValue: Value[_] => TypeChecker.matchesVV(this, argValue)
    case argType: Type[_] => TypeChecker.matchesVT(this, argType)
  }

}