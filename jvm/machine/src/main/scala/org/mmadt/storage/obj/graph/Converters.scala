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

import java.lang.{Boolean => JBoolean, Double => JDouble, Long => JLong}

import org.mmadt.language.LanguageException
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.op.OpInstResolver
import org.mmadt.language.obj.op.branch.CombineOp
import org.mmadt.language.obj.value._
import org.mmadt.storage.StorageFactory.{bool, int, lst, real, str, zeroObj}

import scala.util.Try

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object Converters {

  def objConverter(source:Obj, target:Obj):Stream[Obj] = {
    val itarget = target.inflate[Obj](source.model)
    (source.inflate[Obj](source.model) match {
      case abool:BoolValue => Stream(boolConverter(abool, itarget))
      case abool:BoolType => Stream(boolConverter(abool, itarget))
      case aint:IntValue => Stream(intConverter(aint, itarget))
      case aint:IntType => Stream(intConverter(aint, itarget))
      case areal:RealValue => Stream(realConverter(areal, itarget))
      case areal:RealType => Stream(realConverter(areal, itarget))
      case astr:StrValue => Stream(strConverter(astr, itarget))
      case astr:StrType => Stream(strConverter(astr, itarget))
      case alst:Lst[Obj] => lstConverter(alst, itarget)
      case arec:Rec[Obj, Obj] => recConverter(arec, itarget)
      case _:__ => Stream(source)
      case _ => Stream(source) // strm weirdness
    }).filter(_.alive) // .map(x => x.update(source.model)) //.map(x => target.trace.reconstruct[Obj](x))
  }

  private def boolConverter(source:BoolValue, target:Obj):Obj = target match {
    case _:__ => source
    case _:BoolType => bool(name = target.name, g = source.g, q = source.q, via = source.via)
    case _:StrType => str(name = target.name, g = source.g.toString, q = source.q, via = source.via)
    case _ => throw LanguageException.typingError(source, asType(target))
  }

  private def boolConverter(source:BoolType, target:Obj):Obj = target match {
    case _:__ => source
    case _:BoolType => bool.clone(name = target.name, q = source.q, via = source.via)
    case _:StrType => str.clone(name = target.name, q = source.q, via = source.via)
    case _ => throw LanguageException.typingError(source, asType(target))
  }

  private def intConverter(source:IntValue, target:Obj):Obj = target match {
    case _:__ => source
    case _:IntType => int(name = target.name, g = source.g, q = source.q, via = source.via)
    case _:RealType => real(name = target.name, g = source.g, q = source.q, via = source.via)
    case _:StrType => str(name = target.name, g = source.g.toString, q = source.q, via = source.via)
    case _ => throw LanguageException.typingError(source, asType(target))
  }


  private def intConverter(source:IntType, target:Obj):Obj = target match {
    case _:__ => source
    case _:IntType => int.clone(name = target.name, q = source.q, via = source.via)
    case _:RealType => real.clone(name = target.name, q = source.q, via = source.via)
    case _:StrType => str.clone(name = target.name, q = source.q, via = source.via)
    case _ => throw LanguageException.typingError(source, asType(target))
  }

  private def realConverter(source:RealValue, target:Obj):Obj = target match {
    case _:__ => source
    case _:IntType => int(name = target.name, g = source.g.longValue(), q = source.q, via = source.via)
    case _:RealType => real(name = target.name, g = source.g, q = source.q, via = source.via)
    case _:StrType => str(name = target.name, g = source.g.toString, q = source.q, via = source.via)
    case _ => throw LanguageException.typingError(source, asType(target))
  }

  private def realConverter(source:RealType, target:Obj):Obj = target match {
    case _:__ => source
    case _:IntType => int.clone(name = target.name, q = source.q, via = source.via)
    case _:RealType => real.clone(name = target.name, q = source.q, via = source.via)
    case _:StrType => str.clone(name = target.name, q = source.q, via = source.via)
    case _ => throw LanguageException.typingError(source, asType(target))
  }

  private def strConverter(source:StrValue, target:Obj):Obj = target match {
    case _:__ => source
    case _:BoolType => Try(JBoolean.valueOf(source.g)).map(i => bool(name = target.name, g = i, q = source.q, via = source.via)).getOrElse(zeroObj)
    case _:IntType => Try(JLong.valueOf(source.g)).map(i => int(name = target.name, g = i, q = source.q, via = source.via)).getOrElse(zeroObj)
    case _:RealType => Try(JDouble.valueOf(source.g)).map(i => real(name = target.name, g = i, q = source.q, via = source.via)).getOrElse(zeroObj)
    case _:StrType => str(name = target.name, g = source.g, q = source.q, via = source.via)
    case _ => throw LanguageException.typingError(source, asType(target))
  }

  private def strConverter(source:StrType, target:Obj):Obj = target match {
    case _:__ => source
    case _:BoolType => bool.clone(name = target.name, via = source.via)
    case _:IntType => int.clone(name = target.name, via = source.via)
    case _:RealType => real.clone(name = target.name, via = source.via)
    case _:StrType => str.clone(name = target.name, via = source.via)
    case _ => throw LanguageException.typingError(source, asType(target))
  }

  private def lstConverter(source:Lst[Obj], target:Obj):Stream[Obj] = target match {
    case _:__ => Stream(source)
    case _:StrType => Stream(str(name = target.name, g = source.toString, via = source.via))
    case _:Inst[Obj, Obj] => Stream(OpInstResolver.resolve(source.g._2.head.asInstanceOf[StrValue].g, source.g._2.tail))
    case blst:Lst[Obj] if lstTest(source, blst) => source match {
      case _:LstType[_] =>
        source.glist.zip(blst.glist).flatMap(pair => pair._1.coercions(pair._2))
          .foldLeft(List.empty[Obj])((a, b) => a :+ b)
          .combinations(source.size).toStream.distinct
          .filter(x => x.size == source.size)
          .filter(x => x.forall(_.alive))
          .map(x => {
            if (source.glist == x) source
            else source.combine(source.clone(_ => x))
          })
          .map(x => x.named(blst.name).reload)
      case _:LstValue[_] =>
        val clst = lst(name = source.name, g = (blst.gsep, source.glist.zip(blst.glist).map(a => a._1.coerce(a._2))), via = source.via)
        Stream((if (Lst.exactTest(clst, blst.domainObj)) CombineOp.combineAlgorithm(clst, blst, withAs = false) else clst).reload)
    }
    case _ => Stream.empty
  }
  private def recConverter(source:Rec[Obj, Obj], target:Obj):Stream[Obj] = target match {
    case _:__ => Stream(source)
    case bstr:StrType => Stream(str(name = bstr.name, g = source.toString, via = source.via))
    case brec:Rec[_, _] if brec.ctype => Stream(source.named(brec.name))
    case brec:Rec[Obj, Obj] => source.gmap
      .flatMap(a => brec.gmap.flatMap(b => cartesianProduct(List(a._1.coercions(b._1), a._2.coercions(b._2)))))
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
    case blst:Lst[Obj] => (Poly.sameSep(alst, blst) && alst.size == blst.size) //&&
    //  !alst.glist.zip(blst.glist).forall(p => __.isAnon(p._1) || p._1.name.equals(p._2.name)))
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
