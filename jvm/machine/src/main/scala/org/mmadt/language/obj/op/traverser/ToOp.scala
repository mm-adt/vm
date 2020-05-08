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
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{IntQ, Obj}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ToOp {
  this: Obj =>
  def to(label: StrValue): this.type = ToOp(label).exec(this)
}

object ToOp {
  def apply[O <: Obj](label: StrValue): ToInst[O] = new ToInst(label)

  class ToInst[O <: Obj](label: StrValue, q: IntQ = qOne) extends VInst[O, O]((Tokens.to, List(label)), q) with TraceInstruction {
    override def q(q: IntQ): this.type = new ToInst[O](label, q).asInstanceOf[this.type]
    override def exec(start: O): O = start.via(start, this)
  }

}
