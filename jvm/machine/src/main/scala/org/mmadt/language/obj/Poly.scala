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

package org.mmadt.language.obj

import org.mmadt.language.Tokens
import org.mmadt.language.obj.op.branch.MergeOp
import org.mmadt.language.obj.op.map.{HeadOp, LastOp, TailOp}

trait Poly[+A <: Obj] extends Obj
  with HeadOp[Obj]
  with TailOp
  with LastOp[Obj]
  with MergeOp[A] {
  def gsep: String
  def glist: Seq[A]
  def isSerial: Boolean = this.gsep == Tokens.`;`
  def isParallel: Boolean = this.gsep == Tokens.`,`
  def isChoice: Boolean = this.gsep == Tokens.|
  def isPlus: Boolean = this.isParallel | this.isChoice
  def isEmpty: Boolean = this.glist.isEmpty
  def size: scala.Int = this.glist.size
  def ctype: Boolean
}

object Poly {
  def resolveSlots[A <: Obj](start: A, apoly: Poly[A]): Poly[A] = {
    apoly match {
      case arec: Rec[Obj, A] => arec.clone(x => Rec.moduleStruct(start, arec.gsep, x))
      case alst: Lst[A] => alst.clone(x => Lst.moduleStruct(start, alst.gsep, x))
    }
  }
  def keepFirst[A <: Obj](start: Obj, apoly: Poly[A]): Poly[A] = {
    apoly match {
      case arec: Rec[Obj, A] => Rec.keepFirst(start, arec)
      case alst: Lst[A] => Lst.keepFirst(alst)
    }
  }
  def sameSep(apoly: Poly[_], bpoly: Poly[_]): Boolean = (apoly.size < 2 || bpoly.size < 2) ||
    (apoly.isChoice == bpoly.isChoice && apoly.isParallel == bpoly.isParallel && apoly.isSerial == bpoly.isSerial)
}
