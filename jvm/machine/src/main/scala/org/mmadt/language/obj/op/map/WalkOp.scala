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
import org.mmadt.language.obj.op.trace.AsOp
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageFactory.{lst, qOne}
import org.mmadt.storage.obj.value.VInst

import scala.util.Try

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait WalkOp {
  this:Obj =>
  def walk(target:Type[Obj]):Lst[target.type] = WalkOp(target).exec(this).asInstanceOf[Lst[target.type]]
  def ~>(target:Type[Obj]):Lst[target.type] = this.walk(target)
}
object WalkOp extends Func[Obj, Obj] {
  override val preArgs:Boolean = false
  def apply[A <: Obj](atype:OType[A]):Inst[Obj, Obj] = new VInst[Obj, Obj](g = (Tokens.walk, List(atype)), func = this)
  override def apply(start:Obj, inst:Inst[Obj, Obj]):Lst[Obj] =
    Poly.finalResult(start match {
      case _:Type[_] => lst
      case _:Value[_] => lst(g = (Tokens.`,`,
        start.model.graph.path(asType(start).rangeObj.hardQ(qOne), inst.arg0[Obj]).toList
          .map(list => lst(g = (Tokens.`;`, list.map(step => step.rangeObj))))))
    }, start, inst)

  /////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////

  def testSourceToTarget(source:Obj, target:Obj):Boolean =
    source.model.graph.path(source, target).nonEmpty && Try[Boolean]((source `=>` target).alive).getOrElse(false)
  def walkSourceToTarget[A <: Obj](source:Obj, target:A, targetName:Boolean = false):A = {
    val result:A = source match {
      case astrm:Strm[Obj] => astrm(x => walkSourceToTarget[A](x, target))
      case _ if !target.named => target // NEED A PATH RESOLVER FOR BASE TYPES TO AVOID STACK ISSUES
      case _ => Obj.resolveTokenOption(source, target).getOrElse({
        if (source.isInstanceOf[Type[_]] || !AsOp.searchable(target)) return target
        source.model.graph.fpath(source, target).headOption
          .getOrElse {
            if (source.model.search[A](__, target).nonEmpty) throw LanguageException.typingError(source, asType(target))
            else throw LanguageException.labelNotFound(source, target.name)
          }.asInstanceOf[A]
      })
    }
    if (targetName) result.named(target.name) else result
  }
}