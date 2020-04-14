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

package org.mmadt.language.obj.op.map

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait AOp {
  this: Obj =>
  def a(other: Obj): Bool = (this match {
    case _: Value[_] => bool(this.test(other))
    case _: Type[_] => bool
  }).via(this, AOp(other))
}

object AOp {
  def apply(other: Obj): AInst = new AInst(other)

  class AInst(other: Obj, q: IntQ = qOne) extends VInst[Obj, Bool]((Tokens.a, List(other)), q) {
    override def q(quantifier: IntQ): this.type = new AInst(other, quantifier).asInstanceOf[this.type]
    override def exec(start: Obj): Bool = start.a(other).via(start, this)
  }

}