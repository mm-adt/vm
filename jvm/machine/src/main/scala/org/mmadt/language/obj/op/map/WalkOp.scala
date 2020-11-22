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

import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageFactory.{lst, qOne}
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait WalkOp {
  this:Obj =>
  def walk(target:Type[Obj]):Lst[target.type] = WalkOp(target).exec(this).asInstanceOf[Lst[target.type]]
  def ~~>(target:Type[Obj]):Lst[target.type] = this.walk(target)
}
object WalkOp extends Func[Obj, Obj] {
  override val preArgs:Boolean = false
  def apply[A <: Obj](atype:OType[A]):Inst[Obj, Obj] = new VInst[Obj, Obj](g = (Tokens.walk, List(atype)), func = this)
  override def apply(start:Obj, inst:Inst[Obj, Obj]):Lst[Obj] =
    (start match {
      case _:__ => lst
      case _ => lst(g = (Tokens.`,`,
        start.model.graph.paths(asType(start).rangeObj.hardQ(qOne), inst.arg0[Obj])
          .map(list => lst(g = (Tokens.`;`, list.map(step => step.rangeObj)))).toList))
    }).via(start, inst)

  /////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////

  def walkSourceToTarget[A <: Obj](source:Obj, target:A, targetName:Boolean = false):A = {
    source match {
      case astrm:Strm[Obj] => astrm(x => walkSourceToTarget[A](x, target))
      case _ => Obj.resolveTokenOption(source, target, !targetName).getOrElse({
        if (source.isInstanceOf[Type[_]] || !__.isToken(target)) return target
        Some(source.coerce(target)).filter(_.alive).getOrElse {
          if (source.model.graph.exists(target)) throw LanguageException.typingError(source, asType(target))
          else throw LanguageException.labelNotFound(source, target.name)
        }
      })
    }
  }
}
