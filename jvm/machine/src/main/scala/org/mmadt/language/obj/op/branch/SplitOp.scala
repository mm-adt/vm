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

package org.mmadt.language.obj.op.branch

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait SplitOp {
  this: Obj =>
  def split[A <: Obj](brch: Poly[A]): Poly[A] = SplitOp(brch).exec(this.asInstanceOf[A])
  final def -<[A <: Obj](brch: Poly[A]): Poly[A] = this.split(brch)
}

object SplitOp {
  def apply[A <: Obj](branches: Poly[A]): SplitInst[A] = new SplitInst[A](branches)

  class SplitInst[A <: Obj](apoly: Poly[A], q: IntQ = qOne) extends VInst[A, Poly[A]]((Tokens.split, List(apoly)), q) with BranchInstruction {
    override def q(q: IntQ): this.type = new SplitInst[A](apoly, q).asInstanceOf[this.type]
    override def exec(start: A): Poly[A] = {
      start match {
        case astrm: Strm[A] => strm(astrm.values.map(x => apoly.clone(apoly.ground._2.map(y => Inst.resolveArg(x, y)).filter(_.alive)))).clone(via = (start, this))
        case _ => apoly.clone(apoly.ground._2.map(x => Inst.resolveArg(start, x))).clone(via = (start, this))
      }
    }
  }

}
