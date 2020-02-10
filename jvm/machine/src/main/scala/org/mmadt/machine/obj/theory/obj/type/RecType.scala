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

package org.mmadt.machine.obj.theory.obj.`type`

import org.mmadt.language.Tokens
import org.mmadt.machine.obj.impl.traverser.RecursiveTraverser
import org.mmadt.machine.obj.theory.obj.value.{BoolValue, RecValue, StrValue, Value}
import org.mmadt.machine.obj.theory.obj.{Obj, Rec}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait RecType[A <: Obj, B <: Obj] extends Rec[A, B]
  with Type[RecType[A, B]] {

  def value(): Map[A, B] = throw new IllegalAccessException("...")

  override def to(label: StrValue): RecType[A, B] = this.push(inst(Tokens.to, label)) //
  override def get[BT <: Type[BT]](key: A, btype: BT): BT = this.push(btype, inst(Tokens.get, key)) //
  override def get(key: A): B = this.value().get(key) match {
    case Some(bvalue: Value[_]) => bvalue.asInstanceOf[B]
    case Some(btype: Type[_]) => new RecursiveTraverser(key).apply(btype).obj()
    case None => throw new NoSuchElementException("The rec does not have a value for the key: " + key)
    case _ => throw new RuntimeException()
  }

  override def plus(other: RecType[A, B]): RecType[A, B] = this.push(inst(Tokens.plus, other)) //
  override def plus(other: RecValue[A, B]): RecType[A, B] = this.push(inst(Tokens.plus, other)) //
  override def is(bool: BoolType): RecType[A, B] = this.push(inst(Tokens.is, bool)).q(0, q()._2) //
  override def is(bool: BoolValue): RecType[A, B] = this.push(inst(Tokens.is, bool)).q(0, q()._2) //
}