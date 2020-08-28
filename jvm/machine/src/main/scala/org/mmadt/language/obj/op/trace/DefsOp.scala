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
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Inst, Obj, Rec}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait DefsOp {
  this: Obj =>
  def defs: Rec[Obj, Obj] = DefsOp().exec(this)
}
object DefsOp extends Func[Obj, Obj] {
  override val preArgs: Boolean = false
  override val preStrm: Boolean = false

  def apply(): Inst[Obj, Rec[Obj, Obj]] = new VInst[Obj, Rec[Obj, Obj]](g = (Tokens.defs, List.empty), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    val defs: Rec[Obj, Obj] = rec(g = (Tokens.`,`, start.trace.map(x => x._2).
      filter(x => x.op.equals(Tokens.define)).flatMap(x => x.args).
      foldLeft(List.empty[Tuple2[Obj, Obj]])((x, y) => x :+ (str(y.range.name) -> y))))
    val vars: Rec[Obj, Obj] = rec(g = (Tokens.`,`, start.trace.
      filter(x => x._2.op.equals(Tokens.to)).
      foldLeft(List.empty[Tuple2[Obj, Obj]])((x, y) => x :+ (y._2.arg0[StrValue] -> y._1))))
    val rewrites: Rec[Obj, Obj] = rec(g = (Tokens.`,`, start.trace.map(x => x._2).
      filter(x => x.op.equals(Tokens.rewrite)).
      foldLeft(List.empty[Tuple2[Obj, Obj]])((x, y) => x :+ (str(y.arg0[Obj].range.name) -> y.arg0[Obj]))))
    rec(g = (Tokens.`,`, List(
      str("defs") -> defs,
      str("vars") -> vars,
      str("rewrites") -> rewrites).
      filter(x => x._2.gmap.nonEmpty).
      foldLeft(List.empty[Tuple2[Obj, Obj]])((x, y) => x :+ y)))
  }
}