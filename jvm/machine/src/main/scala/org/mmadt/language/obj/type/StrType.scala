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

import org.mmadt.language.obj._
import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.traverser.ToOp
import org.mmadt.language.obj.value.{BoolValue, StrValue, Value}
import org.mmadt.storage.StorageFactory._


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait StrType extends Str
  with Type[Str]
  with ObjType {

  override def to(label:StrValue):this.type = this.compose(ToOp(label))
  override def eqs(other:Type[Str]):BoolType = this.compose(bool,EqsOp(other))
  override def eqs(other:Value[Str]):BoolType = this.compose(bool,EqsOp(other))
  override def plus(other:Type[Str]):StrType = this.compose(PlusOp(other))
  override def plus(other:Value[Str]):this.type = this.compose(PlusOp(other))
  override def gt(other:Type[Str]):BoolType = this.compose(bool,GtOp(other))
  override def gt(other:Value[Str]):BoolType = this.compose(bool,GtOp(other))
  override def gte(other:Type[Str]):BoolType = this.compose(bool,GteOp(other))
  override def gte(other:Value[Str]):BoolType = this.compose(bool,GteOp(other))
  override def lt(other:Type[Str]):BoolType = this.compose(bool,LtOp(other))
  override def lt(other:Value[Str]):BoolType = this.compose(bool,LtOp(other))
  override def lte(other:Type[Str]):BoolType = this.compose(bool,LteOp(other))
  override def lte(other:Value[Str]):BoolType = this.compose(bool,LteOp(other))
}


