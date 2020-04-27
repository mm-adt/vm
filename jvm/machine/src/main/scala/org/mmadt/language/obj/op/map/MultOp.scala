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

package org.mmadt.language.obj.op.map

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.branch.{Brch, Coprod, Prod}
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Inst, Int, IntQ, Obj, Real}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait MultOp[O <: Obj] {
  this: O =>
  def mult(anon: __): this.type = MultOp(anon).exec(this)
  def mult(arg: O): this.type = MultOp(arg).exec(this)
  final def *(anon: __): this.type = this.mult(anon)
  final def *(arg: O): this.type = this.mult(arg)
}

object MultOp {
  def apply[O <: Obj](obj: Obj): MultInst[O] = new MultInst[O](obj)

  class MultInst[O <: Obj](arg: Obj, q: IntQ = qOne) extends VInst[O, O]((Tokens.mult, List(arg)), q) {
    override def q(q: IntQ): this.type = new MultInst[O](arg, q).asInstanceOf[this.type]
    override def exec(start: O): O = {
      val inst = new MultInst(Inst.resolveArg(start, arg), q)
      (start match {
        case _: Strm[_] => start
        case _: Value[_] => start match {
          case aint: Int => start.clone(value = aint.value * inst.arg0[Int]().value)
          case areal: Real => start.clone(value = areal.value * inst.arg0[Real]().value)
          //////// EXPERIMENTAL
          case prodA: Prod[O] => multObj[O](arg match {
            case prodB: Prod[O] => prod[O]().clone(value = prodA.value ++ prodB.value)
            case coprodB: Coprod[O] => coprod[O]().clone(value = coprodB.value.map(a => prod().clone(value = prodA.value :+ a)))
          })
          case coprodA: Coprod[O] => multObj[O](arg match {
            case prodB: Prod[O] => coprod[O]().clone(value = coprodA.value.map(a => prod().clone(value = a +: prodB.value)))
            case coprodB: Coprod[O] => coprod[O]().clone(value = coprodA.value.flatMap(a => coprodB.value.map(b => prod(a, b))))
          })
        }
        case _ => start
      }).via(start, inst).asInstanceOf[O]
    }
  }

  def multObj[O <: Obj](brch: Brch[O]): Brch[O] = {
    if (!brch.isType) return brch
    brch.clone(value = List(brch.value.foldLeft(brch.value.head.asInstanceOf[Type[Obj]].domain[Obj]())((a, b) => a.compute[Obj](b.asInstanceOf[Type[Obj]]).asInstanceOf[Type[Obj]])))
  }

}

