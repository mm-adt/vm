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
import org.mmadt.language.obj.value.IntValue
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Inst, Int, Obj}
import org.mmadt.storage.StorageFactory.{int, qOne}
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait SumOp {
  this: Obj =>
  def sum(): Int = SumOp().exec(this)
}

object SumOp extends Func[Obj, Obj] {
  override val preArgs: Boolean = false
  override val preStrm: Boolean = false

  def apply(): Inst[Obj, Int] = new VInst[Obj, Int](g = (Tokens.sum, Nil), func = this) with ReduceInstruction[Int] with TraceInstruction {
    val seed: Int = int(0)
    val reducer: Int = __.to("x").get(0).plus(__.from("x").get(1).mult(__.quant())).as(int)
  }

  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    start match {
      case strm: Strm[Int] => strm.values.foldLeft(int(0))((x, y) => x + int(y.g * y.q._1.g)).clone(q = qOne, via = (start, inst))
      case avalue: IntValue => int(avalue.g * avalue.q._1.g).clone(q = qOne, via = (start, inst))
      case _: Type[_] => int.via(start, inst).hardQ(qOne)
    }
  }
}