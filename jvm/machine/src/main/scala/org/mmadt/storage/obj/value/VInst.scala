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

package org.mmadt.storage.obj.value

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj._
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.op.map.IdOp
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VInst[S <: Obj, E <: Obj](val name: String = Tokens.inst, val g: LstTuple[S], val q: IntQ = qOne, val via: ViaTuple = base, val func: Func[_ <: Obj, _ <: Obj] = null) extends Inst[S, E] {
  override def clone(name: String = this.name,
                     g: Any = this.g,
                     q: IntQ = this.q,
                     via: ViaTuple = this.via): this.type = new VInst[S, E](name, g.asInstanceOf[LstTuple[S]], q, via, this.func).asInstanceOf[this.type]
  override def exec(start: S): E =
    this match {
      case _: TraceInstruction => this.func.asInstanceOf[Func[S, E]](start, this)
      case _ => this.func.asInstanceOf[Func[S, E]](start, this.clone(g = (this.op, this.args.map(arg => Inst.resolveArg(start, arg)))).via(this, IdOp()))
    }
}


