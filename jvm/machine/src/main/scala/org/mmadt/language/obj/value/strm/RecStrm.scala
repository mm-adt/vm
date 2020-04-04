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

import org.mmadt.language.obj.`type`.{RecType, Type}
import org.mmadt.language.obj.value.{RecValue, Value}
import org.mmadt.language.obj.{Obj, Rec}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait RecStrm[A <: Value[Obj],B <: Value[Obj]] extends Strm[Rec[A,B]] with Rec[A,B] {
  override val value:Iterator[RecValue[A,B]]

  override def plus(other:RecType[A,B]):RecType[A,B] = throw new UnsupportedOperationException
  override def plus(other:RecValue[_,_]):this.type = throw new UnsupportedOperationException
  override def get(key:A):B = throw new UnsupportedOperationException
  override def get[BB <: Obj](key:A,btype:BB):BB = throw new UnsupportedOperationException
  override def put(key:A,value:B):RecValue[A,B] = throw new UnsupportedOperationException

}

