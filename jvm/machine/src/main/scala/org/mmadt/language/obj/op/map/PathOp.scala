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
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait PathOp {
  this: Obj =>
  def path(): Lst[Obj] = PathOp().exec(this)
}
object PathOp extends Func[Obj, Lst[Obj]] {
  def apply(): Inst[Obj, Lst[Obj]] = new VInst[Obj, Lst[Obj]](g = (Tokens.path, Nil), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Lst[Obj]]): Lst[Obj] = {
    (start match {
      case _: Strm[_] => start
      case _ => lst(Tokens.`;`, start.trace.foldRight(List.empty[Obj])((a, b) => a._1 +: b) :+ start: _*)
    }).via(start, inst).asInstanceOf[Lst[Obj]]
  }
}