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
import org.mmadt.machine.obj.impl.TInt.int
import org.mmadt.machine.obj.impl.VInt.{int0, int1}
import org.mmadt.machine.obj.theory.obj
import org.mmadt.machine.obj.theory.obj.`type`.{BoolType, IntType}
import org.mmadt.machine.obj.theory.obj.value.{BoolValue, IntValue}


/**
  * @author Marko A. Rodriguez (http://markorodriguez.com)
  */
class VInt(jvm: Long, quantifier: TQ) extends VObj[Long](jvm, quantifier) with IntValue {

  def this(jvm: Long) = this(jvm, qOne)

  override def zero(): obj.Int = int0

  override def one(): obj.Int = int1

  override def plus(other: IntValue): IntValue = new VInt(this.jvm + other._jvm())

  override def plus(other: IntType): IntType = new TInt(List(VInst.plus(other)), qOne) // ??

  override def plus(other: Long): IntValue = this.plus(int(other))

  override def mult(other: IntValue): IntValue = new VInt(this.jvm * other._jvm())

  override def mult(other: IntType): IntType = new TInt(List(VInst.plus(other)), qOne) // ??

  override def mult(other: Long): IntValue = this.mult(int(other))

  override def neg(): obj.Int = new VInt(-this.jvm)

  override def gt(other: Long): BoolValue = new VBool(this.jvm < other)

  override def gt(other: IntValue): BoolValue = new VBool(this.jvm < other._jvm())

  override def gt(other: IntType): BoolType = new TBool() //


}

object VInt {

  object int1 extends VInt(1)

  object int0 extends VInt(0)

}
