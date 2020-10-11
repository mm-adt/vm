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
import org.mmadt.language.obj.value.{LstValue, RecValue, StrValue, Value}
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
  def autoAsType[E <: Obj](source:Obj, f:Obj => Obj, target:Obj):E =
    if (target.root) f(autoAsType(source, target.domain)).asInstanceOf[E]
    else autoAsType(f(autoAsType(source, target.domain, domain = true)), target.range, domain = false).asInstanceOf[E]

  private def autoAsType(source:Obj, target:Obj, domain:Boolean):Obj = {
    if (domain && __.isToken(target) && source.isInstanceOf[Type[_]] && source.reload.model.vars(target.name).isDefined) return source.from(__(target.name))
    if (source.name.equals(target.name)) {
      source match {
        case alst:LstValue[Obj] if !Lst.exactTest(alst, target) =>
        // case arec:RecValue[Obj, Obj] if !Rec.test(arec, target.asInstanceOf[Rec[Obj, Obj]]) =>
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
    val asObj:Obj = if (searchable(target)) WalkOp.walkSourceToTarget(source, target, targetName = true) else target
    val dObj:Obj = Tokens.tryName(asObj, pickMapping(source, asObj))
    val rObj:Obj =
      if (searchable(asObj.range) && asObj.domain != asObj.range && source.model.findCtype(asObj.range.name).isDefined)
        pickMapping(dObj, Tokens.tryName(target, asObj.range))
      else dObj
    if (!rObj.alive) throw LanguageException.typingError(source, asType(asObj))
    Tokens.tryName(asObj, rObj)
  }

  /*private def baseMapping(source:Obj, target:Obj):Boolean = __.isAnon(source) || __.isAnon(target) || source.named || target.named ||
    source.model.dtypes.exists(t => source.name.equals(t.domainObj.name) && target.name.equals(t.rangeObj.name))*/
  def searchable(aobj:Obj):Boolean = __.isToken(aobj) || (aobj.isInstanceOf[LstType[Obj]] && !aobj.asInstanceOf[Lst[Obj]].ctype && !aobj.named)

  private def pickMapping(source:Obj, target:Obj):Obj = {
    if (target.isInstanceOf[Value[Obj]]) source ~~> target
    else if (source.isInstanceOf[Type[_]]) target.update(source.model)
    else if (searchable(target)) objConverter(source, source.model.search(source, target).headOption.getOrElse(target))
    else objConverter(source, target)
  }

  /////// CONVERTERS

  private def objConverter(source:Obj, target:Obj):Obj =
    (source match {
      case abool:Bool => boolConverter(abool, target)
      case aint:Int => intConverter(aint, target)
      case areal:Real => realConverter(areal, target)
      case astr:Str => strConverter(astr, target)
      case alst:Lst[Obj] => lstConverter(alst, target)
      case arec:Rec[Obj, Obj] => recConverter(arec, target)
    }).named(target.name).update(source.model)

  private def boolConverter(source:Bool, target:Obj):Obj =
    target.trace.reconstruct(target.domain match {
      case _:__ => source
      case abool:BoolType => bool(name = abool.name, g = source.g, via = source.via)
      case astr:StrType => str(name = astr.name, g = source.g.toString, via = source.via)
      case _ => throw LanguageException.typingError(source, asType(target))
    })

  private def intConverter(source:Int, target:Obj):Obj = {
    target.trace.reconstruct(Obj.resolveToken(source, target).domain match {
      case _:__ => source
      case aint:IntType => int(name = aint.name, g = source.g, via = source.via)
      case areal:RealType => real(name = areal.name, g = source.g, via = source.via)
      case astr:StrType => str(name = astr.name, g = source.g.toString, via = source.via)
      case _ => throw LanguageException.typingError(source, asType(target))
    })
  }

  private def realConverter(source:Real, target:Obj):Obj =
    target.trace.reconstruct(target.domain match {
      case _:__ => source
      case aint:IntType => int(name = aint.name, g = source.g.longValue(), via = source.via)
      case areal:RealType => real(name = areal.name, g = source.g, via = source.via)
      case astr:StrType => str(name = astr.name, g = source.g.toString, via = source.via)
      case _ => throw LanguageException.typingError(source, asType(target))
    })

  private def strConverter(source:Str, target:Obj):Obj =
    target.trace.reconstruct(Obj.resolveToken(source, target).domain match {
      case _:__ => source
      case abool:BoolType => bool(name = abool.name, g = JBoolean.valueOf(source.g), via = source.via)
      case aint:IntType => int(name = aint.name, g = JLong.valueOf(source.g), via = source.via)
      case areal:RealType => real(name = areal.name, g = JDouble.valueOf(source.g), via = source.via)
      case astr:StrType => str(name = astr.name, g = source.g, via = source.via)
      case _ => throw LanguageException.typingError(source, asType(target))
    })

  private def lstConverter(source:Lst[Obj], target:Obj):Obj = {
    target.trace.reconstruct(Obj.resolveToken(source, target).domain match {
      case _:__ => source
      case astr:StrType => str(name = astr.name, g = source.toString, via = source.via)
      case _:Inst[Obj, Obj] => OpInstResolver.resolve(source.g._2.head.asInstanceOf[StrValue].g, source.g._2.tail)
      case alst:LstType[Obj] if alst.ctype => source.named(alst.name)
      case alst:LstType[Obj] if Lst.shapeTest(source, alst) => lst(name = alst.name, g = (alst.gsep, source.glist.zip(alst.glist).map(a => a._1.compute(a._2))), via = source.via).reload
      case alst:LstType[Obj] if Lst.test(source, alst) => source.named(alst.name)
      case _ => throw LanguageException.typingError(source, asType(target))
    })
  }

  private def recConverter(source:Rec[Obj, Obj], target:Obj):Obj =
    target.trace.reconstruct(Obj.resolveToken(source, target).domain match {
      case _:__ => source
      case astr:StrType => str(name = astr.name, g = source.toString, via = source.via)
      case arec:RecType[Obj, Obj] if arec.ctype => source.named(arec.name)
      case arec:RecType[Obj, Obj] => val z = rec(name = arec.name, g = (arec.gsep,
        source.gmap.flatMap(a => arec.gmap
          .filter(b => a._1.test(b._1))
          .map(b => (a._1.as(b._1), a._2.as(b._2))))), via = source.via)
        if (z.gmap.size < arec.gmap.count(x => x._2.q._1.g > 0)) throw LanguageException.typingError(source, asType(target)) else z
      case _ => throw LanguageException.typingError(source, asType(target))
    })
}
