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

import org.mmadt.language.obj.`type`.TypeChecker
import org.mmadt.language.obj.{OType, OValue, Obj}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Value[V <: Value[V]] extends Obj {

  def value():Any
  def start():OType

  override def map[O <: Obj](other:O):O = other match {
    case _:OValue => other
    case atype:OType with O => (this ==> atype).asInstanceOf[O]
  }

  override def id():this.type = this
  override def from[O <: Obj](label:StrValue):O = this.start().from(label)

  override def equals(other:Any):Boolean = other match {
    case avalue:OValue => avalue.value() == this.value()
    case _ => false
  }

  // pattern matching methods
  override def test(other:Obj):Boolean = other match {
    case argValue:OValue => TypeChecker.matchesVV(this,argValue)
    case argType:OType => TypeChecker.matchesVT(this,argType)
  }

}
