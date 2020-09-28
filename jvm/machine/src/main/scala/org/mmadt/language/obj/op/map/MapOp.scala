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

package org.mmadt.language.obj.op.map

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, Obj, _}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait MapOp {
  this: Obj =>
  def map[O <: Obj](other: O): O = MapOp(other).exec(this)
}
object MapOp extends Func[Obj, Obj] {
  def apply[O <: Obj](other: O): Inst[Obj, O] = new VInst[Obj, O](g = (Tokens.map, List(other)), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    val mapArg = inst.arg0[Obj]
    start match {
      case _: Value[_] => mapArg.via(start, inst).hardQ(q => Inst.oldInst(inst).arg0[Obj].q.mult(q))
      case _: Type[_] => asType(mapArg).via(start, inst.clone(_ => List(mapArg.hardQ(Inst.oldInst(inst).arg0[Obj].q))))
    }
  }
}
