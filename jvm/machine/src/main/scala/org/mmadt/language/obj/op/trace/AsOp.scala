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
import org.mmadt.language.obj.op.map.WalkOp
import org.mmadt.language.obj.op.trace.AsOp.autoAsType
import org.mmadt.language.obj.op.{OpInstResolver, TraceInstruction}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{LstValue, StrValue, Value}
import org.mmadt.language.obj.{Inst, _}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait AsOp {
  this:Obj =>
  def as[O <: Obj](aobj:O):O = AsOp(aobj).exec(this).asInstanceOf[O]
  def as(atype:Symbol):__ = AsOp(__(atype.name)).exec(this).asInstanceOf[__]
  def ~>(atype:Symbol):__ = autoAsType(this, x => x, atype)
  def ~>[O <: Obj](aobj:O):O = autoAsType(this, x => x, aobj)
}

object AsOp extends Func[Obj, Obj] {
  override val preArgs:Boolean = false
  def apply[O <: Obj](obj:Obj):Inst[O, O] = new VInst[O, O](g = (Tokens.as, List(obj.asInstanceOf[O])), func = this) with TraceInstruction
  override def apply(start:Obj, inst:Inst[Obj, Obj]):Obj = internalConvertAs(start, inst.arg0[Obj]).via(start, inst)

  def autoAsType(source:Obj, target:Obj):target.type = autoAsType(source, target.domain, domain = true).asInstanceOf[target.type]
  def autoAsType[E <: Obj](source:Obj, f:Obj => Obj, target:Obj):E = autoAsType(f(autoAsType(source, target.domain, domain = true)), target.range, domain = false).asInstanceOf[E]
  private def autoAsType(source:Obj, target:Obj, domain:Boolean):Obj = {
    if (domain && __.isToken(target) && source.reload.model.vars(target.name).isDefined && source.isInstanceOf[Type[_]]) return source.from(target.name)
    if (source.name.equals(target.name)) {
      source match {
        case alst:LstValue[Obj] if !Lst.exactTest(alst, target) =>
        //case alst:LstType[Obj] if !Obj.isRecursive(alst) =>
        //case srec:RecValue[Obj, Obj] if !Rec.test(srec, target.asInstanceOf[Rec[Obj, Obj]]) =>
        case _ => return source
      }
    }
    if (!target.alive) return zeroObj
    if (!source.alive || __.isAnon(target) || source.model.vars(target.name).isDefined) return source
    if ((!__.isAnon(source)) && !source.model.typeExists(target)) throw LanguageException.typeNotInModel(source, asType(target), source.model.name)
    source match {
      case _:Strm[Obj] if source.model.dtypes.exists(x => x.name.equals(target.name) && source.q.within(x.domainObj.q)) => target.trace.reconstruct(source, target.name)
      case astrm:Strm[Obj] => astrm(src => AsOp.autoAsType(src, target, domain))
      case _:Value[_] => internalConvertAs(source, target).hardQ(source.q)
      case _:Type[_] => if (domain) target.update(source.model) else target <= source
    }
  }

  private def internalConvertAs(source:Obj, target:Obj):Obj = {
    val asObj:Obj = if (__.isToken(target)) WalkOp.walkSourceToTarget(source, target, targetName = true) else target
    val dObj:Obj = Tokens.tryName(asObj, pickMapping(source, asObj))
    val rObj:Obj =
      if (__.isToken(asObj.range) && asObj.domain != asObj.range && source.model.findCtype(asObj.range.name).isDefined)
        pickMapping(dObj, Tokens.tryName(target, asObj.range))
      else dObj
    if (!rObj.alive) throw LanguageException.typingError(source, asType(asObj))
    Tokens.tryName(asObj, rObj)
  }

  private def baseMapping(source:Obj, target:Obj):Boolean = __.isAnon(source) || __.isAnon(target) || source.named || target.named || source.model.dtypes.exists(t => source.test(t.domainObj) && target.test(t.rangeObj))

  private def pickMapping(start:Obj, asObj:Obj):Obj = {
    if (asObj.isInstanceOf[Value[Obj]]) start ~~> asObj
    else if (start.isInstanceOf[Type[_]]) asObj.update(start.model)
    else if (__.isToken(asObj))
      start.model.search(start, asObj).headOption.map(x => pickMapping(start, x)).get // recursively search for a base type
    else (start match {
      case abool:Bool => boolConverter(abool, asObj)
      case aint:Int => intConverter(aint, asObj)
      case areal:Real => realConverter(areal, asObj)
      case astr:Str => strConverter(astr, asObj)
      case alst:Lst[Obj] => lstConverter(alst, asObj)
      case arec:Rec[Obj, Obj] => recConverter(arec, asObj)
    }).update(start.model)
  }

  private def boolConverter(x:Bool, y:Obj):Obj =
    y.trace.reconstruct(y.domain match {
      case _:__ => x
      case abool:BoolType => bool(name = abool.name, g = x.g, via = x.via)
      case astr:StrType if baseMapping(x, str) => str(name = astr.name, g = x.g.toString, via = x.via)
      case _ => throw LanguageException.typingError(x, asType(y))
    })

  private def intConverter(x:Int, y:Obj):Obj = {
    y.trace.reconstruct(Obj.resolveToken(x, y).domain match {
      case _:__ => x
      case aint:IntType => int(name = aint.name, g = x.g, via = x.via)
      case areal:RealType if baseMapping(x, real) => real(name = areal.name, g = x.g, via = x.via)
      case astr:StrType if baseMapping(x, str) => str(name = astr.name, g = x.g.toString, via = x.via)
      case _ => throw LanguageException.typingError(x, asType(y))
    })
  }

  private def realConverter(x:Real, y:Obj):Obj =
    y.trace.reconstruct(y.domain match {
      case _:__ => x
      case aint:IntType if baseMapping(x, int) => int(name = aint.name, g = x.g.longValue(), via = x.via)
      case areal:RealType => real(name = areal.name, g = x.g, via = x.via)
      case astr:StrType if baseMapping(x, str) => str(name = astr.name, g = x.g.toString, via = x.via)
      case _ => throw LanguageException.typingError(x, asType(y))
    })

  private def strConverter(x:Str, y:Obj):Obj =
    y.trace.reconstruct(Obj.resolveToken(x, y).domain match {
      case _:__ => x
      case abool:BoolType if baseMapping(x, bool) => bool(name = abool.name, g = JBoolean.valueOf(x.g), via = x.via)
      case aint:IntType if baseMapping(x, int) => int(name = aint.name, g = JLong.valueOf(x.g), via = x.via)
      case areal:RealType if baseMapping(x, real) => real(name = areal.name, g = JDouble.valueOf(x.g), via = x.via)
      case astr:StrType => str(name = astr.name, g = x.g, via = x.via)
      case _ => throw LanguageException.typingError(x, asType(y))
    })

  private def lstConverter(x:Lst[Obj], y:Obj):Obj = {
    y.trace.reconstruct(Obj.resolveToken(x, y).domain match {
      case _:__ => x
      case astr:StrType if baseMapping(x, str) => str(name = astr.name, g = x.toString, via = x.via)
      case _:Inst[Obj, Obj] => OpInstResolver.resolve(x.g._2.head.asInstanceOf[StrValue].g, x.g._2.tail)
      case alst:LstType[Obj] if alst.ctype => x.named(alst.name)
      case alst:LstType[Obj] if Lst.shapeTest(x, alst) => lst(name = alst.name, g = (alst.gsep, x.glist.zip(alst.glist).map(a => a._1.compute(a._2))), via = x.via).reload
      case alst:LstType[Obj] if Lst.test(x, alst) => x.named(alst.name)
      case _ => throw LanguageException.typingError(x, asType(y))
    })
  }

  private def recConverter(x:Rec[Obj, Obj], y:Obj):Obj =
    y.trace.reconstruct(Obj.resolveToken(x, y).domain match {
      case _:__ => x
      case astr:StrType if baseMapping(x, str) => str(name = astr.name, g = x.toString, via = x.via)
      case arec:RecType[Obj, Obj] if arec.ctype => x.named(arec.name)
      case arec:RecType[Obj, Obj] => val z = rec(name = arec.name, g = (arec.gsep,
        x.gmap.flatMap(a => arec.gmap
          .filter(b => a._1.test(b._1))
          .map(b => (a._1.as(b._1), a._2.as(b._2))))), via = x.via)
        if (z.gmap.size < arec.gmap.count(x => x._2.q._1.g > 0)) throw LanguageException.typingError(x, asType(y)) else z
      case _ => throw LanguageException.typingError(x, asType(y))
    })
}
