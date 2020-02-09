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
import org.mmadt.machine.obj.theory.obj.Int
import org.mmadt.machine.obj.theory.obj.value.{BoolValue, IntValue, StrValue}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait IntType extends Int
  with Type[IntType] {

  @throws[IllegalAccessException]
  override def value(): Long = throw new IllegalAccessException("...")

  override def to(label: StrValue): IntType = this.push(inst(Tokens.to, label)) //

  override def plus(other: IntType): IntType = this.push(inst(Tokens.plus, other)) //
  override def plus(other: IntValue): IntType = this.push(inst(Tokens.plus, other)) //
  override def mult(other: IntType): IntType = this.push(inst(Tokens.mult, other)) //
  override def mult(other: IntValue): IntType = this.push(inst(Tokens.mult, other)) //
  override def gt(other: IntType): BoolType = this.bool(inst(Tokens.gt, other)) //
  override def gt(other: IntValue): BoolType = this.bool(inst(Tokens.gt, other)) //
  override def is(bool: BoolType): IntType = this.push(inst(Tokens.is, bool)).q(0, q()._2) //
  override def is(bool: BoolValue): IntType = this.push(inst(Tokens.is, bool)).q(0, q()._2) //
}