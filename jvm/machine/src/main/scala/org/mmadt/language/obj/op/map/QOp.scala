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
import org.mmadt.language.obj.Obj.IntQ
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, Int, Obj, divQ, minusQ, multQ, plusQ}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait QOp {
  this:Obj =>
  def quant():Int = QOp().exec(this)
}

object QOp extends Func[Obj, Int] {
  def apply():Inst[Obj, Int] = new VInst[Obj, Int](g = (Tokens.q, Nil), func = this)
  override def apply(start:Obj, inst:Inst[Obj, Int]):Int = (start match {
    case _:Value[_] => start.q._1.q(qOne)
    case _ => int
  }).via(start, inst).asInstanceOf[Int]

  @inline implicit def qToRichQ(baseQ:IntQ):RichQ = new RichQ(baseQ)
  class RichQ(val richQ:IntQ) {
    def plus(otherQ:IntQ):IntQ = plusQ(richQ, otherQ)
    def mult(otherQ:IntQ):IntQ = multQ(richQ, otherQ)
    def minus(otherQ:IntQ):IntQ = minusQ(richQ, otherQ)
    def div(otherQ:IntQ):IntQ = divQ(richQ, otherQ)
    def within(otherQ:IntQ):Boolean = richQ._1.g >= otherQ._1.g && richQ._2.g <= otherQ._2.g
    def certain:Boolean = richQ._1.g == richQ._2.g
    def zeroable:Boolean = richQ._1.g <= 0 && richQ._2.g >= 0
  }
}