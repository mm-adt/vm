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
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait BranchOp {
  this:Obj =>

  def branch[IT <: Obj,OT <: Obj](branches:(IT,OT)*):OT = this.branch(trec(value = branches.toMap))

  def branch[IT <: Obj,OT <: Obj](branches:RecType[IT,OT],trav:Traverser[IT] = Traverser.standard(this.asInstanceOf[IT])):OT ={
    trav.obj() match {
      case atype:Type[IT] with IT =>
        val newBranches:Traverser[RecType[IT,OT]] = BranchInstruction.applyRec(trav.split(atype.range),branches) // composed branches given the incoming type
        val rangeType  :OT                        = BranchInstruction.generalType[OT](newBranches.obj().value().values)
        atype.compose[OT](rangeType,BranchOp[IT,OT](newBranches.obj())).asInstanceOf[Type[Obj]].hardQ(minZero(branches.value().values.map(x => x.q).reduce((a,b) => plusQ(a,b)))).asInstanceOf[OT]
      case avalue:Value[IT] with IT =>
        strm[OT](branches.value().filter(p => p._1 match {
          case btype:Type[IT] with IT => trav.apply(btype).obj().alive()
          case bvalue:Value[IT] with IT => avalue.test(bvalue)
        }).values.map{
          case btype:Type[OT] with OT => trav.apply(btype).obj()
          case bvalue:Value[OT] with OT => bvalue.q(avalue.q)
        }.toList.iterator)
    }
  }
}

object BranchOp {
  def apply[IT <: Obj,OT <: Obj](branches:RecType[IT,OT]):BranchInst[IT,OT] = new BranchInst(branches)

  class BranchInst[IT <: Obj,OT <: Obj](branches:RecType[IT,OT]) extends VInst[IT,OT]((Tokens.branch,List(branches))) with BranchInstruction {
    override def apply(trav:Traverser[IT]):Traverser[OT] = trav.split(trav.obj().branch(branches,trav)) // TODO: do we maintain the OT branch states?
  }

}
