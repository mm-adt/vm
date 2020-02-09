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

package org.mmadt.machine.obj.theory.obj.`type`

import org.mmadt.language.Tokens
import org.mmadt.machine.obj.theory.obj.value.StrValue
import org.mmadt.machine.obj.theory.obj.{Bool, Int}
import org.mmadt.machine.obj.theory.operator.ToOp

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait IntType extends Int
  with Type[IntType]
  with ToOp[Int] {

  @throws[IllegalAccessException]
  override def value(): Long = throw new IllegalAccessException("...")

  override def is(other: Bool): IntType = this.push(inst(Tokens.is, other)).q(int(0), q()._2) //
  override def plus(other: Int): IntType = this.push(inst(Tokens.plus, other)) //
  override def mult(other: Int): IntType = this.push(inst(Tokens.mult, other)) //
  override def gt(other: Int): BoolType = this.bool(inst(Tokens.gt, other)) //
  override def to(label: StrValue): IntType = this.push(inst(Tokens.to, label)) //
}