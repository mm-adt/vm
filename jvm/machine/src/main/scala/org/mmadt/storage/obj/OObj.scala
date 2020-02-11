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

package org.mmadt.storage.obj

import org.mmadt.language.obj.{Inst, Obj, TQ}


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
abstract class OObj(val _name: String, val quantifier: TQ) extends Obj {

  override def q(): TQ = quantifier //
  override def name: String = _name //
  def as(name: String): this.type //

  override def inst(op: String, args: List[Obj]): Inst = throw new UnsupportedOperationException("This shouldn't happen: " + op)
}
