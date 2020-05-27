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

package org.mmadt.language.obj.value.strm

import org.mmadt.language.LanguageException
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Obj, Rec, RecTuple}
import org.mmadt.storage.obj.value.strm.util.MultiSet

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait RecStrm[A <: Value[Obj], B <: Value[Obj]] extends Strm[Rec[A, B]] with Rec[A, B] {
  override def g: RecTuple[A, B] = throw LanguageException.typeNoGround(this)

  override lazy val hashCode: Int = this.values.hashCode()
  override def equals(other: Any): Boolean = other match {
    case arec: Rec[_, _] => MultiSet.test(this, arec)
    case _ => false
  }
}

