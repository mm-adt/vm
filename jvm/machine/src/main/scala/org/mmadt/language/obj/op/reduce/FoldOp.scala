/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 *  mm-ADT is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU Affero General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 *  License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 *  You can be released from the requirements of the license by purchasing a
 *  commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language.obj.op.reduce

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait FoldOp {
  this: Obj =>
  def fold[O <: Obj](seed: O)(foldType: Type[_]): O = FoldOp(seed, foldType).exec(this).asInstanceOf[O]

}

object FoldOp extends Func[Obj, Obj] {
  def apply[A <: Obj](seed: A, atype: A): Inst[Obj, A] = new VInst[Obj, A](g = (Tokens.fold, List(seed, atype)), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    val seed: Obj = inst.arg0[Obj]
    val folding: Obj = __.to("x").compute(inst.arg1[Obj])
    start match {
      case strm: Strm[_] => strm.values.foldLeft(seed)((x, y) => Inst.resolveArg((x `,` y), folding))
      case avalue: Value[_] => Inst.resolveArg((avalue `,` seed), folding).via(start, inst)
      case _: Type[_] => inst.arg1[Type[Obj]].via(start, inst)
    }
  }
}