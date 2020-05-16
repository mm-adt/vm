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

import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.{Inst, Lst, Obj, Poly}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.obj.value.VInst

trait HeadOp[A <: Obj] {
  this: Poly[A] =>
  def head(): A = HeadOp().exec(this)
}
object HeadOp extends Func[Obj, Obj] {
  def apply[A <: Obj](): Inst[Poly[A], A] = new VInst[Poly[A], A](g = (Tokens.head, Nil), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    (start match {
      case alst: Lst[_] =>
        if (!alst.glist.exists(_.alive))
          throw new LanguageException("no head on empty poly")
        else
          alst.glist.filter(_.alive).head
      case _ => start
    }).via(start, inst)
  }
}