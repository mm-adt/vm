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
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, Obj, _}
import org.mmadt.storage.StorageFactory.zeroObj
import org.mmadt.storage.obj.value.VInst

trait JuxtOp {
  this: Obj =>
  def juxta[A <: Obj](right: A): A = JuxtOp(right).exec(this)
  def `=>`[A <: Obj](right: A): A = this.juxta(right)
}

object JuxtOp extends Func[Obj, Obj] {
  def apply[A <: Obj](right: A): Inst[Obj, A] = new VInst[Obj, A](g = (Tokens.juxt, List(right)), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = inst.arg0[Obj] match {
    case dead: Obj if !dead.alive => zeroObj
    case avalue: Value[_] => start.compute(avalue).hardQ(multQ(start.q, avalue.q))
    case atype: Type[_] => start.compute(atype).hardQ(multQ(start.q, atype.pureQ))
  }
}