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

import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait SplitOp {
  this: Obj =>
  def split[A <: Obj](branches: Lst[A]): Lst[A] = SplitOp(branches).exec(this.asInstanceOf[A])
  final def -<[A <: Obj](branches: Lst[A]): Lst[A] = this.split(branches)
}

object SplitOp {
  def apply[A <: Obj](branches: Lst[A]): SplitInst[A] = new SplitInst[A](branches)

  class SplitInst[A <: Obj](apoly: Lst[A], q: IntQ = qOne) extends VInst[A, Lst[A]]((Tokens.split, List(apoly)), q) with BranchInstruction {
    override def q(q: IntQ): this.type = new SplitInst[A](apoly, q).asInstanceOf[this.type]
    override def exec(start: A): Lst[A] = {
      apoly.connective match {
        case Tokens.:/ | Tokens.:\ => processAll(start)
        case Tokens.:| => processFirst(start)
        case _ => throw new LanguageException("Unknown poly connective: " + start)
      }
    }

    private def processAll(start: A): Lst[A] = {
      val inst = new SplitInst[A](Lst.resolveSlots(start, apoly, this))
      start match {
        case astrm: Strm[A] => astrm.via(start, this).asInstanceOf[Lst[A]]
        case _ => inst.arg0[Lst[A]]().clone(via = (start, inst))
      }
    }

    private def processFirst(start: A): Lst[A] = {
      val inst = new SplitInst[A](Lst.resolveSlots(start, apoly, this))
      (start match {
        case astrm: Strm[A] => return astrm.via(start, inst).asInstanceOf[Lst[A]]
        case _: Type[_] => inst.arg0[Lst[A]]()
        case _ => Lst.keepFirst(inst.arg0[Lst[A]]())
      }).clone(via = (start, inst))
    }
  }

}