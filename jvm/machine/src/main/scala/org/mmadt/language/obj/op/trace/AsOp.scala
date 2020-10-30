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

import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`._
import org.mmadt.language.obj.op.branch.CombineOp
import org.mmadt.language.obj.op.map.WalkOp
import org.mmadt.language.obj.op.{OpInstResolver, TraceInstruction}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{LstValue, StrValue, Value}
import org.mmadt.language.obj.{Inst, _}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.graph.Converters
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait AsOp {
  this:Obj =>
  def as[O <: Obj](aobj:O):O = AsOp(aobj).exec(this).asInstanceOf[O]
  def as(atype:Symbol):__ = AsOp(__(atype.name)).exec(this).asInstanceOf[__]
  //def ~>(atype:Symbol):__ = autoAsType(this, x => x, atype)
  //def ~>[O <: Obj](aobj:O):O = autoAsType(this, x => x, aobj)
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
        case _ => return source
      }

    }
    if (!target.alive) return zeroObj
    if (!source.alive || __.isAnon(target) || source.model.vars(target.name).isDefined) return source
    LanguageException.testTypeInModel(source, asType(target))
    source match {
      case astrm:Strm[Obj] => astrm(src => AsOp.autoAsType(src, target, domain)).named(target.name)
      case _:Value[_] => internalConvertAs(source, target).hardQ(source.q)
      case _:Type[_] => if (domain) target.update(source.model) else target <= source
    }
  }

  private def internalConvertAs(source:Obj, target:Obj):Obj = {
    val asObj:Obj = WalkOp.walkSourceToTarget(source, target, targetName = true)
    val dObj:Obj = pickMapping(source, asObj)
    val rObj:Obj = if (asObj.domain != asObj.range && source.model.findCtype(asObj.range.name).isDefined) // source.model.og.V().has(CTYPE,NAME,asObj.range.name).hasNext
      pickMapping(dObj, asObj.range) else dObj
    if (!rObj.alive) throw LanguageException.typingError(source, asType(asObj))
    rObj.named(asObj.name)
  }

  def searchable(aobj:Obj):Boolean = __.isToken(aobj) || (aobj.isInstanceOf[LstType[Obj]] && !aobj.asInstanceOf[Lst[Obj]].ctype && !aobj.named)

  private def pickMapping(source:Obj, target:Obj):Obj = {
    if (target.isInstanceOf[Value[Obj]]) source ~~> target
    else if (source.isInstanceOf[Type[_]]) target
    else {
      val rtarget = Obj.resolveToken(source, target)
      target.trace.reconstruct[Obj](source match {
        case alst:LstValue[Obj] => lstConverter(alst, rtarget)
        case _:Value[Obj] => Converters.objConverter(source, rtarget.domainObj).headOption.getOrElse(source)
        case _ => Converters.objConverter(source, rtarget).headOption.getOrElse(source)
      })
    }
  }

  /////// CONVERTERS


  private def lstConverter(source:Lst[Obj], target:Obj):Obj = target.domain match {
    case _:__ => source
    case astr:StrType => str(name = astr.name, g = source.toString, via = source.via)
    case _:Inst[Obj, Obj] => OpInstResolver.resolve(source.g._2.head.asInstanceOf[StrValue].g, source.g._2.tail)
    case alst:LstType[Obj] if Lst.shapeTest(source, alst) => //source.coercions2(alst).headOption.getOrElse(source.named(target.name)).reload
      val blst = lst(name = alst.name, g = (alst.gsep, source.glist.zip(alst.glist).map(a => a._1.coerce(a._2))), via = source.via)
      if (Lst.exactTest(blst, alst.domainObj)) CombineOp.combineAlgorithm(blst, alst, withAs = false).reload else blst.reload
    case alst:LstType[Obj] if Lst.test(source, alst) => source.named(alst.name)
    case _ => throw LanguageException.typingError(source, asType(target))
  }
}
