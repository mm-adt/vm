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

import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.map.PlusOp
import org.mmadt.language.obj.value.Value

trait Prod[A <: Obj] extends Brch[A]
  with PlusOp[Prod[A], Prod[A]]
  with Type[Prod[A]]
  with Value[Prod[A]] {

  override def plus(other: Prod[A]): this.type = this.clone(value = this.value ++ other.value, via = (this, PlusOp(other))) // [a;b] + [c;d] = [a;b;c;d]
  //override def plus(other: Coprod[A]): this.type = this.clone(value = List(this, other), via = (this, PlusOp(other))) // [a;b] + [c|d]

  override def test(other: Obj): Boolean = other match {
    case prod: Prod[_] =>
      if (prod.value.isEmpty || this.value.equals(prod.value)) return true
      this.value.zip(prod.value).foldRight(true)((a, b) => a._1.test(a._2) && b)
    case _ => false
  }

}