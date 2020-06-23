/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language.obj.`type`
import org.mmadt.language.LanguageFactory
import org.mmadt.language.obj.op.branch.{MergeOp, SplitOp}
import org.mmadt.language.obj.{Inst, Obj, Rec}
import org.mmadt.storage.StorageFactory.rec

trait RecType[A <: Obj, B <: Obj]
  extends Type[Obj]
    with ObjType
    with Inst[B, Obj]
    with Rec[A, B] {
  override def toString: String = LanguageFactory.printType(this)
  override def exec(start: B): Obj = MergeOp().exec(SplitOp(rec(this.gsep, this.gmap.asInstanceOf[Map[Obj, Obj]])).exec(start)).clone(via = (start, this))
  override def test(other: Obj): Boolean = RecType.super.test(other)
  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.g.hashCode()
  override def equals(other: Any): Boolean = RecType.super.equals(other)
}



