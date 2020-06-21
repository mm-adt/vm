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

package org.mmadt.language.obj.op.sideeffect

import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.value.{StrValue, Value}
import org.mmadt.language.obj.{Inst, Obj, Poly}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ErrorOp {
  this: Obj =>
  def error(message: StrValue): this.type = ErrorOp(message).exec(this).asInstanceOf[this.type]
}
object ErrorOp extends Func[Obj, Obj] {
  def apply(message: StrValue): Inst[Obj, Obj] = new VInst[Obj, Obj](g = (Tokens.error, List(message)), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = start match {
    case _: Value[_] => throw LanguageException.typeError(start, inst.arg0[StrValue].g)
    case _ => start.via(start, inst)
  }
}