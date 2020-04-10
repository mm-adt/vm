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
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.{ReduceInstruction, TraverserInstruction}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait FoldOp {
  this:Obj =>
  def fold[O <: Obj](seed:(String,O))(foldType:Type[O]):O = this match {
    case atype:Type[_] => atype.compose(asType[O](seed._2),FoldOp(seed,foldType))
    case _ => this ==> foldType
  }
  def fold[O <: Obj](seed:O)(atype:Type[O]):O = fold("seed" -> seed)(atype)
}

object FoldOp {
  def apply[A <: Obj](_seed:(String,A),atype:A):Inst[Obj,A] = new FoldInst[A](_seed,atype)

  class FoldInst[A <: Obj](_seed:(String,A),atype:Obj) extends VInst[Obj,A]((Tokens.fold,List(str(_seed._1),_seed._2,atype))) with ReduceInstruction[A] with TraverserInstruction {
    override val seed     :(String,A) = _seed
    override val reduction:Type[A]    = atype.asInstanceOf[Type[A]]

    override def exec(start:Obj):A={
      val end:Obj = start match {
        case _:Type[Obj] => start //Traverser.stateSplit[Obj](this.arg0[StrValue]().value,this.arg1[A]())(trav)
        case _ => start
      }
      end.fold(seed)(Type.resolve(end,reduction))
    }

    /*private def deduceSeed(defaultSeed:ZeroOp[Type[Obj]],model:Model):Option[Obj] = {
       Option(Traverser.standard(int(1))(model.get(defaultSeed.zero()).get).obj())
    }*/
  }

}