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

package org.mmadt.language.obj

import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.map.{AppendOp, HeadOp, TailOp}
import org.mmadt.storage.StorageFactory._

trait Lst[A <: Obj] extends Obj
  with AppendOp[A]
  with HeadOp[A]
  with TailOp[A]
  with Type[Lst[A]] {
  def value(): (Lst[A], A)
}

object Lst {
  def encode[A <: Obj](seq: Seq[A]): Lst[A] = seq.foldRight(lst[A])((a,b) => b.append(a))
  def decode[A <: Obj](alist: Lst[A]): List[A] = if (alist.value() == null) List.empty[A] else  alist.value()._2 +: decode(alist.value()._1)
}