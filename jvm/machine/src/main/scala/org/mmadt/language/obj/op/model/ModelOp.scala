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
import org.mmadt.language.model.rewrite.LeftRightSweepRewrite
import org.mmadt.language.obj.`type`.{RecType, Type}
import org.mmadt.language.obj.op.model.ModelOp.{ModelInst, ModelT}
import org.mmadt.language.obj.{Inst, IntQ, Obj}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ModelOp {
  this: Obj =>
  def model[E <: Obj](model: ModelT): E = this match {
    case atype: Type[_] => new ModelInst[Obj, E](model).exec(atype)
    case other: E => Model.from(model).apply(other)
  }
}

object ModelOp {
  private type ModelT = RecType[Type[Obj], Type[Obj]]
  def apply[S <: Obj, E <: Obj](model: ModelT): Inst[S, E] = new ModelInst[S, E](model)

  class ModelInst[S <: Obj, E <: Obj](model: ModelT, q: IntQ = qOne) extends VInst[S, E]((Tokens.model, List(model)), q) {
    override def q(quantifier: IntQ): this.type = new ModelInst[S, E](model, quantifier).asInstanceOf[this.type]
    val m: Model = Model.from(model)
    override def exec(start: S): E = {
      LeftRightSweepRewrite.rewrite(m,
        start.asInstanceOf[Type[Obj]],
        start.asInstanceOf[Type[Obj]].range,
        str).asInstanceOf[E]
    }
  }

}