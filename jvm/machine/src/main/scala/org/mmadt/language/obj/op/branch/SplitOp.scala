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

package org.mmadt.language.obj.op.branch

import org.mmadt.language.Tokens
import org.mmadt.language.obj.branch._
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj._
import org.mmadt.storage.StorageFactory.{obj, qOne}
import org.mmadt.storage.obj.value.VInst

trait SplitOp {
  this: Obj =>
  def split[A <: Obj](coproduct: Coproduct[A]): Branching[A] = {
    var found = false
    coproduct.clone(value = (coproduct.value._1, coproduct.value._2.map(x => Option(if (this.test(x)) Inst.resolveArg(this, x) else obj.q(0)).filter(x => x.alive() && !found).map(x => {found = true; x }).getOrElse(obj.q(0))))).via(this, SplitOp(coproduct))
  }

  def split[A <: Obj](product: Product[A]): Branching[A] = (this match {
    case avalue: OValue[A] => product.clone(value = (product.value._1, product.value._2.map(x => Inst.resolveArg(avalue, x))))
    case atype: OType[A] => product.clone(value = (product.value._1, product.value._2.map(x => x.compute(atype))))
  }).via(this, SplitOp(product))
}

object SplitOp {
  def apply[A <: Obj](branches: Branching[A]): SplitInst[A] = new SplitInst[A](branches)

  class SplitInst[A <: Obj](branches: Branching[A], q: IntQ = qOne) extends VInst[A, Branching[A]]((Tokens.split, List(branches)), q) with BranchInstruction {
    override def q(quantifier: IntQ): this.type = new SplitInst[A](branches, quantifier).asInstanceOf[this.type]
    override def exec(start: A): Branching[A] = (branches match {
      case coprod:Coproduct[A] => start.split(coprod)
      case prod:Product[A] => start.split(prod)
    }).via(start, this)
  }

}
