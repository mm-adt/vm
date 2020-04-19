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

package org.mmadt.language.obj.op.initial

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.InitialInstruction
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, IntQ, OType, Obj}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait StartOp {
  this: Obj =>
  def start[O <: Obj](): O with Type[O] = (this match {
    case _: Type[_] => this
    case _: Value[_] => asType(this).via(obj.q(0), StartOp(this)).hardQ(this.q)
  }).asInstanceOf[OType[O]]
}

object StartOp {
  def apply[O <: Obj](starts: O): Inst[O, O] = new StartInst(starts)

  class StartInst[O <: Obj](starts: O, q: IntQ = qOne) extends VInst[O, O]((Tokens.start, List(starts)), q) with InitialInstruction {
    override def q(quantifier: IntQ): this.type = new StartInst[O](starts, quantifier).asInstanceOf[this.type]
    override def exec(start: O): O = start match {
      case _: Type[_] => asType(starts).via(obj.q(0), StartOp(starts)).hardQ(starts.q)
      case _ => starts
    }
  }

}
