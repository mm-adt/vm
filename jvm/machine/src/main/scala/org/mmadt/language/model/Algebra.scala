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

package org.mmadt.language.model

import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.{IntType, Type}
import org.mmadt.storage.obj._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object Algebra {
  val ring:Model = Model(
    int + -int -> int.zero(),
    int * int(-1) -> -int,
    (int * int(1)) -> int,
    (int + int(0)) -> (int + int.zero),
    (int + int.zero()) -> int,
    -(-int) -> int,
    (int ~ "x" * (int ~ "y" + int ~ "z")) -> ((int ~ "x" * int ~ "y") + (int ~ "x" * int ~ "z")))

  def ring(atype:Type[Obj]):Model ={
    val t:IntType = atype.asInstanceOf[IntType] // TODO: create a ring type
    Model(
      t + -t -> t.zero(),
      t * t(-1) -> -t,
      (t * t(1)) -> t,
      (t + t(0)) -> (t + t.zero),
      (t + t.zero()) -> t,
      -(-t) -> t)
  }
}
