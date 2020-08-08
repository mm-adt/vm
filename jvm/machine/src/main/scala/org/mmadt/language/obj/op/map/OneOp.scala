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

import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Bool, Inst, Int, Obj, Real}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait OneOp[O <: Obj] {
  this: O =>
  def one(): this.type = OneOp().exec(this)
}
object OneOp extends Func[Obj, Obj] {
  def apply[O <: Obj](): Inst[O, O] = new VInst[O, O](g = (Tokens.one, Nil), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = (start match {
    case _: Bool => btrue
    case _: Int => int(1)
    case _: Real => real(1.0)
    case _: Value[_] => throw LanguageException.unsupportedInstType(start, inst)
    case _ => start
  }).via(start, inst)
}