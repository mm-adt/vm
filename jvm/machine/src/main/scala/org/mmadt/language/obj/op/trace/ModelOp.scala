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

import org.mmadt
import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.Rec._
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.op.sideeffect.LoadOp
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{StrValue, Value}
import org.mmadt.storage
import org.mmadt.storage.StorageFactory.{lst, rec, str}
import org.mmadt.storage.model
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ModelOp {
  this:Obj =>
  def update(model:Model):this.type = ModelOp.updateModel(model, this)
  def model(model:Model):this.type = ModelOp(model).exec(this)
  def model(file:StrValue):this.type = ModelOp(file).exec(this)
  def model(token:__):this.type = ModelOp(mmadt.storage.model(token.name)).exec(this)
}

object ModelOp extends Func[Obj, Obj] {
  override val preArgs:Boolean = false
  lazy val MM:Model = model("mm")
  type Model = Rec[StrValue, ModelMap]
  type ModelMap = Rec[Obj, Lst[Obj]]
  val TYPE:StrValue = str("type")
  val VAR:StrValue = str("var")
  val NOROOT:Pairs[StrValue, ModelOp.ModelMap] = List.empty
  val NOMAP:Pairs[Obj, Lst[Obj]] = List.empty
  val NOREC:ModelMap = rec[Obj, Lst[Obj]]
  val EMPTY:Model = rec[StrValue, ModelOp.ModelMap]

  def apply[O <: Obj](file:StrValue):Inst[O, O] = this.apply(storage.model(LoadOp.loadObj[Model](file.g)))
  def apply[O <: Obj](model:Model):Inst[O, O] = new VInst[O, O](g = (Tokens.model, List(storage.model(model)).asInstanceOf[List[O]]), func = this) with TraceInstruction
  override def apply(start:Obj, inst:Inst[Obj, Obj]):Obj = start match {
    case _:Value[Obj] => start.update(storage.model(inst.arg0[Model]))
    case _:Type[Obj] => start.via(start.update(storage.model(inst.arg0[Model])), inst)
  }
  def updateModel(amodel:Model, aobj:Obj):aobj.type = {
    aobj match {
      case astrm:Strm[aobj.type] => astrm(x => updateModel(amodel, x))
      case _ =>
        if (amodel.isEmpty) aobj
        else if (aobj.root) aobj.clone(via = (aobj.model.merging(amodel), null))
        else aobj.rangeObj.clone(via = (aobj.trace.dropRight(1).foldLeft(aobj.domainObj.clone(via = (aobj.model.merging(amodel), null)).asInstanceOf[Obj])((a, b) => b._1.via(a, b._2)), aobj.via._2))
    }

  }
  def isMetaModel(inst:Inst[_, _]):Boolean = inst.op.equals(Tokens.model) || inst.op.startsWith("rule:") || inst.op.equals(Tokens.define)

  @inline implicit def modelToRichModel(ground:Model):RichModel = new RichModel(ground)
  class RichModel(val model:Model) {
    private final def findType[A <: Obj](model:Model, source:Obj, targetName:String):List[A] =
      (if (model.name.equals(targetName)) List(model).asInstanceOf[List[A]]
      else model.gmap.fetchOrElse(TYPE, NOREC).gmap
        .filter(x => x._1.name == targetName)
        .flatMap(x => x._2.asInstanceOf[Lst[A]].g._2))
        .map(x => if (__.isToken(x.domainObj) && !typeGrounded(model, x)) __.asInstanceOf[A] else x) // is the type is not grounded, anything matches
        .filter(x => if (__.isToken(x.domainObj))
          model.search(source, x.domainObj, baseName = false).exists(y => source.update(model).test(y)) else
          source.update(model).test(x.domainObj.hardQ(source.q)))

    final def typeExists(aobj:Obj):Boolean = model.definitions.isEmpty ||
      model.vars(str(aobj.name)).isDefined ||
      __.isAnon(aobj) ||
      model.gmap.fetchOrElse(ModelOp.TYPE, NOREC).gmap.exists(x => x._1.name == aobj.name)

    private final def typeGrounded(model:Model, aobj:Obj):Boolean =
      model.gmap.fetchOrElse(ModelOp.TYPE, NOREC).gmap
        .find(x => x._1.name == aobj.name).map(x => x._2.glist)
        .exists(x => x.exists(y => !baseName(y.domainObj).equals(Tokens.anon) || !Type.isIdentity(y)))

    final def search[A <: Obj](source:Obj = __, target:A, baseName:Boolean = true):List[A] = {
      model.vars[A](target.name)
        .map(x => if (x.isInstanceOf[Type[_]]) target.range.asInstanceOf[A] else x)
        .map(x => List(x))
        .getOrElse[List[A]](
          findType[A](model, source, target.name)
            .map(y => if (baseName) toBaseName(y) else y)
            .map(x => x.update(model)))
    }

    final def rewrites:List[Obj] = model.gmap.fetchOrElse(TYPE, NOREC).gmap.values
      .flatMap(y => y.g._2)
      .filter(y => y.domain.name.equals(Tokens.pow_op) &&
        y.isInstanceOf[Lst[Obj]]
          && y.domain.isInstanceOf[Lst[Obj]]
          && y.domain.asInstanceOf[Lst[Obj]].g._2.nonEmpty)

    final def definitions:List[Obj] = {
      val map = Option(model.g._2).getOrElse(NOROOT)
      val typesMap = Option(map.fetchOrElse(ModelOp.TYPE, NOREC).g._2).getOrElse(NOMAP)
      typesMap.flatMap(x => x._2.glist)
    }

    final def vars[A <: Obj](key:StrValue):Option[A] = {
      val map = Option(model.g._2).getOrElse(NOROOT)
      val typesMap = Option(map.fetchOrElse(ModelOp.VAR, NOREC).g._2).getOrElse(NOMAP)
      typesMap.fetchOption(key).map(x => x.glist.head).asInstanceOf[Option[A]]
    }

    final def varing(key:StrValue, value:Obj):Model = {
      if (model.vars(key).isDefined && value.isInstanceOf[Type[_]]) return model
      val map = Option(model.g._2).getOrElse(NOROOT)
      val typesMap = Option(map.fetchOrElse(ModelOp.VAR, NOREC).g._2).getOrElse(NOMAP)
      rec(model.name, g = (Tokens.`,`, map.replace(ModelOp.VAR -> rec(g = (Tokens.`,`, typesMap.replace(key -> lst(g = (Tokens.`,`, List(value.rangeObj)))))))))
    }

    final def defining(definition:Obj):Model = {
      val map = Option(model.g._2).getOrElse(NOROOT)
      val typesMap = Option(map.fetchOrElse(ModelOp.TYPE, NOREC).g._2).getOrElse(NOMAP)
      val typeList = Option(typesMap.fetchOrElse(definition.range, lst).g._2).getOrElse(Nil)
      if (typeList.contains(definition)) model
      else rec(model.name, g = (Tokens.`,`, map.replace(ModelOp.TYPE -> rec(g = (Tokens.`,`, typesMap.replace(definition.range -> lst(g = (Tokens.`,`, typeList :+ definition))))))))
    }

    final def merging(other:Model):Model = {
      if (other.isEmpty) return model
      else if (model.isEmpty) return other
      var x:Model = other.g._2.
        fetchOrElse(ModelOp.TYPE, rec(g = (Tokens.`,`, NOMAP))).g._2.
        flatMap(x => x._2.g._2).
        foldLeft(model)((a, b) => a.defining(b))
      x = other.g._2.fetchOrElse(ModelOp.VAR, rec(g = (Tokens.`,`, NOMAP))).gmap.foldLeft(x)((a, b) => a.varing(b._1.asInstanceOf[StrValue], b._2.asInstanceOf[Lst[Obj]].glist.head))
      rec(name = model.name, g = (Tokens.`,`, x.g._2))
    }
  }
}