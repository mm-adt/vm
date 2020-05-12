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

import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.value.{StrValue, Value}
import org.mmadt.language.obj.{Inst, Obj}
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
object FromOp extends Func[Obj, Obj] {
  def apply[O <: Obj](label: StrValue): Inst[Obj, Obj] = this.apply(label = label, default = obj)
  def apply[O <: Obj](label: StrValue, default: O): Inst[Obj, O] = new VInst[Obj, O](g = (Tokens.from, List(label, default)), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    val history: Option[Obj] = Obj.fetchOption[Obj](start, inst.arg0[StrValue].g)
    if (history.isEmpty && start.isInstanceOf[Value[_]])
      throw LanguageException.labelNotFound(start, inst.arg0[StrValue].g)
    history.getOrElse(if (inst.arg1.equals(obj)) asType(start) else inst.arg1).via(start, inst)
  }
}
