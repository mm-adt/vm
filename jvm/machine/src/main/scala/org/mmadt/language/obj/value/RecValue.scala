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

import org.mmadt.language.obj.op.map.{GetOp, PlusOp}
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.{Obj, Rec}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait RecValue[A <: Value[Obj], B <: Value[Obj]] extends Rec[A, B]
  with ObjValue
  with Value[Rec[A, B]] {

  override val value: collection.Map[A, B]
  override def plus(other: RecValue[_, _]): this.type = this.clone(value = this.value ++ other.asInstanceOf[RecValue[A, B]].value, via = (this, PlusOp(other)))
  override def get(key: A): B = this.value(key).via(this, GetOp(key))
  override def get[BB <: Obj](key: A, btype: BB): BB = this.value(key).via(this, GetOp(key, btype)).asInstanceOf[BB]
  override def put(key: A, value: B): this.type = this.clone(value = this.value + (key -> value), via = (this, PutOp(key, value)))
}
