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

package org.mmadt.storage.obj.value.strm

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.BoolType
import org.mmadt.language.obj.op.initial.StartOp
import org.mmadt.language.obj.value.BoolValue
import org.mmadt.language.obj.value.strm.BoolStrm
import org.mmadt.language.obj.{IntQ, Obj}
import org.mmadt.storage.StorageFactory.{int, qZero, tbool}
import org.mmadt.storage.obj.value.AbstractVObj

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VBoolStrm(name:String,java:Seq[BoolValue]) extends AbstractVObj(name,java,quantifier = (int(java.length),int(java.length))) with BoolStrm {
  def this(java:Seq[BoolValue]) = this(name = Tokens.bool,java)

  override val value:Iterator[BoolValue] = java.iterator
  override def q(quantifier:IntQ):this.type = this
}

