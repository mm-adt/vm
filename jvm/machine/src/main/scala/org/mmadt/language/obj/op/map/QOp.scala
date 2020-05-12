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
import org.mmadt.language.obj.op.QuantifierInstruction
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Int, Obj}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait QOp {
  this: Obj =>
  def quant(): Int = QOp().exec(this)
}

object QOp {
  def apply(): QInst = new QInst

  class QInst extends VInst[Obj, Int](g=(Tokens.q, Nil)) with QuantifierInstruction {
    override def exec(start: Obj): Int = (start match {
      case _: Value[_] => this.q._1.q(qOne)
      case _ => int
    }).via(start, this).asInstanceOf[Int]
  }

}