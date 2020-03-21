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

package org.mmadt.language.obj.op.model

import org.mmadt.language.Tokens
import org.mmadt.language.model.Model
import org.mmadt.language.obj.`type`.{RecType, Type}
import org.mmadt.language.obj.op.model.ModelOp.ModelT
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.processor.Traverser
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ModelOp {
  this:Obj =>

  def model[E <: Obj](model:ModelT):E = this match {
    case atype:Type[_] => atype.compose(this,ModelOp(model)).asInstanceOf[E]
    case other:E => Model.from(model).apply(other)
  }
}

object ModelOp {
  private type ModelT = RecType[Type[Obj],Type[Obj]]
  def apply[S <: Obj,E <: Obj](model:ModelT):Inst[S,E] = new ModelInst[S,E](model)

  class ModelInst[S <: Obj,E <: Obj](model:ModelT) extends VInst[S,E]((Tokens.model,List(model))) {
    override def apply(trav:Traverser[S]):Traverser[E] = trav.split(trav.obj().model(arg0()).asInstanceOf[E])
  }

}