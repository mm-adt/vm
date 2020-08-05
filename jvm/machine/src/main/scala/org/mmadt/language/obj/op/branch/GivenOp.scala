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
import org.mmadt.language.obj.Obj.IntQ
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait GivenOp {
  this: Obj =>
  def given[O <: Obj](other: O): O = GivenOp[O](other).exec(this)
  final def `-->`[O <: Obj](other: O): O = this.given[O](other)
}

object GivenOp {
  def apply[O <: Obj](other: O): Inst[Obj, O] = new GivenInst(other)
  class GivenInst[O <: Obj](other: O, q: IntQ = qOne) extends VInst[Obj, O](g = (Tokens.given, List(other)), q = q) with BranchInstruction {
    override def q(q: IntQ): this.type = new GivenInst[O](other, q).asInstanceOf[this.type]
    override def exec(start: Obj): O = {
      val rangeObj: O = Inst.resolveArg(lastBranch(start), other)
      val inst = new GivenInst[O](rangeObj, q)
      (start match {
        case _: Value[_] => rangeObj // TODO: look at split test and to/from in branch
        case _: Type[_] =>
          rangeObj match {
            // case _: Strm[_] => rangeObj
            case _: Type[_] => rangeObj
            case _: Value[_] => asType[O](rangeObj).map(rangeObj)
          }
      }).via(start, inst)
    }
  }

  @scala.annotation.tailrec
  private def lastBranch[O](obj: Obj): O = {
    if (obj.root) return obj.asInstanceOf[O]
    if (obj.via._2.isInstanceOf[BranchInstruction])
      return obj.asInstanceOf[O]
    lastBranch[O](obj.via._1)
  }

}
