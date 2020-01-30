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

import org.mmadt.machine.obj.impl.OInt.{i0, i1}
import org.mmadt.machine.obj.traits.algebra.Commutative
import org.mmadt.machine.obj.{Bool, Int}

/**
  * @author Marko A. Rodriguez (http://markorodriguez.com)
  */
class OInt(jvm: Long) extends OObj[Long](jvm) with Int with Commutative[Int] {

  override def zero(): Int = i0

  override def one(): Int = i1

  override def plus(other: Int): Int = new OInt(this.jvm + other._jvm())

  override def mult(other: Int): Int = new OInt(this.jvm * other._jvm())

  override def minus(other: Int): Int = new OInt(this.jvm - other._jvm())

  override def neg(): Int = new OInt(-this.jvm)

  override def >=(other: Int): Bool = new OBool(this.jvm >= other._jvm())

  override def =<(other: Int): Bool = new OBool(this.jvm <= other._jvm())

  override def <(other: Int): Bool = new OBool(this.jvm < other._jvm())

  override def >(other: Int): Bool = new OBool(this.jvm > other._jvm())

  override def eq(other: Int): Bool = new OBool(this.jvm == other._jvm())
}

object OInt {

  object i1 extends OInt(1)

  object i0 extends OInt(0)

}
