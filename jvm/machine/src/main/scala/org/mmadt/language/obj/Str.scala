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

package org.mmadt.language.obj

import org.mmadt.language.obj.`type`.StrType
import org.mmadt.language.obj.op._
import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.op.map.{EqsOp, GtOp, PlusOp}
import org.mmadt.language.obj.op.traverser.ToOp
import org.mmadt.language.obj.value.StrValue
import org.mmadt.storage.obj.value.VStr

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Str extends Obj
  with EqsOp[Str]
  with PlusOp[Str]
  with GtOp[Str]
  with IsOp[Str]
  with ToOp[Str] {

}

object Str {
  implicit def stringToStr(java:String):StrValue with Str = new VStr(java) //
}
