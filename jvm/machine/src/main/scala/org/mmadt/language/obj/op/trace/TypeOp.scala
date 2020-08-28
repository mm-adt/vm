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
import org.mmadt.language.obj.Obj.Trace
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.obj.value.VInst

trait TypeOp[+A <: Obj] {
  this: Obj =>
  def `type`(): Type[A] = TypeOp().exec(this).asInstanceOf[Type[A]]
}
object TypeOp extends Func[Obj, Type[Obj]] {
  override val preArgs: Boolean = false
  override val preStrm: Boolean = false
  def apply[A <: Obj](): Inst[A, Type[A]] = new VInst[A, Type[A]](g = (Tokens.`type`, Nil), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Type[Obj]]): Type[Obj] = start match {
    case atype: Type[_] => atype.via(start, inst)
    // use preArg instruction as that is the type instruction
    case _ => start.trace.map(x => (x._1, x._2.via._1)).asInstanceOf[Trace].reconstruct(start.domain)
  }
}