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
import org.mmadt.language.obj.op.ReduceInstruction
import org.mmadt.language.obj.{Inst, O, Obj, TType}
import org.mmadt.processor.obj.`type`.util.InstUtil
import org.mmadt.storage.obj.qOne
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait FoldOp {
  this:Obj =>
  def fold[O <: Obj](seed:O)(atype:TType[O]):O = seed
}

object FoldOp {
  def apply[A <: Obj,B <: Obj](_seed:B,atype:TType[B]):Inst = new VInst((Tokens.fold,List(_seed,atype)),qOne,(a:O,b:List[Obj]) => a.fold(_seed)(atype)) with ReduceInstruction[A,B] {
    override val seed     :B          = _seed
    override val reduction:(A,B) => B = (a,b) => InstUtil.typeEval[O,B](a,b,atype) // TODO: put the seed in the traverser state (thus, lift the reduction)
  }
}