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

package org.mmadt.language.obj.op.map

import org.mmadt.language.Tokens
import org.mmadt.language.obj.op.map.PathOp.Path
import org.mmadt.language.obj.{Prod, _}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait PathOp {
  this: Obj =>
  def path(): Path = PathOp().exec(this)
}

object PathOp {
  private type Path = Prod[Obj]
  def apply(): PathInst = new PathInst

  class PathInst(q: IntQ = qOne) extends VInst[Obj, Path]((Tokens.path, Nil), q) {
    override def q(q: IntQ): this.type = new PathInst(q).asInstanceOf[this.type]
    override def exec(start: Obj): Path = prod(start.lineage.foldRight(List.empty[Obj])((a, b) => a._1 +: b) :+ start: _*).via(start, this)
  }

}