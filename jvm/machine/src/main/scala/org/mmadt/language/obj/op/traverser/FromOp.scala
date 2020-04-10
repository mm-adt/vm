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

package org.mmadt.language.obj.op.traverser

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.TraverserInstruction
import org.mmadt.language.obj.value.{StrValue, Value}
import org.mmadt.language.obj.{Inst, IntQ, Obj, multQ}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait FromOp {
  this: Obj =>
  def from[O <: Obj](label: StrValue): O = this match {
    case atype: Type[_] => atype.compose(FromOp(label)).asInstanceOf[O]
    case avalue: Value[_] => avalue.start().from(label)
  }
}

object FromOp {
  def apply(label: StrValue): FromInst[Obj] = new FromInst[Obj](label)
  def apply[O <: Obj](label: StrValue, default: O): FromInst[O] = new FromInst[O](label, default)

  class FromInst[O <: Obj](label: StrValue, default: O = null, q: IntQ = qOne) extends VInst[Obj, O]((Tokens.from, List(label)), q) with TraverserInstruction {
    override def q(quantifier: IntQ): this.type = new FromInst[O](label, default, quantifier).asInstanceOf[this.type]
    override def exec(start: Obj): O = start.lineage.find(x => x._2.op() == Tokens.to && x._2.arg0() == label).get._1.q(multQ(start, this)).via(start,this).asInstanceOf[O]
  }
}
