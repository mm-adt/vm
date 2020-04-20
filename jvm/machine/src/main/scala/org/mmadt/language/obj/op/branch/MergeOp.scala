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
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.branch.{Branching, Prod}
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.{IntQ, Obj, Str}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait MergeOp[A <: Obj] {
  this: Branching[A] =>
  def merge(): A =
    (if (this.value.filter(x => x.alive()).exists(x => x.isInstanceOf[Type[Obj]])) {
      val rangeType = BranchInstruction.typeExternal(this.isInstanceOf[Prod[A]], trec(value = this.value.map(x => (str(x.toString), x)).toMap[Str, Obj]))
      rangeType.via(this, MergeOp()).hardQ(rangeType.q)
    } else strm(this.value.filter(x => x.alive()).map(x=>x.via(this, MergeOp())).flatMap(x => x.toList).toIterator)).asInstanceOf[A]
}

object MergeOp {
  def apply[A <: Obj](): MergeInst[A] = new MergeInst[A]()

  class MergeInst[A <: Obj](q: IntQ = qOne) extends VInst[Branching[A], A]((Tokens.merge, Nil), q) with BranchInstruction {
    override def q(q: IntQ): this.type = new MergeInst[A](q).asInstanceOf[this.type]
    override def exec(start: Branching[A]): A = start.merge().via(start, this)
  }

}
