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

import org.mmadt.language.LanguageFactory
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Obj, eqQ}

trait Prod[A <: Obj] extends Brch[A]
  with Type[Prod[A]]
  with Value[Prod[A]] {

  // TODO: hashcode
  override def equals(other: Any): Boolean = other match {
    case brch: Prod[_] =>
      brch.name.equals(this.name) &&
        eqQ(brch, this) &&
        ((this.isValue && brch.isValue && this.value.zip(brch.value).foldRight(true)((a, b) => a._1.test(a._2) && b)) ||
          (this.value == brch.value && this.via == brch.via))
    case _ => false
  }

  override def test(other: Obj): Boolean = other match {
    case prod: Prod[_] =>
      if (prod.value.isEmpty || this.value.equals(prod.value)) return true
      this.value.zip(prod.value).foldRight(false)((a, b) => a._1.test(a._2) || b)
    case _ => false
  }

  override def toString: String = LanguageFactory.printBrch(this)

}

