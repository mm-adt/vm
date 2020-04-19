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
import org.mmadt.language.obj.`type`.{RecType, Type}
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{IntQ, OType, Obj}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ChooseOp {
  this: Obj =>
  def choose[IT <: Obj, OT <: Obj](branches: (IT, OT)*): OT = this.choose(trec(value = branches.toMap))
  def choose[IT <: Obj, OT <: Obj](branches: RecType[IT, OT], start: IT = this.asInstanceOf[IT]): OT = {
    start match {
      case atype: Type[IT] with IT =>
        val branchTypes: RecType[IT, OT] = BranchInstruction.typeInternal(atype.range, branches)
        val rangeType: OT = BranchInstruction.typeExternal[OT](parallel = false, branchTypes)
        rangeType.via(this, ChooseOp[IT, OT](branchTypes)).asInstanceOf[OType[OT]].hardQ(rangeType.q)
      case _: Value[IT] with IT =>
        branches.value.find(p => p._1 match {
          case btype: Type[IT] with IT => start.compute(btype).alive()
          case bvalue: Value[IT] with IT => start.test(bvalue)
        }).map(_._2).getOrElse(start.q(qZero))
        match {
          case btype: Type[OT] with OT => start.compute(btype)
          case bvalue: Value[OT] with OT => bvalue.q(start.q)
        }
    }
  }
}

object ChooseOp {
  def apply[IT <: Obj, OT <: Obj](branches: RecType[IT, OT]): ChooseInst[IT, OT] = new ChooseInst(branches)

  class ChooseInst[IT <: Obj, OT <: Obj](branches: RecType[IT, OT], q: IntQ = qOne) extends VInst[IT, OT]((Tokens.choose, List(branches)), q) with BranchInstruction {
    override def q(quantifier: IntQ): this.type = new ChooseInst[IT, OT](branches, quantifier).asInstanceOf[this.type]
    override def exec(start: IT): OT = start.choose(branches, start)
  }

}
