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
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.value.StrValue
import org.mmadt.storage.StorageFactory._


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait StrType extends Str
  with Type[Str]
  with ObjType {

  override def plus(other: StrValue): this.type = this.via(this, PlusOp(other))
  override def gt(other: StrValue): BoolType = bool.via(this, GtOp(other))
  override def gte(other: StrValue): BoolType = bool.via(this, GteOp(other))
  override def lt(other: StrValue): BoolType = bool.via(this, LtOp(other))
  override def lte(other: StrValue): BoolType = bool.via(this, LteOp(other))
  override def zero(): this.type = this.via(this, ZeroOp())
}


