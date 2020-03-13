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

package org.mmadt.language.obj.op.traverser

import org.mmadt.language.Tokens
import org.mmadt.language.obj.op.TraverserInstruction
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.processor.Traverser
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait FromOp {
  this:Obj =>
  def from[O <: Obj](label:String):O = this.from(str(label))
  def from[O <: Obj](label:String,default:Obj):O = this.from(str(label),default)
  def from[O <: Obj](label:StrValue):O = label.asInstanceOf[O] // TODO NO IMPL -- INST
  def from[O <: Obj](label:StrValue,default:Obj):O = default.asInstanceOf[O]
}

object FromOp {
  def apply(label:StrValue):Inst[Obj,Obj] = new FromInst[Obj](label)
  def apply[O <: Obj](label:StrValue,default:O):Inst[Obj,O] = new FromInst[O](label,default)

  class FromInst[O <: Obj](label:StrValue,default:O = null) extends VInst[Obj,O]((Tokens.from,List(label))) with TraverserInstruction {
    override def apply(trav:Traverser[Obj]):Traverser[O] ={
      trav.split(composeInstruction(
        if (null != default)
          trav.state.getOrElse(arg0[StrValue]().value(),default).asInstanceOf[O]
        else {
          trav.state.getOrElse(arg0[StrValue]().value(),asType(trav.obj())).asInstanceOf[O]
        }))
    }
  }

}
