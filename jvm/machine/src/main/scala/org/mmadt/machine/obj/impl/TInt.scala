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

import org.mmadt.machine.obj.impl.TInt.int
import org.mmadt.machine.obj.impl.VBool.boolT
import org.mmadt.machine.obj.impl.VInt.{int0, int1}
import org.mmadt.machine.obj.theory.obj
import org.mmadt.machine.obj.theory.obj.`type`.IntType
import org.mmadt.machine.obj.theory.obj.value.IntValue
import org.mmadt.machine.obj.{Bool, TQ, qOne}

/**
  * @author Marko A. Rodriguez (http://markorodriguez.com)
  */
class TInt(jvm: List[VInst], quantifier: TQ) extends TObj(jvm, quantifier) with IntType {

  def this(quantifier: TQ) = this(List(), quantifier)

  def this() = this(qOne)

  override def lt(other: obj.Int): Bool = boolT

  override def gte(other: obj.Int): Bool = boolT

  override def lte(other: obj.Int): Bool = boolT

  override def gt(other: obj.Int): Bool = new TBool(this.jvm ++ List(VInst.gt(other)), this.q())

  override def one(): obj.Int = int1

  override def plus(other: IntValue): IntType = new TInt(this.jvm ++ List(VInst.plus(other)), this.q())

  override def plus(other: IntType): IntType = new TInt(this.jvm ++ List(VInst.plus(other)), this.q())

  override def plus(other: Long): IntType = this.plus(int(other))

  override def neg(): obj.Int = int1

  override def mult(other: IntValue): IntType = new TInt(this.jvm ++ List(VInst.mult(other)), this.q())

  override def mult(other: IntType): IntType = new TInt(this.jvm ++ List(VInst.mult(other)), this.q())

  override def mult(other: Long): IntType = this.mult(int(other))

  override def zero(): obj.Int = int0

  override def minus(other: obj.Int): obj.Int = int1

}

object TInt {

  def int: IntType = new TInt()

  def int(jvm: Long): IntValue = new VInt(jvm)

}