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

package org.mmadt.language.obj.branch

import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.branch.MergeOp
import org.mmadt.language.obj.op.map.{GetOp, HeadOp, TailOp, ZeroOp}
import org.mmadt.language.obj.value.{IntValue, Value}
import org.mmadt.language.obj.{Int, Obj}
import org.mmadt.language.{LanguageException, LanguageFactory}
import org.mmadt.storage.StorageFactory.{asType, obj}

trait Branching[A <: Obj] extends Obj
  with Type[Branching[A]]
  with Value[Branching[A]]
  with MergeOp[A]
  with GetOp[Int, A]
  with HeadOp[A]
  with TailOp
  //with AppendOp[A]
  //with PlusOp[Product[A], Product[A]]
  with ZeroOp {
  val value: List[A]
  override def toString: String = LanguageFactory.printBranch(this)

  override def zero(): this.type = this.clone(value = List.empty[A], via = (this, ZeroOp()))
  override def head(): A = if (this.value.isEmpty) throw new LanguageException("no head on empty lst") else this.value.head.via(this, HeadOp()) // TODO: check process trace for type or value
  override def tail(): this.type = if (this.value.isEmpty) throw new LanguageException("no tail on empty lst") else this.clone(value = this.value.tail, via = (this, TailOp()))

  override def get(key: Int): A = {
    val valueType: A = key match {
      case avalue: IntValue if this.value.length >= (avalue.value + 1) => asType[A](this.value(avalue.value.toInt))
      case avalue: IntValue if this.value.nonEmpty =>
        Branching.checkIndex(this, avalue.value.toInt)
        this.value(avalue.value.toInt)
      case _ => obj.asInstanceOf[A]
    }
    valueType.via(this, GetOp[Int, A](key, valueType))
  }
  override def get[BB <: Obj](key: Int, btype: BB): BB = btype.via(this, GetOp[Int, BB](key, btype))

}

object Branching {
  def checkIndex(alst: Branching[_], index: scala.Int): Unit = {
    if (index < 0) throw new LanguageException("lst index must be 0 or greater: " + index)
    if (alst.value.length < (index + 1)) throw new LanguageException("lst index is out of bounds: " + index)
  }
}

