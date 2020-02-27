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
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.ReduceInstruction
import org.mmadt.storage.obj._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait FoldOp {
  this:Obj =>
  def fold[O <: Obj](seed:(String,O))(atype:TType[O]):O = seed._2
  def fold[O <: Obj](seed:O)(atype:TType[O]):O = fold("seed" -> seed)(atype)
}

object FoldOp {
  def apply[A <: Obj](_seed:(String,A),atype:TType[A]):Inst = new VInst((Tokens.fold,List(str(_seed._1),_seed._2,atype)),qOne,(a:O,b:List[Obj]) => a.fold(_seed)(atype)) with ReduceInstruction[A] {
    override val seed     :(String,A) = _seed
    override val reduction:TType[A]   = atype
  }
  def apply[A <: Obj](_seed:(String,A),atype:__):Inst = new VInst((Tokens.fold,List(str(_seed._1),_seed._2,atype)),qOne,((a:O,b:List[Obj]) => a.fold(_seed)(atype(a.asInstanceOf[OType].range()))).asInstanceOf[(Obj,List[Obj]) => Obj])
}