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
    val asObj: Obj = inst.arg0[Obj]
    val toObj: Obj = if (asObj.isInstanceOf[Value[Obj]]) Inst.resolveArg(start, asObj)
    else {
      start match {
        case _: Type[Obj] if !start.isInstanceOf[Poly[Obj]] => asObj
        case abool: Bool => boolConverter(abool, asObj)
        case aint: Int => intConverter(aint, asObj)
        case areal: Real => realConverter(areal, asObj)
        case astr: Str => strConverter(astr, asObj)
        case alst: Lst[Obj] => lstConverter(alst, asObj)
        case arec: Rec[Obj, Obj] => recConverter(arec, asObj)
      }
    }.via(start, inst)
    if (Tokens.named(asObj.name)) toObj.named(asObj.name) else toObj
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
      case alst: LstType[Obj] => lst(g = (alst.gsep, x.glist.zip(alst.glist).map(a => a._1.as(a._2))))
      case _ => throw LanguageException.typingError(x, asType(y))
    } //, y)
  }

  private def recConverter(x: Rec[Obj, Obj], y: Obj): Obj = {
    // if (x.isType) return y
    //Inst.resolveArg(
    y.domain match {
      case _: __ => x
      case astr: StrType => vstr(name = astr.name, g = x.toString)
      case arec: RecType[Obj, Obj] => rec(g = (arec.gsep,
        x.gmap
          .flatMap(a => arec.gmap
            .filter(q => a._1.test(q._1))
            .map(q => (a._1.as(q._1), a._2.as(q._2)))).toMap[Obj, Obj]))
      case _ => throw LanguageException.typingError(x, asType(y))
    } //, y)
  }
}
