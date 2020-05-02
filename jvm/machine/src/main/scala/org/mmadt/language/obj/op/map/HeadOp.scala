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

package org.mmadt.language.obj.op.map

import org.mmadt.language.obj.`type`.LstType
import org.mmadt.language.obj.value.LstValue
import org.mmadt.language.obj.{Brch, IntQ, Obj}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageFactory.{asType, obj, qOne}
import org.mmadt.storage.obj.value.VInst

trait HeadOp[A <: Obj] {
  this: Obj =>
  def head(): A = HeadOp().exec(this)
}

object HeadOp {
  def apply[A <: Obj](): HeadInst[A] = new HeadInst[A]

  class HeadInst[A <: Obj](q: IntQ = qOne) extends VInst[Obj, A]((Tokens.head, Nil), q) {
    override def q(q: IntQ): this.type = new HeadInst[A](q).asInstanceOf[this.type]
    override def exec(start: Obj): A = (start match {
      case alst: LstValue[A] => if (alst.ground.isEmpty) throw new LanguageException("no head on empty lst") else alst.ground.head
      case alst: LstType[A] => if (alst.ground.isEmpty) obj.asInstanceOf[A] else asType(alst.ground.head)
      case abrch: Brch[A] => if (abrch.ground.isEmpty) throw new LanguageException("no head on empty brch") else abrch.ground.head
    }).via(start, this)
  }

}