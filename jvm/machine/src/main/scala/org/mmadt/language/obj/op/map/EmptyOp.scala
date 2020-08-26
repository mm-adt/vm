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

package org.mmadt.language.obj.op.map

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.{Bool, Inst, Obj, Poly}
import org.mmadt.storage.StorageFactory.bool
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait EmptyOp {
  this: Obj =>
  def empty: Bool = EmptyOp[this.type]().exec(this)
}
object EmptyOp extends Func[Obj, Bool] {
  def apply[A <: Obj](): Inst[A, Bool] = new VInst[A, Bool](g = (Tokens.empty, Nil), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Bool]): Bool = (start match {
    case apoly: Poly[_] if apoly.ctype => bool
    case apoly: Poly[_] => bool(apoly.isEmpty)
    case _: __ => bool
  }).via(start, inst)
}
