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
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, Obj, asType}
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait SwapOp {
  this:Obj =>
  def swap[A <: Obj](atype:A):A = SwapOp(atype).exec(this).asInstanceOf[A]
}

object SwapOp extends Func[Obj, Obj] {
  override val preArgs:Boolean = false
  def apply(atype:Obj):Inst[Obj, Obj] = new VInst[Obj, Obj](g = (Tokens.swap, List(atype)), func = this) with BranchInstruction
  override def apply(start:Obj, inst:Inst[Obj, Obj]):Obj = {
    val nestedInst:Inst[Obj, Obj] = inst.arg0[Obj].via._2.asInstanceOf[Inst[Obj, Obj]]
    val arg:Obj = nestedInst.arg0[Obj]
    start match {
      case _:Type[_] => start.via(start, inst)
      case _:Value[_] => (arg ~~> nestedInst.clone(_ => List(start)).exec(asType(arg))).via(start, inst)
    }
  }
}