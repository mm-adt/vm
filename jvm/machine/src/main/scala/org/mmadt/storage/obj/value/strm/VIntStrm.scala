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
import org.mmadt.language.obj.TQ
import org.mmadt.language.obj.`type`.IntType
import org.mmadt.language.obj.value.IntValue
import org.mmadt.language.obj.value.strm.IntStrm
import org.mmadt.storage.obj.{OObj, _}
import org.mmadt.storage.obj.`type`.TInt

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VIntStrm(java: Seq[IntValue]) extends OObj(quantifier = (java.length, java.length))
  with IntStrm {
  override def value(): Iterator[IntValue] = java.iterator //
  override def start(): IntType = new TInt(List((new TInt(Nil, qZero), inst(Tokens.start, this))), q()) //
  override def q(quantifier: TQ): this.type = new VIntStrm(java).asInstanceOf[this.type] //
}
