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

import org.mmadt.language.obj.Int
import org.mmadt.language.obj.op.map.LteOp.LteInst
import org.mmadt.language.obj.op.map.ZeroOp.ZeroInst
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.value.IntValue
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait IntType extends Int
  with Type[Int]
  with ObjType {
  def apply(value: IntValue): IntValue = value.named(this.name)
  override def plus(other: IntType): IntType = this.compose(PlusOp(other))
  override def plus(other: IntValue): this.type = this.compose(PlusOp(other))
  override def mult(other: IntType): IntType = this.compose(MultOp(other))
  override def mult(other: IntValue): this.type = this.compose(MultOp(other))
  override def neg(): this.type = this.compose(NegOp())
  override def one(): this.type = this.compose(OneOp())
  override def gt(other: IntType): BoolType = this.compose(bool, GtOp(other))
  override def gt(other: IntValue): BoolType = this.compose(bool, GtOp(other))
  override def gte(other: IntValue): BoolType = this.compose(bool, GteOp(other))
  override def lt(other: IntValue): BoolType = this.compose(bool, LtOp(other))
  override def lte(other: IntValue): BoolType = this.compose(bool, LteOp(other))
  override def zero(): this.type = this.compose(new ZeroInst())
}


