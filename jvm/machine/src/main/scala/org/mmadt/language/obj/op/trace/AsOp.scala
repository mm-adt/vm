/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 *  mm-ADT is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU Affero General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 *  License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 *  You can be released from the requirements of the license by purchasing a
 *  commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language.obj.op.trace

import java.lang.{Boolean => JBoolean, Double => JDouble, Long => JLong}

import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.value.Value
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
  def apply[O <: Obj](obj: Obj): Inst[O, O] = new VInst[O, O](g = (Tokens.as, List(obj)), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    val toObj: Obj = inst.arg0[Obj]

    if (!toObj.isInstanceOf[Poly[Obj]] && toObj.isInstanceOf[Value[Obj]]) {
      Inst.resolveArg(start, toObj)
    } else {
      start match {
        case _: Type[Obj] if !start.isInstanceOf[Poly[Obj]] => toObj
        case abool: Bool => boolConverter(abool, toObj)
        case aint: Int => intConverter(aint, toObj)
        case areal: Real => realConverter(areal, toObj)
        case astr: Str => strConverter(astr, toObj)
        case alst: Lst[Obj] => lstConverter(alst, toObj)
      }
    }.via(start, inst)

    /*val asObj: Obj = (resolve match {
      // case apoly: Poly[Obj] => resolve.named(rename)
      case atype: Type[Obj] if start.isInstanceOf[Value[_]] => atype match {
        case rectype: Rec[Obj, Obj] => rec(rectype.gsep, rectype.gmap.map(x => Inst.resolveArg(start, x._1) -> Inst.resolveArg(start, x._2)).toMap).named(rectype.name)
        case atype: StrType => vstr(name = atype.name, g = start.asInstanceOf[Value[Obj]].g.toString).compute(atype)
        case atype: IntType => vint(name = atype.name, g = Integer.valueOf(start.asInstanceOf[Value[Obj]].g.toString).longValue()).compute(atype)
        case atype: RealType => vreal(name = atype.name, g = JDouble.valueOf(start.asInstanceOf[Value[Obj]].g.toString).doubleValue()).compute(atype)
        case x => x.named(rename)
      }
      case x => x.named(rename)
    }).via(start, inst)
    println(asObj)
    assert(asObj.alive)
    asObj*/
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
    Inst.resolveArg(y.domain match {
      case _: __ => x
      case aint: IntType => vint(name = aint.name, g = x.g)
      case areal: RealType => vreal(name = areal.name, g = x.g)
      case astr: StrType => vstr(name = astr.name, g = x.g.toString)
      case _ => throw LanguageException.typingError(x, asType(y))
    }, y)
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
      case _ => throw LanguageException.typingError(x, asType(y))
    }, y)
  }

  private def lstConverter(x: Lst[Obj], y: Obj): Obj = {
    // if (x.isType) return y
    //Inst.resolveArg(
    y.domain match {
      case _: __ => x
      case astr: StrType => vstr(name = astr.name, g = x.toString)
      case alst: Lst[Obj] => lst[Obj](sep = alst.gsep, x.glist.zip(alst.glist).map(a => a._1.as(a._2)):_*)
      case _ => throw LanguageException.typingError(x, asType(y))
    }//, y)
  }
}
