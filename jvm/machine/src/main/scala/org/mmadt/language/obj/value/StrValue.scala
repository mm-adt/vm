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

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Str
import org.mmadt.language.obj.`type`.StrType
import org.mmadt.language.obj.op.map._
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait StrValue extends Str
  with ObjValue
  with Value[Str] {
  override val value: String
  def value(java: String): this.type = this.clone(this.name, java, this.q)
  override def plus(other: StrType): StrType = this.start[Str]().plus(other)
  override def plus(other: StrValue): this.type = this.value(this.value + other.value)
  override def gt(other: StrValue): BoolValue = vbool(value = this.value > other.value, q = this.q, via = (this, GtOp(other)))
  override def gte(other: StrValue): BoolValue = vbool(value = this.value >= other.value, q = this.q, via = (this, GteOp(other)))
  override def lt(other: StrValue): BoolValue = vbool(value = this.value < other.value, q = this.q, via = (this, LtOp(other)))
  override def lte(other: StrValue): BoolValue = vbool(value = this.value <= other.value, q = this.q, via = (this, LteOp(other)))
  override def zero(): this.type = this.clone(name = this.name, value = Tokens.empty, q = this.q, via = (this, ZeroOp()))
}
