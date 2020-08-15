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
import org.mmadt.language.obj.Obj.ViaTuple
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.op.branch.CombineOp
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Inst, Lst, Obj}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait PathOp {
  this: Obj =>
  def path(): Lst[Obj] = PathOp().exec(this)
  def path(pattern: Lst[_ <: Obj]): Lst[_ <: Obj] = PathOp(pattern).exec(this)
}

object PathOp extends Func[Obj, Lst[Obj]] {
  val VERTICES: Lst[Obj] = (__ `;` zeroObj).asInstanceOf[Lst[Obj]]
  def apply(): Inst[Obj, Lst[Obj]] = PathOp.apply((__ `;` __).asInstanceOf[Lst[Obj]])
  def apply(pattern: Lst[_ <: Obj]): Inst[Obj, Lst[Obj]] = new VInst[Obj, Lst[Obj]](g = (Tokens.path, List(pattern)), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Lst[Obj]]): Lst[Obj] = (start match {
    case _: Strm[_] => start
    case _ => lst(g = (inst.arg0[Lst[Obj]].gsep,
      CombineOp.combineAlgorithm(lst(g = (
        inst.arg0[Lst[Obj]].gsep,
        start.trace.foldLeft(List.empty[Obj])((a, b) => a :+ b._1 :+ b._2) :+ start)), inst.arg0[Lst[Obj]]).glist.filter(_.alive)))
  }).via(start, inst).asInstanceOf[Lst[Obj]]

  @inline implicit def viaToRichVia(baseVia: ViaTuple): RichVia = new RichVia(baseVia)
  class RichVia(val richVia: ViaTuple) {
    def isOp(op: String): Boolean = richVia._2 != null && richVia._2.op.equals(op)
  }
}