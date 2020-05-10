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
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Inst, Int, IntQ, Obj, Poly, Real}
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
          case aint: Int => start.clone(ground = aint.ground * inst.arg0[Int]().ground)
          case areal: Real => start.clone(ground = areal.ground * inst.arg0[Real]().ground)
          //////// EXPERIMENTAL
          case serialA: Poly[O] if serialA.isSerial => multObj[O](arg match {
            case serialB: Poly[O] if serialB.isSerial => serialA.clone(serialA.groundList ++ serialB.groundList)
            case choiceB: Poly[O] if choiceB.isChoice => choiceB.clone(choiceB.groundList.map(a => /.clone(serialA.groundList :+ a)).asInstanceOf[List[O]])
          })
          case choiceA: Poly[O] if choiceA.isChoice => multObj[O](arg match {
            case serialB: Poly[O] if serialB.isSerial => choiceA.clone(choiceA.groundList.map(a => /.clone(a +: serialB.groundList)).asInstanceOf[List[O]])
            case choiceB: Poly[O] if choiceB.isChoice => /[O].clone(choiceA.groundList.flatMap(a => choiceB.groundList.map(b => a / b)).asInstanceOf[List[O]])
          })
        }
        case _ => start
      }).via(start, inst).asInstanceOf[O]
    }
  }

  def multObj[O <: Obj](poly: Poly[O]): Poly[O] = {
    if (!poly.isType) return poly
    poly.clone(List(poly.groundList.foldLeft(poly.groundList.head.domain[Obj]())((a, b) => a.compute[Obj](b.asInstanceOf[Type[Obj]]).asInstanceOf[Type[Obj]])).asInstanceOf[List[O]])
  }

}

