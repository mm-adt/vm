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
  def ~[O <: Obj](obj: O): O = this.as(obj)
}

object AsOp extends Func[Obj, Obj] {
  def apply[O <: Obj](obj: Obj): Inst[O, O] = new VInst[O, O](g = (Tokens.as, List(obj.asInstanceOf[O])), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    if (start.isInstanceOf[Strm[_]]) return start.via(start, inst)
    val asObj: Obj = if (start.isInstanceOf[Type[_]]) inst.arg0[Obj] else Inst.resolveToken(start, inst.arg0[Obj])
    val dObj: Obj = pickMapping(start, asObj)
    val rObj: Obj = if (asObj.domain != asObj.range) pickMapping(dObj, asObj.range) else dObj
    val result = (if (Tokens.named(inst.arg0[Obj].name)) rObj.named(inst.arg0[Obj].name) else rObj).via(start, inst)
    if (!result.alive) throw LanguageException.typingError(start, asType(asObj))
    result
  }
  private def pickMapping(start: Obj, asObj: Obj): Obj = {
    if (asObj.isInstanceOf[Value[Obj]]) Inst.resolveArg(start, asObj)
    else {
      val defined = Obj.fetchOption[Obj](start, start, asObj.name)
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
      case abool: BoolType => vbool(name = abool.name, g = x.g)
      case astr: StrType => vstr(name = astr.name, g = x.g.toString)
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
      case aint: IntType => vint(name = aint.name, g = x.g.longValue())
      case areal: RealType => vreal(name = areal.name, g = x.g)
      case astr: StrType => vstr(name = astr.name, g = x.g.toString)
      case _ => throw LanguageException.typingError(x, asType(y))
    }, y)
  }

  private def strConverter(x: Str, y: Obj): Obj = {
    Inst.resolveArg(y.domain match {
      case _: __ => x
      case abool: BoolType => vbool(name = abool.name, g = JBoolean.valueOf(x.g))
      case aint: IntType => vint(name = aint.name, g = JLong.valueOf(x.g))
      case areal: RealType => vreal(name = areal.name, g = JDouble.valueOf(x.g))
      case astr: StrType => vstr(name = astr.name, g = x.g)
      case _: ObjType => x
      case _ => throw LanguageException.typingError(x, asType(y))
    }, y)
  }

  private def lstConverter(x: Lst[Obj], y: Obj): Obj = {
    val w: Obj = Inst.resolveToken(x, y).domain match {
      case _: __ => x
      case astr: StrType => vstr(name = astr.name, g = x.toString)
      case alst: LstType[Obj] => lst(g = (alst.gsep, x.glist.zip(alst.glist).map(a => a._1.as(a._2))), via = x.via)
      case _: ObjType => x
      case _ => throw LanguageException.typingError(x, asType(y))
    }
    y.trace.map(x => x._2).foldLeft(w)((x, y) => y.exec(x))
  }

  private def recConverter(x: Rec[Obj, Obj], y: Obj): Obj = {
    val w: Obj = Inst.resolveToken(x, y).domain match {
      case _: __ => x
      case astr: StrType => vstr(name = astr.name, g = x.toString)
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
