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
import org.mmadt.language.obj.`type`.{RecType, Type}
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait BranchOp {
  this: Obj =>
  def branch[IT <: Obj, OT <: Obj](branches: (IT, OT)*): OT = this.branch(trec(value = branches.toMap))
  def branch[IT <: Obj, OT <: Obj](branches: RecType[IT, OT], start: IT = this.asInstanceOf[IT]): OT = {
    start match {
      case atype: Type[IT] with IT =>
        val branchTypes: RecType[IT, OT] = BranchInstruction.typeInternal(atype.range, branches) // composed branches given the incoming type
        val rangeType: OT = BranchInstruction.typeExternal[OT](parallel = true, branchTypes)
        rangeType.via(this, BranchOp[IT, OT](branchTypes)).asInstanceOf[OType[OT]].hardQ(rangeType.q)
      case _: Value[IT] with IT =>
        strm[OT](branches.value().filter(p => p._1 match {
          case btype: Type[IT] with IT => start.compute(btype).alive()
          case bvalue: Value[IT] with IT => start.test(bvalue)
        }).values.map {
          case btype: Type[OT] with OT => start.compute(btype)
          case bvalue: Value[OT] with OT => bvalue.q(start.q)
        }.toList.iterator)
    }
  }
}

object BranchOp {
  def apply[IT <: Obj, OT <: Obj](branches: RecType[IT, OT]): BranchInst[IT, OT] = new BranchInst(branches)

  class BranchInst[IT <: Obj, OT <: Obj](branches: RecType[IT, OT], q: IntQ = qOne) extends VInst[IT, OT]((Tokens.branch, List(branches)), q) with BranchInstruction {
    override def q(quantifier: IntQ): this.type = new BranchInst[IT, OT](branches, quantifier).asInstanceOf[this.type]
    override def exec(start: IT): OT = start.branch(branches, start)
  }

}
