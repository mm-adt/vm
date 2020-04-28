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

import org.mmadt.language.LanguageFactory
import org.mmadt.language.obj.`type`.{Type, TypeChecker}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Obj, _}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Value[+V <: Obj] extends Obj {
  def value: Any

  // pattern matching methods
  override def test(other: Obj): Boolean = other match {
    case argValue: Value[_] => TypeChecker.matchesVV(this, argValue)
    case argType: Type[_] => TypeChecker.matchesVT(this, argType)
  }

  // standard Java implementations
  override def toString: String = LanguageFactory.printValue(this)
  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.value.hashCode()
  override def equals(other: Any): Boolean = other match {
    case astrm: Strm[V] => astrm.values
      .filter(x => !this.q(1).equals(x.q(1))) // cheesy
      .map(x => x.q)
      .fold(qZero)((a, b) => plusQ(a, b))
      .equals(this.q)
    case avalue: Value[V] => avalue.value.equals(this.value) //&& eqQ(this, avalue)
    case _ => false
  }
}
