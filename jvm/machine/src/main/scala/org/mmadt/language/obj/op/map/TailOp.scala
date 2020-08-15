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
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.obj.value.VInst

trait TailOp {
  this: Obj =>
  def tail(): this.type = TailOp[Obj]().exec(this).asInstanceOf[this.type]
}
object TailOp extends Func[Obj, Poly[Obj]] {
  def apply[A <: Obj](): Inst[Obj, Poly[A]] = new VInst[Obj, Poly[A]](g = (Tokens.tail, Nil), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Poly[Obj]]): Poly[Obj] = (start match {
    case apoly: Poly[_] if apoly.ctype => apoly
    case alst: Lst[Obj] => if (alst.isEmpty) throw LanguageException.PolyException.noTail
      alst.clone(_.tail)
    case arec: Rec[Obj, Obj] => if (arec.isEmpty) throw LanguageException.PolyException.noTail
      arec.clone(_.tail)
    case _ => start
  }).via(start, inst).asInstanceOf[Poly[Obj]]
}