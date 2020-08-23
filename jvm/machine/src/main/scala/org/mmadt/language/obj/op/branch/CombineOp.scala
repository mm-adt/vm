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

package org.mmadt.language.obj.op.branch

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj._
import org.mmadt.language.obj.op.{BranchInstruction, TraceInstruction}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.obj.value.VInst

trait CombineOp[+A <: Obj] {
  this: Poly[A] =>
  def combine[B <: Obj](other: Poly[B]): this.type = CombineOp[A, B](other).exec(this).asInstanceOf[this.type]
  final def :=[B <: Obj](other: Poly[B]): this.type = this.combine[B](other)
}

object CombineOp extends Func[Obj, Obj] {
  def apply[A <: Obj, B <: Obj](other: Obj): Inst[Obj, Obj] = new VInst[Obj, Obj](g = (Tokens.combine, List(other)), func = this) with BranchInstruction with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    (start match {
      case astrm: Strm[Obj] => astrm
      case alst: Poly[Obj] if !alst.ctype => combineAlgorithm(alst, inst.arg0[Poly[Obj]]).via(start, inst)
      case alst: Poly[Obj] => alst
      case _ => start
    }).via(start, inst)
  }

  def combineAlgorithm(apoly: Poly[Obj], bpoly: Poly[Obj]): Poly[Obj] = {
    apoly match {
      case arec: Rec[Obj, Obj] =>
        val argList: Rec.Pairs[Obj, Obj] = bpoly.asInstanceOf[Rec[Obj, Obj]].gmap
        val argSize = argList.size
        var i = 0
        var newList: Rec.Pairs[Obj, Obj] = List.empty[(Obj, Obj)]
        val newSep: String = /*if (argSize < 2) apoly.gsep else*/ bpoly.gsep
        if (argSize > 0) {
          for (x <- arec.gmap) {
            newList = newList :+ (x._1 ~~> argList(i)._1, x._2 ~~> argList(i)._2)
            i = (i + 1) % argSize
          }
        }
        arec.clone(g = (newSep, newList))
      case alst: Lst[Obj] =>
        val argList: Seq[Obj] = bpoly.asInstanceOf[Lst[Obj]].glist
        val argSize = argList.size
        var i = 0
        var newList: List[Obj] = List.empty[Obj]
        val newSep: String = /*if (argSize < 2) apoly.gsep else*/ bpoly.gsep
        if (argSize > 0) {
          for (x <- alst.glist) {
            newList = newList :+ (x ~~> argList(i))
            i = (i + 1) % argSize
          }
        }
        alst.clone(g = (newSep, newList))
    }


  }
}

