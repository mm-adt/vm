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
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.op.map.WalkOp
import org.mmadt.language.obj.op.trace.ModelOp.NONE
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{LstValue, Value}
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
  def `=>`[O <: Obj](aobj:O):O = as(aobj)
  def `=>`(atype:Symbol):__ = as(atype)
}
object AsOp extends Func[Obj, Obj] {
  override val preArgs:Boolean = false
  override val preStrm:Boolean = true
  def apply[O <: Obj](obj:Obj):Inst[O, O] = new VInst[O, O](g = (Tokens.as, List(obj.asInstanceOf[O])), func = this) with TraceInstruction
  override def apply(start:Obj, inst:Inst[Obj, Obj]):Obj = {
    if (__.isAnon(start)) return start.via(start, inst)
    inst.arg0[Obj] match {
      case avalue:Value[Obj] => avalue.hardQ(q => start.q.mult(q))
      case atype:Type[Obj] if __.isAnonRootAlive(atype) => start
      case atype:Type[Obj] =>
        val rtype = atype.inflate[Obj](start.model)
        start.inflate[Obj]() match {
          case _:Type[_] if start.rangeObj == rtype => start
          case _:Type[Obj] if start.model == NONE => rtype.rangeObj.via(start, inst)
          case _:Type[_] if !Tokens.named(rtype.name) && toBaseName(start.rangeObj) == rtype => Converters.objConverter(start, rtype).headOption.getOrElse(zeroObj) // atype.rangeObj <= start
          case _:Type[_] => start.coerce(rtype)
          case _:Value[_] => start.named(rtype.domainObj.name).compute(atype, withAs = false).named(rtype.rangeObj.name)
        }
    }
  }
  def searchable(aobj:Obj):Boolean = __.isToken(aobj) || (aobj.isInstanceOf[LstType[Obj]] && !aobj.asInstanceOf[Lst[Obj]].ctype && !aobj.named)
  def autoAsType(source:Obj, target:Obj):target.type = autoAsType(source, target.domain, domain = true).asInstanceOf[target.type]
  def autoAsType[E <: Obj](source:Obj, f:Obj => Obj, target:Obj):E =
    if (target.root) f(autoAsType(source, target.domain)).asInstanceOf[E]
    else autoAsType(f(autoAsType(source, target.domain, domain = true)), target.range, domain = false).asInstanceOf[E]

  /////// PRIVATE METHODS
  private def autoAsType(source:Obj, target:Obj, domain:Boolean):Obj = {
    if (domain && __.isToken(target) && source.isInstanceOf[Type[_]] && source.reload.model.vars(target.name).isDefined) return source.from(__(target.name))
    if (source.name.equals(target.name)) source match {
      case alst:LstValue[Obj] if !Lst.exactTest(alst, target) =>
      case _ => return source
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

  private def pickMapping(source:Obj, target:Obj):Obj = {
    if (target.isInstanceOf[Value[Obj]]) source ->> target
    else source match {
      case _:Value[Obj] => Converters.objConverter(source, target.domainObj).map(x => target.trace.reconstruct[Obj](x)).headOption.getOrElse(source)
      case _ => Converters.objConverter(source, target).headOption.getOrElse(source)
    }
  }
}
