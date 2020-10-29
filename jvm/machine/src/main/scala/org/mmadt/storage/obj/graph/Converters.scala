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

package org.mmadt.storage.obj.graph

import org.mmadt.language.LanguageException
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.op.OpInstResolver
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Bool, Inst, Int, Lst, Obj, Poly, Real, Rec, Str, asType}
import org.mmadt.storage.StorageFactory.{bool, int, real, str}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object Converters {

  def objConverter(source:Obj, target:Obj):Stream[Obj] = {
    (source match {
      case abool:Bool => Stream(boolConverter(abool, target))
      case aint:Int => Stream(intConverter(aint, target))
      case areal:Real => Stream(realConverter(areal, target))
      case astr:Str => Stream(strConverter(astr, target))
      case alst:Lst[Obj] => lstConverter(alst, target)
      case arec:Rec[Obj, Obj] => recConverter(arec, target)
      case _:__ => Stream(source)
    }).filter(_.alive) //.map(x => target.trace.reconstruct[Obj](x))
  }

  private def boolConverter(source:Bool, target:Obj):Obj = target.domain match {
    case _:__ => source
    case abool:BoolType => bool(name = abool.name, g = source.g, via = source.via)
    case astr:StrType => str(name = astr.name, g = source.g.toString, via = source.via)
    case _ => throw LanguageException.typingError(source, asType(target))
  }

  private def intConverter(source:Int, target:Obj):Obj = target match {
    case _:__ => source
    case aint:IntType => int.clone(name = aint.name, via = source.via)
    case areal:RealType => real.clone(name = areal.name, via = source.via)
    case astr:StrType => str.clone(name = astr.name, via = source.via)
    case _ => throw LanguageException.typingError(source, asType(target))
  }

  private def realConverter(source:Real, target:Obj):Obj = target.domain match {
    case _:__ => source
    case aint:IntType => int.clone(name = aint.name, via = source.via)
    case areal:RealType => real.clone(name = areal.name, via = source.via)
    case astr:StrType => str.clone(name = astr.name, via = source.via)
    case _ => throw LanguageException.typingError(source, asType(target))
  }

  private def strConverter(source:Str, target:Obj):Obj = target.domain match {
    case _:__ => source
    case abool:BoolType => bool.clone(name = abool.name, via = source.via)
    case aint:IntType => int.clone(name = aint.name, via = source.via)
    case areal:RealType => real.clone(name = areal.name, via = source.via)
    case astr:StrType => str.clone(name = astr.name, via = source.via)
    case _ => throw LanguageException.typingError(source, asType(target))
  }

  private def lstConverter(source:Lst[Obj], target:Obj):Stream[Obj] = target match {
    case _:__ => Stream(source)
    case _:StrType => Stream(str(name = target.name, g = source.toString, via = source.via))
    case _:Inst[Obj, Obj] => Stream(OpInstResolver.resolve(source.g._2.head.asInstanceOf[StrValue].g, source.g._2.tail))
    case blst:Lst[Obj] if lstTest(source, blst) =>
      source.glist.zip(blst.glist).flatMap(pair => pair._1.coercions2(pair._2))
        .foldLeft(List.empty[Obj])((a, b) => a :+ b)
        .combinations(source.size).toStream.distinct
        .filter(x => x.size == source.size)
        .filter(x => x.forall(_.alive))
        .map(x => source.combine(source.clone(_ => x)).named(blst.name))
    case _ => Stream(source)
  }

  private def recConverter(source:Rec[Obj, Obj], target:Obj):Stream[Obj] = target match {
    case _:__ => Stream(source)
    case bstr:StrType => Stream(str(name = bstr.name, g = source.toString, via = source.via))
    case brec:Rec[Obj, Obj] => source.gmap.flatMap(a => brec.gmap
      .flatMap(b => cartesianProduct(List(a._1.coercions2(b._1), a._2.coercions2(b._2)))))
      .map(b => (b.head, b.last))
      .combinations(source.size)
      .toStream
      .distinct
      .map(x => source.clone(name = brec.name, g = (brec.gsep, x)))
      .filter(x => x.gmap.size >= brec.gmap.count(x => x._2.q._1.g > 0))
    case _ => Stream.empty[Obj]
  }

  ////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////

  private def lstTest(alst:Lst[Obj], bobj:Obj):Boolean = bobj match {
    case blst:Lst[Obj] => blst.ctype || (Poly.sameSep(alst, blst) && alst.size == blst.size &&
      !alst.glist.zip(blst.glist).forall(p => __.isAnon(p._1) || p._1.name.equals(p._2.name)))
    case _ => false
  }

  private def cartesianProduct[T](in:Seq[Seq[T]]):Seq[Seq[T]] = {
    @scala.annotation.tailrec
    def loop(acc:Seq[Seq[T]], rest:Seq[Seq[T]]):Seq[Seq[T]] = {
      rest match {
        case Nil => acc
        case seq :: remainingSeqs =>
          val next = for {
            i <- seq
            a <- acc
          } yield i +: a
          loop(next, remainingSeqs)
      }
    }
    loop(Seq(Nil), in.reverse)
  }
}
