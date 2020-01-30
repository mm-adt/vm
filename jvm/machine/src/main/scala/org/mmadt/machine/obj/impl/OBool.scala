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

import org.mmadt.machine.obj.impl.OBool.{False, True}
import org.mmadt.machine.obj.{Bool, JQ, qOne}

/**
  * @author Marko A. Rodriguez (http://markorodriguez.com)
  */
class OBool(jvm: Boolean, quantifier: JQ) extends OObj[Boolean](jvm, quantifier) with Bool {

  def this(jvm: Boolean) = this(jvm, qOne)

  override def or(other: Bool): Bool = new OBool(this.jvm || other._jvm())

  override def and(other: Bool): Bool = new OBool(this.jvm && other._jvm())

  override def zero(): Bool = True

  override def one(): Bool = False
}

object OBool {

  object True extends OBool(true)

  object False extends OBool(false)

}