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

import org.mmadt.machine.obj._
import org.mmadt.machine.obj.impl.VInt.{int0, int1}

/**
  * @author Marko A. Rodriguez (http://markorodriguez.com)
  */
class VInt(jvm: Long, quantifier: TQ) extends VObj[Long](jvm, quantifier) with Int {

  def this(jvm: Long) = this(jvm, qOne)

  override def zero(): Int = int0

  override def one(): Int = int1

  override def plus(other: Int): Int = new VInt(this.jvm + otherLong(other))

  override def mult(other: Int): Int = new VInt(this.jvm * otherLong(other))

  override def minus(other: Int): Int = new VInt(this.jvm - otherLong(other))

  override def neg(): Int = new VInt(-this.jvm)

  override def gte(other: Int): Bool = new VBool(this.jvm >= otherLong(other))

  override def lte(other: Int): Bool = new VBool(this.jvm <= otherLong(other))

  override def gt(other: Int): Bool = new VBool(this.jvm < otherLong(other))

  override def lt(other: Int): Bool = new VBool(this.jvm > otherLong(other))

  private def otherLong(other: Int) : Long = other.asInstanceOf[VInt]._jvm()

}

object VInt {

  object int1 extends VInt(1)

  object int0 extends VInt(0)

}
