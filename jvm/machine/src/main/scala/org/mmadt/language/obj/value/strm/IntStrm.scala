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

package org.mmadt.language.obj.value.strm

import org.mmadt.language.obj.Int
import org.mmadt.language.obj.`type`.{BoolType, IntType}
import org.mmadt.language.obj.value.{BoolValue, IntValue, StrValue, Value}
import org.mmadt.storage.obj.value.VInt
import org.mmadt.storage.obj.value.strm.VIntStrm

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait IntStrm extends Int
  with Value[IntStrm] {

  override def value(): Iterator[IntValue] //
  override def start(): IntType //

  override def as(objType:String): this.type = new VIntStrm(objType,this.value().toSeq).asInstanceOf[this.type]  //
  override def to(label: StrValue): IntType = this.start().to(label) //
  override def plus(other: IntType): IntType = this.start().plus(other) //
  override def plus(other: IntValue): IntValue = throw new IllegalAccessException() //
  override def mult(other: IntType): IntType = this.start().mult(other) //
  override def mult(other: IntValue): IntValue = throw new IllegalAccessException() //
  override def neg(): IntValue = throw new IllegalAccessException() //
  override def gt(other: IntType): BoolType = this.start().gt(other) //
  override def gt(other: IntValue): BoolValue = throw new IllegalAccessException() //
  override def gt(): BoolType = throw new IllegalAccessException() //
  override def is(bool: BoolType): IntType = this.start().is(bool) //
  override def is(bool: BoolValue): IntValue = throw new IllegalAccessException() //
}
