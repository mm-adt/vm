/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language.obj.op.trace

import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.value.strm.Strm
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
  def from(label: StrValue): this.type = FromOp(label).exec(this).asInstanceOf[this.type]
  def from[O <: Obj](label: StrValue, atype: O): O = FromOp(label, atype).exec(this)
}
object FromOp extends Func[Obj, Obj] {
  def apply[O <: Obj](label: StrValue): Inst[Obj, Obj] = new VInst[Obj, O](g = (Tokens.from, List(label)), func = this) with TraceInstruction
  def apply[O <: Obj](label: StrValue, default: O): Inst[Obj, O] = new VInst[Obj, O](g = (Tokens.from, List(label, default)), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    if (start.isInstanceOf[Strm[_]]) start.via(start, inst)
    else Obj.fetch[Obj](start, __,inst.arg0[StrValue].g).filter(x => Tokens.to == x._1).map(x => x._2).getOrElse(
      start match {
        case _: Type[_] => inst.args.tail.headOption.getOrElse(asType(start))
        case _: Value[_] => inst.args.tail.headOption.map(x => Inst.resolveArg(start, x)).getOrElse(throw LanguageException.labelNotFound(start.path(PathOp.VERTICES), inst.arg0[StrValue].g))
      }).via(start, inst)
  }
}
