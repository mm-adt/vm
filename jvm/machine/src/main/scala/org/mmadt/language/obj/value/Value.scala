/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language.obj.value

import org.mmadt.language.LanguageFactory
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.trace.TypeOp
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.strm.util.MultiSet

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Value[+V <: Obj] extends Obj with TypeOp[V] {
  def g: Any

  override def test(other: Obj): Boolean = other match {
    case _: Obj if !other.alive => !this.alive
    case _: __ if __.isToken(other) => this.test(Inst.resolveToken(this, other))
    case _: Type[_] => (baseName(this).equals(baseName(other.domain)) || __.isAnonObj(other)) && withinQ(this, other.domain) && this.compute(other).alive
    case avalue: Value[_] => this.g.equals(avalue.g) && withinQ(this, avalue)
    case _ => false
  }

  // standard Java implementations
  override def toString: String = LanguageFactory.printValue(this)
  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.g.hashCode()
  override def equals(other: Any): Boolean = other match {
    case obj: Obj if !this.alive => !obj.alive
    case astrm: Strm[_] => MultiSet.equals(this, astrm)
    case avalue: Value[_] => this.isInstanceOf[PolyValue[_, _]] || (this.name.equals(avalue.name) && this.g.equals(avalue.g) && eqQ(this, avalue))
    case _ => false
  }
}
