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
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory.zeroObj
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait WalkOp {
  this: Obj =>
  def walk(atype: Type[Obj]): atype.type = WalkOp(atype).exec(this).asInstanceOf[atype.type]
}
object WalkOp extends Func[Obj, Obj] {
  override val preArgs: Boolean = false
  def apply[A <: Obj](atype: OType[A]): Inst[Obj, Obj] = new VInst[Obj, Obj](g = (Tokens.walk, List(atype)), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    val target: Type[Obj] = inst.arg0[Type[Obj]]
    start match {
      case _: Type[_] => start.via(start, inst).named(target.name)
      case _: Value[_] => WalkOp
        .resolvePaths(start, inst.arg0[Obj])
        .sortBy(x => x.trace.size)
        .map(x => start ~~> toBaseName(x))
        .find(_.alive)
        .map(x => x.via(start, inst))
        .getOrElse(zeroObj)
    }
  }
  def resolvePaths[A <: Obj, B <: Obj](source: A, target: B, checked: List[Obj] = Nil): List[B] = {
    source.model.definitions
      .filter(t => !checked.contains(t))
      //.filter(_ => !source.name.equals(target.name))
      .filter(t => source.rangeObj.name.equals(t.domainObj.name) || baseName(source.rangeObj).equals(baseName(t.domainObj)))
      .filter(t => asType(source).test(t))
      .flatMap(t => {
        val nextT = asType(source) `=>` t
        if (t.rangeObj.name == target.rangeObj.name) {
          List(nextT.asInstanceOf[B])
        } else {
          resolvePaths(nextT.model(source.model), target, checked :+ t)
        }
      })
  }
}