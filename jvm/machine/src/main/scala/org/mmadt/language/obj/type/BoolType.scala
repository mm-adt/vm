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

package org.mmadt.language.obj.`type`

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Bool
import org.mmadt.language.obj.op.{AndOp, IsOp, OrOp, ToOp}
import org.mmadt.language.obj.value.{BoolValue, StrValue}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait BoolType extends Bool
  with Type[BoolType] {

  override def and(bool: BoolType): BoolType = this.push(AndOp(bool)) //
  override def and(bool: BoolValue): BoolType = this.push(AndOp(bool)) //
  override def or(bool: BoolType): BoolType = this.push(OrOp(bool)) //
  override def or(bool: BoolValue): BoolType = this.push(OrOp(bool)) //
  override def to(label: StrValue): BoolType = this.push(ToOp(label)) //
  override def is(bool: BoolType): BoolType = this.push(IsOp(bool)).q(0, q()._2) //
  override def is(bool: BoolValue): BoolType = this.push(IsOp(bool)).q(0, q()._2) //
}
