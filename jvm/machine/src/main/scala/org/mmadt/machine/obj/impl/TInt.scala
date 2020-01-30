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

package org.mmadt.machine.obj.impl

import org.mmadt.machine.obj.impl.VBool.boolT
import org.mmadt.machine.obj.impl.VInt.{int0, int1}
import org.mmadt.machine.obj.{Bool, Inst, Int, TQ, qOne}

/**
  * @author Marko A. Rodriguez (http://markorodriguez.com)
  */
class TInt(jvm: List[VInst], quantifier: TQ) extends TObj(jvm, quantifier) with Int {

  def this(quantifier: TQ) = this(List(), quantifier)

  def this() = this(qOne)

  override def lt(other: Int): Bool = boolT

  override def gte(other: Int): Bool = boolT

  override def lte(other: Int): Bool = boolT

  override def gt(other: Int): Bool = new TBool(this.jvm ++ List(VInst.gt(other)), this.q())

  override def one(): Int = int1

  override def plus(other: Int): Int = new TInt(this.jvm ++ List(VInst.plus(other)), this.q())

  override def neg(): Int = int1

  override def mult(other: Int): Int = new TInt(this.jvm ++ List(VInst.mult(other)), this.q())

  override def zero(): Int = int0

  override def minus(other: Int): Int = int1

}

object TInt {

  object int extends TInt

  def int(jvm: Long): Int = new VInt(jvm)

}