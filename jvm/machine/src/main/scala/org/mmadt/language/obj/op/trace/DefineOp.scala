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
import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait DefineOp {
  this: Obj =>
  def define(objs: Obj*): this.type = DefineOp(objs: _*).exec(this)
}
object DefineOp extends Func[Obj, Obj] {
  def apply[O <: Obj](objs: Obj*): Inst[O, O] = new VInst[O, O](g = (Tokens.define, objs.toList.asInstanceOf[List[O]]), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    val undefined = inst.args.filter(x => !Obj.fetch(start, x))
    if (undefined.isEmpty) start else start.via(start, inst.clone(g = (Tokens.define, undefined)))
    // if (!Obj.fetch(start, inst.arg0[Obj])) start.via(start, inst) else start
  }
}