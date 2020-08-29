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

package org.mmadt.language.obj.op.map

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.storage.StorageFactory.lst
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait WalkOp {
  this: Obj =>
  def walk(target: Type[Obj]): Lst[target.type] = WalkOp(target).exec(this).asInstanceOf[Lst[target.type]]
}
object WalkOp extends Func[Obj, Obj] {
  override val preArgs: Boolean = false
  def apply[A <: Obj](atype: OType[A]): Inst[Obj, Obj] = new VInst[Obj, Obj](g = (Tokens.walk, List(atype)), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Lst[Obj] =
    lst(g = (Tokens.`,`, WalkOp
      .resolvePaths(start.model, List(asType(start)), inst.arg0[Obj])
      .map(list => lst(g = (Tokens.`;`, list)))))
      .via(start, inst)

  def resolvePaths[A <: Obj, B <: Obj](model: Model, source: List[A], target: B, checked: List[Obj] = Nil): List[List[B]] = {
    model.definitions
      .filter(t => !checked.contains(t))
      //.filter(_ => !source.name.equals(target.name))
      .filter(t => source.last.rangeObj.name.equals(t.domainObj.name))
      .filter(t => asType(source.last).test(t))
      .map(t => {
        //val nextT = asType(source) `=>` t
        if (t.rangeObj.name == target.rangeObj.name) {
          source :+ t
        } else {
          resolvePaths(model, source :+ t, target, checked :+ t)
        }
      }).asInstanceOf[List[List[B]]]
  }
}