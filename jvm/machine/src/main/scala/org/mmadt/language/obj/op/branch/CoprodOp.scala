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

package org.mmadt.language.obj.op.branch

import org.mmadt.language.obj.branch.Coproduct
import org.mmadt.language.obj.{OValue, Obj}
import org.mmadt.storage.StorageFactory

trait CoprodOp {
  this: Obj =>
  def coprod[A <: Obj](coproduct: A*): Coproduct[A] = this.coprod(StorageFactory.coprod[A](coproduct: _*))
  def coprod[A <: Obj](coproduct: Coproduct[A]): Coproduct[A] = (this match {
    case avalue: OValue[A] => coproduct.exec(avalue)
    case _ => coproduct
  }).via(this, coproduct)
}

object CoprodOp {
  def apply[A <: Obj](coproduct: Coproduct[A]): Coproduct[A] = coproduct
}
