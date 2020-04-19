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
import org.mmadt.language.obj.op.map.GetOp
import org.mmadt.language.obj.value.{IntValue, Value}
import org.mmadt.language.obj.{InstTuple, Int, Lst, Obj}
import org.mmadt.language.{LanguageException, LanguageFactory}
import org.mmadt.storage.StorageFactory.{asType, obj}

trait Branching[A <: Obj] extends Obj
  with MergeOp[A]
  with GetOp[Int, A]
  with Type[Branching[A]]
  with Value[Branching[A]]
  //with HeadOp[A]
  //with TailOp
  //with AppendOp[A]
  //with PlusOp[Product[A], Product[A]]
  //with ZeroOp {
{
  val value: InstTuple
  override def toString: String = LanguageFactory.printBranch(this)

  override def get(key: Int): A = {
    val valueType: A = key match {
      case avalue: IntValue if this.value._2.length >= (avalue.value + 1) => asType[A](this.value._2(avalue.value.toInt).asInstanceOf[A])
      case avalue: IntValue if this.value._2.nonEmpty =>
        Branching.checkIndex(this, avalue.value.toInt)
        this.value._2(avalue.value.toInt).asInstanceOf[A]
      case _ => obj.asInstanceOf[A]
    }
    valueType.via(this, GetOp[Int, A](key, valueType))
  }
  override def get[BB <: Obj](key: Int, btype: BB): BB = btype.via(this, GetOp[Int, BB](key, btype))

}

object Branching {
  def checkIndex(alst: Branching[_], index: scala.Int): Unit = {
    if (index < 0) throw new LanguageException("lst index must be 0 or greater: " + index)
    if (alst.value._2.length < (index + 1)) throw new LanguageException("lst index is out of bounds: " + index)
  }
}

