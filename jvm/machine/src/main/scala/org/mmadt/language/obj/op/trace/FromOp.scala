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

package org.mmadt.language.obj.op.trace

import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.value.{StrValue, Value}
import org.mmadt.language.obj.{IntQ, Obj}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait FromOp {
  this: Obj =>
  def from(label: StrValue): this.type = this.from[this.type](label, asType(this))
  def from[O <: Obj](label: StrValue, atype: O): O = FromOp(label, atype).exec(this)
}

object FromOp {
  def apply(label: StrValue): FromInst[Obj] = new FromInst[Obj](label)
  def apply[O <: Obj](label: StrValue, default: O): FromInst[O] = new FromInst[O](label, default)

  class FromInst[O <: Obj](label: StrValue, default: O = null, q: IntQ = qOne) extends VInst[Obj, O]((Tokens.from, List(label)), q) with TraceInstruction {
    override def q(q: IntQ): this.type = new FromInst[O](label, default, q).asInstanceOf[this.type]
    override def exec(start: Obj): O = {
      val history: Option[O] = Obj.fetchOption[O](start, label.ground)
      if (history.isEmpty && start.isInstanceOf[Value[_]])
        throw LanguageException.labelNotFound(start, label.ground)
      history.getOrElse(if (null == default) asType(start).asInstanceOf[O] else default).via(start, this)
    }
  }

}
