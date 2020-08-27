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
import org.mmadt.language.obj.value.strm.Strm
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
    start match {
      case astrm: Strm[_] => astrm(x => inst.exec(x))
      case _ => inst.args.foldLeft(start)((a, b) => a.model(a.model.defining(b)))
    }
  }
}