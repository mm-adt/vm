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
import org.mmadt.storage.obj.`type`.TObj
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
        val newBranches:Traverser[RecType[IT,OT]] = applyRec(trav.split(atype.range),branches) // composed branches given the incoming type
        val rangeType  :OT                        = generalType[OT](newBranches.obj().value().values)
        atype.compose[OT](rangeType,BranchOp[IT,OT](newBranches.obj())).asInstanceOf[Type[Obj]].hardQ(minZero(branches.value().values.map(x=>x.q).reduce((a,b)=>plusQ(a,b)))).asInstanceOf[OT]
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

  private def applyRec[IT <: Obj,OT <: Obj](current:Traverser[Type[IT] with IT],branches:RecType[IT,OT]):Traverser[RecType[IT,OT]] ={
    current.split(
      trec(value = branches.value().map(x => (x._1 match {
        case atype:Type[IT] with IT => current.obj().compose(atype).asInstanceOf[IT]
        case avalue:Value[IT] with IT => avalue
      },x._2 match {
        case atype:Type[OT] with OT => current.obj().compose(atype).asInstanceOf[OT]
        case avalue:Value[OT] with OT => avalue
      }))))
  }

  private def generalType[OT <: Obj](outs:Iterable[OT]):OT ={ // TODO: record introspection for type generalization
    val types = outs.map{
      case atype:Type[Obj] => atype.range.asInstanceOf[OT]
      case avalue:OT => avalue
    }.filter(x => x.alive())
    (types.toSet.size match {
      case 1 => types.head
      case _ => new TObj().asInstanceOf[OT]
    }).q(0) // the quantification is the largest span of the all the branch ranges
  }
}

object BranchOp {
  def apply[IT <: Obj,OT <: Obj](branches:RecType[IT,OT]):Inst[IT,OT] = new BranchInst(branches)

  class BranchInst[IT <: Obj,OT <: Obj](branches:RecType[IT,OT]) extends VInst[IT,OT]((Tokens.branch,List(branches))) with BranchInstruction {
    override def apply(trav:Traverser[IT]):Traverser[OT] = trav.split(trav.obj().branch(branches,trav)) // TODO: do we maintain the OT branch states?
  }

}
