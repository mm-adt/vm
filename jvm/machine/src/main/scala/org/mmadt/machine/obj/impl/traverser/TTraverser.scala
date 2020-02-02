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

package org.mmadt.machine.obj.impl.traverser

import org.mmadt.language.Tokens
import org.mmadt.machine.obj.theory.obj.`type`.Type
import org.mmadt.machine.obj.theory.obj.value.IntValue
import org.mmadt.machine.obj.theory.traverser.Traverser

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TTraverser[O](obj: O) extends Traverser[O] {

  override def obj(): O = obj

  override def apply(t: Type[_]): Traverser[_] = {
    t.inst().op() match {
      case Tokens.plus => new TTraverser[IntValue](this.obj.asInstanceOf[IntValue].plus(t.inst().arg().asInstanceOf[IntValue]))
      case Tokens.mult => new TTraverser[IntValue](this.obj.asInstanceOf[IntValue].mult(t.inst().arg().asInstanceOf[IntValue]))
      case _ => throw new RuntimeException("Unknown instruction: " + t);
    }
  }
}
