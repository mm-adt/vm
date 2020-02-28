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

import org.mmadt.language.obj.`type`.RecType
import org.mmadt.language.obj.op._
import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.op.map.{EqsOp, GetOp, PlusOp}
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.op.traverser.ToOp
import org.mmadt.language.obj.value.RecValue
import org.mmadt.storage.obj.value.VRec

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Rec[A <: Obj,B <: Obj] extends Obj
  with EqsOp[Rec[A,B]]
  with PlusOp[Rec[A,B]]
  with IsOp[Rec[A,B]]
  with ToOp[Rec[A,B]]
  with GetOp[A,B]
  with PutOp[A,B]

object Rec {
  implicit def mapToRec[A <: Obj,B <: Obj](java:Map[A,B]):RecValue[A,B] with Rec[A,B] = new VRec(java) //

  /*implicit final class ColonAssoc[A](private val self:A) extends AnyVal {
    @inline
    def -:[B](y:B):Tuple2[A,B] = Tuple2(self,y)
  }*/
}
