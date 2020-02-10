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

package org.mmadt.language.obj.value

import org.mmadt.language.obj.Bool
import org.mmadt.language.obj.`type`.BoolType

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait BoolValue extends Bool
  with Value[BoolValue] {

  override def value(): Boolean //
  override def start(): BoolType //

  override def to(label: StrValue): BoolType = this.start().to(label) //

  override def and(bool: BoolType): BoolType = this.start().and(bool) //
  override def and(bool: BoolValue): BoolValue = this.value() && bool.value() //
  override def or(bool: BoolType): BoolType = this.start().or(bool) //
  override def or(bool: BoolValue): BoolValue = this.value() || bool.value() //
  override def is(bool: BoolType): BoolType = this.start().is(bool) //
  override def is(bool: BoolValue): BoolValue = if (bool.value()) this else this.q(0)
}