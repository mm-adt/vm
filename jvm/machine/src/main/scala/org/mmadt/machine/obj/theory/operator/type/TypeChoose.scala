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

package org.mmadt.machine.obj.theory.operator.`type`

import org.mmadt.language.Tokens
import org.mmadt.machine.obj.theory.obj.`type`.Type
import org.mmadt.machine.obj.theory.obj.value.RecValue

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait TypeChoose[T <: Type[T]] extends Type[T] {
  def choose[T1 <: Type[T1], T2 <: Type[T2], TE <: Type[TE]](c1: (T1, TE), c2: (T2, TE)): TE = this.choose[T1, T2, TE](rec(Map(c1, c2).asInstanceOf[Map[T1 with T2, TE]]))

  def choose[T1 <: Type[T1], T2 <: Type[T2], TE <: Type[TE]](branches: RecValue[T1 with T2, TE]): TE = this.push(branches.value().head._2, inst(Tokens.choose, branches))

}
