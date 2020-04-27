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

import org.mmadt.language.obj.op.map.ZeroOp.ZeroInst
import org.mmadt.language.obj.op.map.{GetOp, PlusOp}
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.value.{RecValue, Value}
import org.mmadt.language.obj.{Obj, Rec}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VRec

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait RecType[A <: Obj, B <: Obj] extends Rec[A, B]
  with Type[Rec[A, B]]
  with ObjType {

  def apply(value: (Value[A], Value[B])*): RecValue[Value[A], Value[B]] = new VRec[Value[A], Value[B]](this.name, value.toMap, this.q)
  def apply(value: RecValue[Value[A], Value[B]]): RecValue[Value[A], Value[B]] = new VRec[Value[A], Value[B]](this.name, value.value, this.q)
  val value: collection.Map[A, B]

  override def get[BB <: Obj](key: A, btype: BB): BB = btype.via(this, GetOp[A, BB](key, btype))
  override def get(key: A): B = asType(this.value(key)).via(this, GetOp[A, B](key, asType(this.value(key))))
  override def put(key: A, value: B): this.type = this.clone(value = this.value + (key -> value)).via(this, PutOp(key, value))
  override def zero(): this.type = this.via(this, new ZeroInst())

  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.value.toString().hashCode() ^ this.lineage.hashCode() ^ this.q.hashCode()
  override def equals(other: Any): Boolean = other match {
    case atype: RecType[A, B] => this.name == atype.name && this.q == atype.q && this.value == atype.value && this.via == atype.via
    case _ => false
  }
}
