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

import org.mmadt.language.obj.{Int, Real}
import org.mmadt.language.obj.op.map.{GtOp, GteOp, LtOp, LteOp, MultOp, NegOp, OneOp, PlusOp, ZeroOp}
import org.mmadt.language.obj.value.{IntValue, RealValue, Value}
import org.mmadt.storage.StorageFactory.bool

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait RealType extends Real
  with Type[Real]
  with ObjType {

  def apply(value:RealValue):RealValue = value.named(this.name)

  override def plus(other:Type[Real]):RealType = this.compose(PlusOp(other))
  override def plus(other:Value[Real]):this.type = this.compose(PlusOp(other))
  override def mult(other:Type[Real]):RealType = this.compose(MultOp(other))
  override def mult(other:Value[Real]):this.type = this.compose(MultOp(other))
  override def neg():this.type = this.compose(NegOp())
  override def one():RealType = this.compose(OneOp())
  override def gt(other:Value[Real]):BoolType = this.compose(bool,GtOp(other))
  override def gte(other:Value[Real]):BoolType = this.compose(bool,GteOp(other))
  override def lt(other:Value[Real]):BoolType = this.compose(bool,LtOp(other))
  override def lte(other:Value[Real]):BoolType = this.compose(bool,LteOp(other))
  override def zero():RealType = this.compose(ZeroOp())
}
