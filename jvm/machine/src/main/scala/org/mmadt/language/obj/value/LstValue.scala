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
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.{Inst, Lst, Obj, withinQ}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait LstValue[A <: Obj] extends PolyValue[A, Lst[A]] with Lst[A] {
  override def test(other: Obj): Boolean = other match {
    case _: Obj if !other.alive => !this.alive
    case _: __ if __.isToken(other) => this.test(Inst.resolveToken(this, other))
    case alst: LstValue[A] => Lst.test(this, alst)
    case _: Type[_] => withinQ(this, other.domain) && (other.domain match {
      case alst: Lst[A] => Lst.test(this, alst)
      case x => __.isAnonObj(x)
    }) && this.compute(other).alive
    case _ => false
  }
  override def equals(other: Any): Boolean = other.isInstanceOf[LstValue[_]] && super[Lst].equals(other) && super[PolyValue].equals(other)
}