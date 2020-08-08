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

package org.mmadt.language.obj.op.trace

import java.lang.{Boolean => JBoolean, Double => JDouble, Long => JLong}

import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Inst, _}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait AsOp {
  this: Obj =>
  def as[O <: Obj](obj: O): O = AsOp(obj).exec(this).asInstanceOf[O]
}

object AsOp extends Func[Obj, Obj] {
  def apply[O <: Obj](obj: Obj): Inst[O, O] = new VInst[O, O](g = (Tokens.as, List(obj.asInstanceOf[O])), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    if (start.isInstanceOf[Strm[_]]) return start.via(start, inst)
    internalConvertAs(start, inst.arg0[Obj]).via(start, inst)
  }

  def autoAsType[E <: Obj](source: Obj, f: Obj => Obj, target: Obj): E = autoAsType(f(autoAsType(source, target.domain, target, domain = true)), target.range, target, domain = false).asInstanceOf[E]

  private def autoAsType(source: Obj, target: Obj, rangeType: Obj, domain: Boolean): Obj = {
    if (!target.alive) return zeroObj
    if (!source.alive) return source
    source match {
      case value: Strm[Obj] => value(x => AsOp.autoAsType(x, target, rangeType, domain))
      case _ =>
        if (source.rangeObj.equals(target.rangeObj) || __.isAnon(target) || Obj.fetch(source, __, target.name).exists(x => Tokens.to == x._1)) source
        else if (baseName(target).equals(baseName(source))) source.named(target.name)
        else {
          source match {
            case _: Value[_] =>
              if (!__.isToken(target) || source.name.equals(target.name)) source
              else internalConvertAs(source.model(rangeType.model), target).hardQ(source.q)
            case _: Type[_] if domain => if (!__.isToken(target)) source else Obj.copyDefinitions(source, target) // TODO: def/model equality issues
            case _: Type[_] => target <= source
          }
        }
    }
  }

  private def internalConvertAs(source: Obj, target: Obj): Obj = {
    val asObj: Obj = if (source.isInstanceOf[Type[_]]) target.model(source.model) else Inst.resolveToken(source, target)
    val dObj: Obj = pickMapping(source, asObj)
    val rObj: Obj = if (asObj.domain != asObj.range) pickMapping(dObj, asObj.range) else dObj
    val result = (if (Tokens.named(target.name)) rObj.named(target.name) else rObj)
    if (!result.alive) throw LanguageException.typingError(source, asType(asObj.named(target.name)))
    result
  }

  private def pickMapping(start: Obj, asObj: Obj): Obj = {
    if (asObj.isInstanceOf[Value[Obj]]) Inst.resolveArg(start, asObj)
    else {
      val defined = Obj.fetch[Obj](start, start, asObj.name).map(x => x._2)
      start match {
        case _: Type[Obj] => asObj
        case _ if defined.isDefined => Inst.resolveArg(start, defined.get)
        case abool: Bool => boolConverter(abool, asObj)
        case aint: Int => intConverter(aint, asObj)
        case areal: Real => realConverter(areal, asObj)
        case astr: Str => strConverter(astr, asObj)
        case alst: Lst[Obj] => lstConverter(alst, asObj)
        case arec: Rec[Obj, Obj] => recConverter(arec, asObj)
      }
    }
  }
  private def boolConverter(x: Bool, y: Obj): Obj = {
    Inst.resolveArg(y.domain match {
      case _: __ => x
      case abool: BoolType => vbool(name = abool.name, g = x.g, via = x.via)
      case astr: StrType => vstr(name = astr.name, g = x.g.toString, via = x.via)
      case _ => throw LanguageException.typingError(x, asType(y))
    }, y)
  }

  private def intConverter(x: Int, y: Obj): Obj = {
    val w: Obj = Inst.resolveToken(x, y).domain match {
      case _: __ => x
      case aint: IntType => vint(name = aint.name, g = x.g, via = x.via)
      case areal: RealType => vreal(name = areal.name, g = x.g, via = x.via)
      case astr: StrType => vstr(name = astr.name, g = x.g.toString, via = x.via)
      case _: ObjType => x
      case _ => throw LanguageException.typingError(x, asType(y))
    }
    y.trace.map(x => x._2).foldLeft(w)((x, y) => y.exec(x))
  }

  private def realConverter(x: Real, y: Obj): Obj = {
    Inst.resolveArg(y.domain match {
      case _: __ => x
      case aint: IntType => vint(name = aint.name, g = x.g.longValue(), via = x.via)
      case areal: RealType => vreal(name = areal.name, g = x.g, via = x.via)
      case astr: StrType => vstr(name = astr.name, g = x.g.toString, via = x.via)
      case _ => throw LanguageException.typingError(x, asType(y))
    }, y)
  }

  private def strConverter(x: Str, y: Obj): Obj = {
    val w: Obj = Inst.resolveToken(x, y).domain match {
      case _: __ => x
      case abool: BoolType => vbool(name = abool.name, g = JBoolean.valueOf(x.g), via = x.via)
      case aint: IntType => vint(name = aint.name, g = JLong.valueOf(x.g), via = x.via)
      case areal: RealType => vreal(name = areal.name, g = JDouble.valueOf(x.g), via = x.via)
      case astr: StrType => vstr(name = astr.name, g = x.g, via = x.via)
      case _: ObjType => x
      case _ => throw LanguageException.typingError(x, asType(y))
    }
    y.trace.map(x => x._2).foldLeft(w)((x, y) => y.exec(x))
  }

  private def lstConverter(x: Lst[Obj], y: Obj): Obj = {
    val w: Obj = Inst.resolveToken(x, y).domain match {
      case _: __ => x
      case astr: StrType => vstr(name = astr.name, g = x.toString, via = x.via)
      case alst: LstType[Obj] if (x.glist.size == alst.glist.size) => lst(g = (alst.gsep, x.glist.zip(alst.glist).map(a => a._1.as(a._2))), via = x.via)
      case alst: LstType[Obj] if x.test(alst) => x
      case _: ObjType => x
      case _ => throw LanguageException.typingError(x, asType(y))
    }
    y.trace.map(x => x._2).foldLeft(w)((x, y) => y.exec(x))
  }

  private def recConverter(x: Rec[Obj, Obj], y: Obj): Obj = {
    val w: Obj = Inst.resolveToken(x, y).domain match {
      case _: __ => x
      case astr: StrType => vstr(name = astr.name, g = x.toString, via = x.via)
      case arec: RecType[Obj, Obj] => val z = rec(name = arec.name, g = (arec.gsep,
        x.gmap.flatMap(a => arec.gmap
          .filter(b => a._1.test(b._1))
          .map(b => (a._1.as(b._1), a._2.as(b._2))))), via = x.via)
        if (z.gmap.size < arec.gmap.count(x => x._2.q._1.g > 0)) throw LanguageException.typingError(x, asType(y)) else z
      case _: ObjType => x
      case _ => throw LanguageException.typingError(x, asType(y))
    }
    y.trace.map(x => x._2).foldLeft(w)((x, y) => y.exec(x))
  }
}
