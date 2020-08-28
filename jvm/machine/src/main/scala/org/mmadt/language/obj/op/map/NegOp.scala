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
import org.mmadt.language.obj._
import org.mmadt.language.obj.value.{BoolValue, IntValue, RealValue, Value}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait NegOp[O <: Obj] {
  this: O =>
  def neg: this.type = NegOp().exec(this)
  final def unary_-(): this.type = this.neg
}
object NegOp extends Func[Obj, Obj] {
  def apply[A <: Obj](): Inst[A, A] = new VInst[A, A](g = (Tokens.neg, Nil), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = (start match {
    case aint: IntValue => start.clone(g = -aint.g)
    case areal: RealValue => start.clone(g = -areal.g)
    case abool: BoolValue => start.clone(g = !abool.g)
    case _: Value[_] => throw LanguageException.unsupportedInstType(start, inst)
    case _ => start
  }).via(start, inst)
}