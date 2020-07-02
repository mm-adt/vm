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

package org.mmadt.language.obj.op.filter

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.value.BoolValue
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait IsOp {
  this: Obj =>
  def is(anon: __): this.type = IsOp(anon).exec(this)
  def is(bool: Bool): this.type = IsOp(bool).exec(this)
}
object IsOp extends Func[Obj, Obj] {
  def apply[O <: Obj](other: Obj): Inst[O, O] = new VInst[O, O](g = (Tokens.is, List(other.asInstanceOf[O])), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = inst.arg0[Obj] match {
    case avalue: BoolValue => if (avalue.g) start.via(start, inst) else start.via(start, inst).hardQ(qZero)
    case _ => start.via(start, inst).hardQ(minZero(multQ(start, inst)))
  }
}
