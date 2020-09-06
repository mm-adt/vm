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
import org.mmadt.language.obj.op.map.WalkOp
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
  this:Obj =>
  def as[O <: Obj](obj:O):O = AsOp(obj).exec(this).asInstanceOf[O]
  def as(obj:Symbol):__ = AsOp(__(obj.name)).exec(this).asInstanceOf[__]
}

object AsOp extends Func[Obj, Obj] {
  override val preArgs:Boolean = false
  def apply[O <: Obj](obj:Obj):Inst[O, O] = new VInst[O, O](g = (Tokens.as, List(obj.asInstanceOf[O])), func = this) with TraceInstruction
  override def apply(start:Obj, inst:Inst[Obj, Obj]):Obj = internalConvertAs(start, inst.arg0[Obj]).via(start, inst)

  def autoAsType[E <: Obj](source:Obj, f:Obj => Obj, target:Obj):E = autoAsType(f(autoAsType(source, target.domain, target, domain = true)), target.range, target, domain = false).asInstanceOf[E]
  private def autoAsType(source:Obj, target:Obj, rangeType:Obj, domain:Boolean):Obj = {
    if (!target.alive) return zeroObj
    if (!source.alive) return source
    if (source.isInstanceOf[Strm[Obj]]) return source.toStrm(x => AsOp.autoAsType(x, target, rangeType, domain))
    if (source.name.equals(target.name) || __.isAnon(target) || source.model.vars(target.name).isDefined) return source
    if ((!__.isAnon(source) && source.root) && !source.model.typeExists(target)) throw LanguageException.typeNotInModel(source, asType(target), source.model.name)
    source match {
      case _:Value[_] => internalConvertAs(source, target).hardQ(source.q)
      case _:Type[_] if domain => target.update(source.model)
      case _:Type[_] => target <= source
    }
  }

  private def internalConvertAs(source:Obj, target:Obj):Obj = {
    val asObj:Obj = if (__.isToken(target)) WalkOp.resolveTokenPath(source, target) else target
    val dObj:Obj = pickMapping(source, asObj).update(source.model)
    val rObj:Obj = if (asObj.domain != asObj.range) pickMapping(dObj, Tokens.tryName(target, asObj.range)) else dObj
    val result = Tokens.tryName(target, rObj)
    if (!result.alive) throw LanguageException.typingError(source, asType(asObj.named(target.name)))
    result.update(source.model)
  }

  private def pickMapping(start:Obj, asObj:Obj):Obj = {
    if (asObj.isInstanceOf[Value[Obj]]) start ~~> asObj
    else {
      val defined = if (__.isToken(asObj)) start.model.search(start, asObj).headOption else None
      (start match {
        case _:Type[Obj] => asObj
        case _ if defined.isDefined => pickMapping(start, defined.get)
        case abool:Bool => boolConverter(abool, asObj)
        case aint:Int => intConverter(aint, asObj)
        case areal:Real => realConverter(areal, asObj)
        case astr:Str => strConverter(astr, asObj)
        case alst:Lst[Obj] => lstConverter(alst, asObj)
        case arec:Rec[Obj, Obj] => recConverter(arec, asObj)
      }).update(start.model)
    }
  }
  private def boolConverter(x:Bool, y:Obj):Obj =
    y.trace.reconstruct(y.domain match {
      case _:__ => x
      case abool:BoolType => bool(name = abool.name, g = x.g, via = x.via)
      case astr:StrType => str(name = astr.name, g = x.g.toString, via = x.via)
      case _ => throw LanguageException.typingError(x, asType(y))
    })

  private def intConverter(x:Int, y:Obj):Obj =
    y.trace.reconstruct(Obj.resolveTokenOption(x, y).getOrElse(y).domain match {
      case _:__ => x
      case aint:IntType => int(name = aint.name, g = x.g, via = x.via)
      case areal:RealType => real(name = areal.name, g = x.g, via = x.via)
      case astr:StrType => str(name = astr.name, g = x.g.toString, via = x.via)
      case _ => throw LanguageException.typingError(x, asType(y))
    })

  private def realConverter(x:Real, y:Obj):Obj =
    y.trace.reconstruct(y.domain match {
      case _:__ => x
      case aint:IntType => int(name = aint.name, g = x.g.longValue(), via = x.via)
      case areal:RealType => real(name = areal.name, g = x.g, via = x.via)
      case astr:StrType => str(name = astr.name, g = x.g.toString, via = x.via)
      case _ => throw LanguageException.typingError(x, asType(y))
    })

  private def strConverter(x:Str, y:Obj):Obj =
    y.trace.reconstruct(Obj.resolveTokenOption(x, y).getOrElse(y).domain match {
      case _:__ => x
      case abool:BoolType => bool(name = abool.name, g = JBoolean.valueOf(x.g), via = x.via)
      case aint:IntType => int(name = aint.name, g = JLong.valueOf(x.g), via = x.via)
      case areal:RealType => real(name = areal.name, g = JDouble.valueOf(x.g), via = x.via)
      case astr:StrType => str(name = astr.name, g = x.g, via = x.via)
      case _ => throw LanguageException.typingError(x, asType(y))
    })

  private def lstConverter(x:Lst[Obj], y:Obj):Obj =
    y.trace.reconstruct(Obj.resolveTokenOption(x, y).getOrElse(y).domain match {
      case _:__ => x
      case astr:StrType => str(name = astr.name, g = x.toString, via = x.via)
      case alst:LstType[Obj] if alst.ctype => x.named(alst.name)
      case alst:LstType[Obj] if x.glist.size == alst.glist.size => lst(g = (alst.gsep, x.glist.zip(alst.glist).map(a => a._1.as(a._2))), via = x.via)
      case _ => throw LanguageException.typingError(x, asType(y))
    })

  private def recConverter(x:Rec[Obj, Obj], y:Obj):Obj =
    y.trace.reconstruct(Obj.resolveTokenOption(x, y).getOrElse(y).domain match {
      case _:__ => x
      case astr:StrType => str(name = astr.name, g = x.toString, via = x.via)
      case arec:RecType[Obj, Obj] if arec.ctype => x.named(arec.name)
      case arec:RecType[Obj, Obj] => val z = rec(name = arec.name, g = (arec.gsep,
        x.gmap.flatMap(a => arec.gmap
          .filter(b => a._1.test(b._1))
          .map(b => (a._1.as(b._1), a._2.as(b._2))))), via = x.via)
        if (z.gmap.size < arec.gmap.count(x => x._2.q._1.g > 0)) throw LanguageException.typingError(x, asType(y)) else z
      case _ => throw LanguageException.typingError(x, asType(y))
    })
}
