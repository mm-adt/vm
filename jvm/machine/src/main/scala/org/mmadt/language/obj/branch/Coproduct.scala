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
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, InstTuple, Obj}
import org.mmadt.storage.StorageFactory.obj

trait Coproduct[A <: Obj] extends Branching[A]
  with Type[Coproduct[A]]
  with Value[Coproduct[A]]
  with Inst[A, Coproduct[A]] {

  override val value: InstTuple

  override def test(other: Obj): Boolean = other match {
    case prod: Coproduct[_] =>
      if (prod.value._2.isEmpty || this.value.equals(prod.value)) return true
      this.value._2.zip(prod.value._2).foldRight(true)((a, b) => a._1.test(a._2) && b)
    case _ => false
  }

  override def exec(start: A): this.type = {
    var found = false
    this.clone(value = (this.value._1, this.value._2.map(x => Option(if(start.test(x)) Inst.resolveArg(start, x) else obj.q(0)).filter(x => x.alive() && !found).map(x => {found = true; x }).getOrElse(obj.q(0)))))
  }


}

