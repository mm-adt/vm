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

package org.mmadt.language.obj.`type`

import org.mmadt.language.LanguageFactory
import org.mmadt.language.obj.op.trace.ExplainOp
import org.mmadt.language.obj.{eqQ, _}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Type[+T <: Obj] extends Obj with ExplainOp {
  // type signature properties and functions
  //def value: Any = throw LanguageException.typesNoValue(this)
  override lazy val range: this.type = this.isolate

  // pattern matching methods
  override def test(other: Obj): Boolean = other match {
    case _: Obj if !other.alive => !this.alive
    case _: __ if __.isTokenRoot(other) =>
      val temp = Inst.resolveToken(this, other)
      if (temp == other) true else this.test(temp)
    case _: Type[_] => (sameBase(this, other.domain) || __.isAnon(this) || __.isAnonObj(other.domain)) && withinQ(this, other)
    case _ => false
  }

  // standard Java implementations
  override def toString: String = LanguageFactory.printType(this)

  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.q.hashCode() ^ this.trace.hashCode()

  override def equals(other: Any): Boolean = other match {
    case obj: Obj if !this.alive => !obj.alive
    case atype: Type[_] => atype.name.equals(this.name) && eqQ(atype, this) && this.via == atype.via
    case _ => false
  }
}
