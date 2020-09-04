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
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.language.obj.value.Value
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageFactory.lst
import org.mmadt.storage.obj.value.VInst

import scala.util.{Failure, Success, Try}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait WalkOp {
  this: Obj =>
  def walk(target: Type[Obj]): Lst[target.type] = WalkOp(target).exec(this).asInstanceOf[Lst[target.type]]
  def ~>(target: Type[Obj]): Lst[target.type] = this.walk(target)
}
object WalkOp extends Func[Obj, Obj] {
  override val preArgs: Boolean = false
  def apply[A <: Obj](atype: OType[A]): Inst[Obj, Obj] = new VInst[Obj, Obj](g = (Tokens.walk, List(atype)), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Lst[Obj] =
    (start match {
      case _: Type[_] => lst
      case _: Value[_] => lst(g = (Tokens.`,`, WalkOp.resolvePaths(start.model, List(asType(start).rangeObj), inst.arg0[Obj]).map(list => lst(g = (Tokens.`;`, list.map(step => step.rangeObj))))))
    }).via(start, inst)

  /*
  TODO: Lst.test()/equals() needs to determine equality different for different forms (just sort order on ,-lst)
   */
  def resolvePaths[A <: Obj, B <: Obj](model: Model, source: List[A], target: B, checked: List[Obj] = Nil): List[List[B]] = {
    if (source.last.rangeObj.name == target.rangeObj.name) {
      if (source.last.test(target.range)) return List(List(target)) else return Nil
    }
    model.definitions
      .filter(t => !checked.contains(t))
      .filter(t => {
        //println(toBaseName(Type.trueRange(source.last).rangeObj) + "===TESTING==>" + toBaseName(t.domainObj) + " ::: " + Type.trueRange(source.last).rangeObj.test(t.domainObj))
        source.last.rangeObj.name == t.domainObj.name && Type.trueRange(source.last).rangeObj.test(t.domainObj)
      })
      .flatMap(t => {
        val nextT = asType(source.last) ~~> t
        if (nextT.rangeObj.name == target.rangeObj.name)
          List(source :+ nextT)
        else if (!source.last.root || (source.last != nextT))
          resolvePaths(model, source :+ t, target, checked :+ t)
        else Nil
      })
      .filter(list => list.nonEmpty)
      .asInstanceOf[List[List[B]]]
  }

  def resolveTokenPath[A <: Obj](obj: Obj, arg: A): A = {
    if (!__.isToken(arg)) return arg
    Obj.resolveTokenOption(obj, arg).getOrElse({
      if (obj.isInstanceOf[Type[_]]) return arg
      Try[Obj]({
        WalkOp
          .resolvePaths[Obj, Obj](obj.model, List(obj), arg)
          .headOption
          .map(path => path.foldLeft(obj)((a, b) => (a `=>` toBaseName(b)).named(b.name, ignoreAnon = true))).get
      }) match {
        case y: Success[A] => y.value
        case _: Failure[Obj] => if (obj.model.search[A](target = arg).nonEmpty)
          throw LanguageException.typingError(obj, asType(arg))
        else
          throw LanguageException.labelNotFound(obj, arg.name)
      }
    })
  }
}