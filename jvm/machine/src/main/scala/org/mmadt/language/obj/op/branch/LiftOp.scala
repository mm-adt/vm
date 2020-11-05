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

package org.mmadt.language.obj.op.branch

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.StorageFactory.zeroObj
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait LiftOp {
  this: Obj =>
  def lift[A <: Obj](anon: __): A = LiftOp(anon).exec(this).asInstanceOf[A]
  def lift[A <: Obj](atype: A): A = LiftOp(atype).exec(this).asInstanceOf[A]
  // final def `<<`(atype: Type[Obj]): atype.type = this.lift(atype)
  // final def `>>`[A <: Obj]: A = this.asInstanceOf[A]
}

object LiftOp extends Func[Obj, Obj] {
  override val preArgs: Boolean = false
  def apply(atype: Obj): Inst[Obj, Obj] = new VInst[Obj, Obj](g = (Tokens.lift, List(atype)), func = this) with BranchInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = (start.clone(via = (start.rangeObj, inst.clone(_ => List(zeroObj)))) =>> inst.arg0[Obj].trace.reconstruct[Obj](start.range)).via(start, inst)

  def inLift(aobj: Obj, inst: Inst[_, _]): Boolean = !inst.op.equals(Tokens.lift) && aobj.via.exists(x => x._2.op.equals(Tokens.lift) && x._2.arg0[Obj].equals(zeroObj)) // TODO: this is costly
}