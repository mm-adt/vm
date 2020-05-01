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

package org.mmadt.language.obj.op.filter

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.FilterInstruction
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

import scala.util.Try

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait IsOp {
  this: Obj =>
  def is(anon: __): this.type = IsOp(anon).exec(this)
  def is(bool: Bool): this.type = IsOp(bool).exec(this)
}

object IsOp {
  def apply[O <: Obj](other: Obj): Inst[O, O] = new IsInst[O](other)

  class IsInst[O <: Obj](arg: Obj, q: IntQ = qOne) extends VInst[O, O]((Tokens.is, List(arg)), q) with FilterInstruction {
    override def q(q: IntQ): this.type = new IsInst[O](arg, q).asInstanceOf[this.type]
    override def exec(start: O): O = {
      val inst: Inst[O, O] = new IsInst(Inst.resolveArg(start, arg), q)
      Try[O](
        if (inst.arg0[Bool]().value) start.via(start, inst)
        else start.via(start, inst).hardQ(qZero))
        .getOrElse(start match {
          case astrm: Strm[O] => astrm.via(start, inst).asInstanceOf[O]
          case _ => start.clone(via = (start, inst), q = minZero(multQ(start, inst)))
        })
    }
  }

}

