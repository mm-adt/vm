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

import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.obj.value.VInst

trait SplitOp {
  this: Obj =>
  def split[A <: Obj](branches: Poly[A]): Poly[A] = SplitOp(branches).exec(this.asInstanceOf[A])
  final def -<[A <: Obj](branches: Poly[A]): Poly[A] = this.split(branches)
}

object SplitOp extends Func[Obj, Obj] {
  def apply[A <: Obj](branches: Obj): Inst[A, Poly[A]] = new VInst[A, Poly[A]](g = (Tokens.split, List(branches)), func = this) with BranchInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    val oldInst: Inst[Obj, Poly[Obj]] = Inst.oldInst(inst).asInstanceOf[Inst[Obj, Poly[Obj]]]
    val apoly: Poly[Obj] = oldInst.arg0[Obj] match {
      case x: Poly[Obj] => x
      case x => Inst.resolveArg[Obj, Obj](start, x).asInstanceOf[Poly[Obj]]

    }
    val newInst: Inst[Obj, Poly[Obj]] = SplitOp(Poly.resolveSlots(start, apoly, oldInst))
    apoly.gsep match {
      case _ if apoly.isChoice & apoly.isInstanceOf[Rec[Obj, Obj]] => processFirst(start, inst.asInstanceOf[Inst[Obj, Poly[Obj]]]) // TODO: why?
      case _ if apoly.isChoice => processFirst(start, newInst)
      case _ if apoly.isSerial || apoly.isParallel => processAll(start, newInst)
      case _ => throw new LanguageException("Unknown poly connective: " + start)
    }
  }

  private def processAll(start: Obj, inst: Inst[Obj, Poly[Obj]]): Poly[Obj] = {
    start match {
      case astrm: Strm[Obj] => astrm.via(start, inst).asInstanceOf[Lst[Obj]]
      case _ => inst.arg0[Poly[Obj]].clone(via = (start, inst))
    }
  }

  private def processFirst(start: Obj, inst: Inst[Obj, Poly[Obj]]): Poly[Obj] = {
    (start match {
      case astrm: Strm[Obj] => return astrm.via(start, inst).asInstanceOf[Poly[Obj]]
      case _: Type[_] => inst.arg0[Poly[Obj]]
      case _ => Poly.keepFirst(start, inst, inst.arg0[Poly[Obj]])
    }).clone(via = (start, inst))
  }
}