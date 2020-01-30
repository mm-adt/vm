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

import org.mmadt.machine.obj.impl.VBool.{boolF, boolT}
import org.mmadt.machine.obj.{Bool, Inst, TQ, qOne}

/**
  * @author Marko A. Rodriguez (http://markorodriguez.com)
  */
class TBool(jvm: List[VInst], quantifier: TQ) extends TObj(jvm, quantifier) with Bool {

  def this(jvm: List[VInst]) = this(jvm, qOne)

  def this() = this(List(), qOne)

  override def or(other: Bool): Bool = new TBool(this.jvm ++ otherInst(other))

  override def and(other: Bool): Bool = new TBool(this.jvm ++ otherInst(other))

  override def zero(): Bool = boolT

  override def one(): Bool = boolF


}

object TBool {

  object bool extends TBool

}