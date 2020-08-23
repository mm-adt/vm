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
import org.mmadt.language.obj._
import org.mmadt.storage.StorageFactory.zeroObj
import org.mmadt.storage.obj.value.VInst

trait TailOp[+A <: Obj] {
  this: Obj =>
  def tail: this.type = TailOp[Obj]().exec(this).asInstanceOf[this.type]
}
object TailOp extends Func[Obj, Obj] {
  def apply[A <: Obj](): Inst[Obj, Obj] = new VInst[Obj, Obj](g = (Tokens.tail, Nil), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = (start match {
    case apoly: Poly[_] if apoly.ctype => apoly
    case alst: Lst[Obj] => if (alst.isEmpty) zeroObj else alst.clone(_.tail) //throw LanguageException.PolyException.noTail
    case arec: Rec[Obj, Obj] => if (arec.isEmpty) zeroObj else arec.clone(_.tail) // throw LanguageException.PolyException.noTail
    case _ => start
  }).via(start, inst)
}