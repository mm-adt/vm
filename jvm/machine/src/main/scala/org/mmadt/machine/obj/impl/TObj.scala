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

package org.mmadt.machine.obj.impl

import org.mmadt.language.Stringer
import org.mmadt.machine.obj.impl.VInst._id
import org.mmadt.machine.obj.{Inst, JQ, qOne}

/**
  * @author Marko A. Rodriguez (http://markorodriguez.com)
  */
class TObj(jvm: List[Inst], quantifier: JQ) extends OObj(jvm, quantifier)  {

  def this(jvm: List[Inst]) = this(jvm, qOne)

  def this() = this(List(_id))

  override def toString: String = Stringer.t(this) + Stringer.q(this.q())
}

object TObj {

  object obj extends TObj()

}