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
import org.mmadt.language.obj.value.Value
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
  def ~>(target:Type[Obj]):Lst[target.type] = this.walk(target)
}
object WalkOp extends Func[Obj, Obj] {
  override val preArgs:Boolean = false
  def apply[A <: Obj](atype:OType[A]):Inst[Obj, Obj] = new VInst[Obj, Obj](g = (Tokens.walk, List(atype)), func = this)
  override def apply(start:Obj, inst:Inst[Obj, Obj]):Lst[Obj] =
    Poly.finalResult(start match {
      case _:Type[_] => lst
      case _:Value[_] => lst(g = (Tokens.`,`,
        WalkOp.resolvePaths(List(asType(start).rangeObj.hardQ(qOne)), inst.arg0[Obj])
          .map(list => lst(g = (Tokens.`;`, list.map(step => step.rangeObj))))))
    }, start, inst)

  /////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////

  val nameTest:(Obj, Obj) => Boolean = (source:Obj, target:Obj) => source.rangeObj.name == target.domainObj.name || __.isAnon(target.domainObj)
  val rangeDomainTest:(Obj, Obj) => Boolean = (source:Obj, target:Obj) => nameTest(source, target) && Type.trueRange(source).rangeObj.test(target.domainObj)
  val objObjTest:(Obj, Obj) => Boolean = (source:Obj, target:Obj) => nameTest(source, target) && source.test(target)

  private def resolvePaths[A <: Obj, B <: Obj](source:List[A], target:B, checked:List[Obj] = Nil, composeTest:(Obj, Obj) => Boolean = rangeDomainTest):List[List[B]] = {
    //.map(t => {println(toBaseName(Type.trueRange(source.last).rangeObj) + "===TESTING==>" + toBaseName(t.domainObj) + " ::: " + Type.trueRange(source.last).rangeObj.test(t.domainObj));t})
    val tail:A = source.last
    if (tail.rangeObj.name == target.rangeObj.name) return if (tail.test(tail.model.findCtype(tail.name).getOrElse(target))) List(List(target)) else Nil
    tail.model.definitions // TODO: index by name
      .filter(t => !checked.contains(t))
      .filter(t => composeTest(tail, t))
      .flatMap(t => {
        val nextT = tail ~~> t // asType(source.last)
        if (nextT.rangeObj.name == target.rangeObj.name) List(source :+ nextT)
        else if (!tail.root || (tail != nextT)) resolvePaths(source :+ t, target, checked :+ t)
        else Nil
      })
      .filter(list => list.nonEmpty)
      .asInstanceOf[List[List[B]]]
  }

  def walkSourceToTarget[A <: Obj](source:Obj, target:A):A = walkSourceToTarget(source, target, rangeDomainTest)
  def walkSourceToTarget[A <: Obj](source:Obj, target:A, composeTest:(Obj, Obj) => Boolean = rangeDomainTest):A = {
    source match {
      case astrm:Strm[Obj] => astrm(x => walkSourceToTarget[A](x, target))
      case _ => Obj.resolveTokenOption(source, target).getOrElse({
        if (source.isInstanceOf[Type[_]] || !__.isToken(target)) return target
        WalkOp.resolvePaths[Obj, A](List(source), target, composeTest = composeTest)
          .headOption
          .map(path => path.foldLeft(source)((a, b) => (a `=>` toBaseName(b)).named(b.name, ignoreAnon = true)))
          .getOrElse {
            if (!Tokens.named(target.name)) target
            else if (source.model.search[A](target = target).nonEmpty) throw LanguageException.typingError(source, asType(target))
            else throw LanguageException.labelNotFound(source, target.name)
          }.asInstanceOf[A]
      })
    }
  }
}