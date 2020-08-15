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
import org.mmadt.language.obj.{Bool, Inst, Int, Lst, Obj, Real, Rec, Str}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ZeroOp[O <: Obj] {
  this: O =>
  def zero(): this.type = ZeroOp().exec(this)
}
object ZeroOp extends Func[Obj, Obj] {
  def apply[O <: Obj](): Inst[O, O] = new VInst[O, O](g = (Tokens.zero, Nil), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = (start match {
    case _: Bool => bfalse
    case _: Int => int(0)
    case _: Real => real(0.0)
    case _: Str => str(Tokens.empty)
    case arec: Rec[Obj, Obj] => arec.clone(_ => List.empty)
    case alst: Lst[Obj] => alst.clone(_ => List.empty)
    case _ => start
  }).via(start, inst)
}