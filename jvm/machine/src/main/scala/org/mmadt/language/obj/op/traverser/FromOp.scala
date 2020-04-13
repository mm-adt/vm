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

import org.mmadt.language.obj.op.TraverserInstruction
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
  def from(label: StrValue): this.type = {
    this.from[this.type](label, asType(this))
  }
  def from[O <: Obj](label: StrValue, atype: O): O = {
    val history: Option[O] = Obj.fetchOption[O](this, label.value)
      .map(x => x.via(this.via._1, this.via._2))
      .map(x => x.via(x, FromOp(label, atype)))
    if (history.isEmpty && this.isInstanceOf[Value[_]])
      throw LanguageException.labelNotFound(this, label.value)
    history.getOrElse(atype.via(atype, FromOp(label, atype)))
  }
}

object FromOp {
  def apply(label: StrValue): FromInst[Obj] = new FromInst[Obj](label)
  def apply[O <: Obj](label: StrValue, default: O): FromInst[O] = new FromInst[O](label, default)

  class FromInst[O <: Obj](label: StrValue, default: O = null, q: IntQ = qOne) extends VInst[Obj, O]((Tokens.from, List(label)), q) with TraverserInstruction {
    override def q(quantifier: IntQ): this.type = new FromInst[O](label, default, quantifier).asInstanceOf[this.type]
    override def exec(start: Obj): O = start.from(label, if (null == default) start else default).via(start, this).asInstanceOf[O]
  }

}
