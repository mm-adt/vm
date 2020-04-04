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
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ChooseOp {
  this:Obj =>

  def choose[IT <: Obj,OT <: Obj](branches:(IT,OT)*):OT = this.choose(trec(value = branches.toMap))

  def choose[IT <: Obj,OT <: Obj](branches:RecType[IT,OT],trav:Traverser[IT] = Traverser.standard(this.asInstanceOf[IT])):OT ={
    trav.obj() match {
      case atype:Type[IT] with IT =>
        val newBranches:Traverser[RecType[IT,OT]] = BranchInstruction.applyRec(trav.split(atype.range),branches) // composed branches given the incoming type
        val rangeType  :OT                        = BranchInstruction.generalType[OT](newBranches.obj().value().values)
        atype.compose[OT](rangeType,ChooseOp[IT,OT](newBranches.obj()))
      case avalue:Value[IT] with IT =>
        branches.value().find(p => p._1 match {
          case btype:Type[IT] with IT => trav.apply(btype).obj().alive()
          case bvalue:Value[IT] with IT => avalue.test(bvalue)
        }).map(_._2).getOrElse(avalue.q(qZero))
        match {
          case btype:Type[OT] with OT => trav.apply(btype).obj()
          case bvalue:Value[OT] with OT => bvalue.q(avalue.q)
        }
    }
  }
}

object ChooseOp {
  def apply[IT <: Obj,OT <: Obj](branches:RecType[IT,OT]):Inst[IT,OT] = new ChooseInst(branches)

  class ChooseInst[IT <: Obj,OT <: Obj](branches:RecType[IT,OT]) extends VInst[IT,OT]((Tokens.choose,List(branches))) with BranchInstruction {
    override def apply(trav:Traverser[IT]):Traverser[OT] = trav.split(trav.obj().choose(branches,trav)) // TODO: do we maintain the OT branch states?
  }

}
