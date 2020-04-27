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
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.branch._
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait SplitOp {
  this: Obj =>
  def split[A <: Obj](brch: Brch[A]): Brch[A] = SplitOp(brch).exec(this.asInstanceOf[A])
  final def -<[A <: Obj](brch: Brch[A]): Brch[A] = this.split(brch)
}

object SplitOp {
  def apply[A <: Obj](branches: Brch[A]): SplitInst[A] = new SplitInst[A](branches)

  class SplitInst[A <: Obj](brchs: Brch[A], q: IntQ = qOne) extends VInst[A, Brch[A]]((Tokens.split, List(brchs)), q) with BranchInstruction {
    override def q(q: IntQ): this.type = new SplitInst[A](brchs, q).asInstanceOf[this.type]
    override def exec(start: A): Brch[A] = {
      brchs match {
        case product: Prod[_] =>
          var qTest = qOne
          product.clone(value = product.value.map(y =>
            Option(start)
              .filter(x => asType(x).range.test(asType(y).range)) // this is generally needed (find a more core home)
              .map(_ => y match {
                case atype: Type[_] => start ==> atype // COMPILE
                case _ => y
              })
              .map {
                case atype: Type[_] => start ==> atype // EXECUTE
                case x => x
              }
              .filter(x => x.alive())
              .map(x => x.q(multQ(x.q, qTest)))
              .map(x => {
                qTest = qZero;
                x
              })
              .getOrElse(obj.q(qZero))))
            .via(start, this)
        case _: Coprod[_] =>
          val inst = new SplitInst[A](brchs.clone(value = brchs.value.map(x => Inst.resolveArg(start, x))).asInstanceOf[Brch[A]], q)
          inst.arg0[Coprod[A]]().clone(via = (start, inst)) // the incoming quantifer effect slot quantification, not product quantification
      }
    }
  }

}
