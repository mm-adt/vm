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

import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.trace.TypeOp
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.{LanguageFactory, Tokens}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.strm.util.MultiSet

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Value[+V <: Obj] extends Obj with TypeOp[V] {
  def g: Any

  override def test(other: Obj): Boolean = other match {
    case aobj: Obj if !aobj.alive => !this.alive
    case anon: __ if __.isToken(anon) => this.test(Inst.resolveToken(this, anon))
    case anon: __ => withinQ(this, anon) && Inst.resolveArg(this, anon).alive
    case astrm: Strm[_] => MultiSet.test(this, astrm)
    case avalue: Value[_] => this.g.equals(avalue.g) && withinQ(this, avalue)
    case atype: Type[_] => (baseName(this).equals(baseName(atype)) || atype.name.equals(Tokens.obj) || atype.name.equals(Tokens.anon)) && withinQ(this, atype.domain) && this.compute(atype).alive
    case _ => false
  }

  // standard Java implementations
  override def toString: String = LanguageFactory.printValue(this)
  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.g.hashCode()
  override def equals(other: Any): Boolean = other match {
    case obj: Obj if !this.alive => !obj.alive
    case astrm: Strm[V] => MultiSet.test(astrm, this.toStrm)
    case avalue: Value[V] => this.name.equals(avalue.name) && this.g.equals(avalue.g) && eqQ(this, avalue)
    case _ => false
  }
}
