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

package org.mmadt.language.obj.op.reduce

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.{ReduceInstruction, TraceInstruction}
import org.mmadt.language.obj.value.{IntValue, Value}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Inst, Int, Obj}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait CountOp {
  this: Obj =>
  def count: Int = CountOp().exec(this)
}

object CountOp extends Func[Obj, Obj] {
  def apply(): Inst[Obj, Int] = new VInst[Obj, Int](g = (Tokens.count, Nil), func = this) with ReduceInstruction[Int] with TraceInstruction {
    val seed: Int = int(0)
    val reducer: Int = __.to("x").get(0).plus(__.from("x").get(1).quant()).as(int)
  }
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    start match {
      case _: __ => int.via(start,inst)
      case strm: Strm[_] => strm.values.map(x => x.q._1).foldLeft(int(0))((x, y) => x + y).clone(q = qOne, via = (start, inst))
      case avalue: Value[_] => int(0).plus(avalue.q._1).clone(q = qOne, via = (start, inst))
      case _: Type[_] => int.via(start, inst)
    }
  }
}