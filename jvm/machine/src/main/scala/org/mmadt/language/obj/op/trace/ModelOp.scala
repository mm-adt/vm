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
import org.mmadt.language.obj.value.{LstValue, StrValue, Value}
import org.mmadt.storage
import org.mmadt.storage.StorageFactory.{lst, rec, str}
import org.mmadt.storage.model
import org.mmadt.storage.obj.graph.ObjGraph
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ModelOp {
  this:Obj =>
  def update(model:Model):this.type = ModelOp.mergeModel(model, this)
  def model(model:Model):this.type = ModelOp(model).exec(this)
  def model(file:StrValue):this.type = ModelOp(file).exec(this)
  def model(token:__):this.type = ModelOp(mmadt.storage.model(token.name)).exec(this)
}

object ModelOp extends Func[Obj, Obj] {
  override val preArgs:Boolean = false
  lazy val NONE:Model = model('none)
  lazy val MM:Model = model('mm)
  lazy val MMX:Model = model('mmx)
  type Model = Rec[StrValue, ModelMap]
  type ModelMap = Rec[Obj, Lst[Obj]]
  val TYPE:StrValue = str("type")
  val VAR:StrValue = str("var")
  val MODEL_EXTENSION:String = "#"
  val NOROOT:Pairs[StrValue, ModelOp.ModelMap] = List.empty
  val NOMAP:Pairs[Obj, Lst[Obj]] = List.empty
  val NOREC:ModelMap = rec[Obj, Lst[Obj]]

  def apply[O <: Obj](file:StrValue):Inst[O, O] = this.apply(storage.model(LoadOp.loadObj[Model](file.g)))
  def apply[O <: Obj](model:Model):Inst[O, O] = new VInst[O, O](g = (Tokens.model, List(storage.model(model)).asInstanceOf[List[O]]), func = this) with TraceInstruction
  override def apply(start:Obj, inst:Inst[Obj, Obj]):Obj = start match {
    case _:Value[Obj] => start.update(storage.model(inst.arg0[Model]))
    case _:Type[Obj] => start.via(start.update(storage.model(inst.arg0[Model])), inst)
  }
  def mergeModel(amodel:Model, aobj:Obj):aobj.type = {
    aobj match {
      case astrm:Strm[aobj.type] => astrm(x => mergeModel(amodel, x))
      case _ =>
        if (amodel.name == aobj.model.name) aobj
        else if (amodel.isEmpty) aobj
        else if (aobj.root) aobj.clone(via = (aobj.model.merging(amodel), null))
        else aobj.rangeObj.clone(via = (aobj.trace.dropRight(1).foldLeft(aobj.domainObj.clone(via = (aobj.model.merging(amodel), null)).asInstanceOf[Obj])((a, b) => b._1.via(a, b._2)), aobj.via._2))
    }
  }
  private def nameModel(amodel:Model):Model = amodel.named((
    if (amodel.name.indexOf(MODEL_EXTENSION) == -1) amodel.name + MODEL_EXTENSION
    else amodel.name.substring(0, amodel.name.indexOf(MODEL_EXTENSION) + 1)) +
    Math.abs(amodel.toString.hashCode))

  def isMetaModel(inst:Inst[_, _]):Boolean = inst.op.equals(Tokens.model) || inst.op.startsWith("rule:") || inst.op.equals(Tokens.define)

  @inline implicit def modelToRichModel(ground:Model):RichModel = new RichModel(ground)
  class RichModel(val model:Model) {
    lazy val graph:ObjGraph.ObjGraph = ObjGraph.create(model)
    lazy val coreName:String = model.name.split(MODEL_EXTENSION)(0)

    private final def findType[A <: Obj](model:Model, source:Obj, targetName:String):List[A] =
      (if (model.name.equals(targetName)) List(model).asInstanceOf[List[A]]
      else model.gmap.fetchOrElse(TYPE, NOREC).gmap
        .filter(x => x._1.name == targetName)
        .flatMap(x => x._2.asInstanceOf[Lst[A]].g._2))
        .filter(x => x.domainObj.name != Tokens.lift_op) // little optimization hack that will go away as model becomes more cleverly organized
        .map(x => if (__.isToken(x.domainObj) && !typeGrounded(model, x)) __.asInstanceOf[A] else x) // is the type is not grounded in an mm base type, then anything matches
        .filter(x => if (__.isToken(x.domainObj))
          model.search(source, x.domainObj, baseName = false).exists(y => source.test(y))
        else if (source.isInstanceOf[LstValue[Obj]] && AsOp.searchable(x.domainObj)) Lst.fastCheck(source, x.domainObj)
        else source.test(x.domainObj.hardQ(source.q)))

    private final def typeGrounded(model:Model, aobj:Obj):Boolean =
      model.gmap.fetchOrElse(ModelOp.TYPE, NOREC).gmap
        .find(x => x._1.name == aobj.name).map(x => x._2.glist)
        .exists(x => x.exists(y => !baseName(y.domainObj).equals(Tokens.anon) || !Type.isIdentity(y)))

    final def typeExists(aobj:Obj):Boolean = __.isAnon(aobj) || model.vars(str(aobj.name)).isDefined || model.gmap.fetchOrElse(ModelOp.TYPE, NOREC).gmap.exists(x => x._1.name == aobj.name) || model.dtypes.isEmpty

    final def search[A <: Obj](source:Obj = __, target:A, baseName:Boolean = true):List[A] = {
      model.vars[A](target.name)
        .map(x => if (x.isInstanceOf[Type[_]]) target.range.asInstanceOf[A] else x)
        .map(x => List(x))
        .getOrElse[List[A]](
          findType[A](model, source, target.name)  // TODO: graph.model.fpath()
            .map(y => if (baseName) toBaseName(y) else y))
    }

    final def findCtype[A <: Obj](name:String):Option[A] = model.gmap.fetchOrElse(TYPE, NOREC).gmap
      .filter(x => x._1.name == name)
      .flatMap(x => x._2.asInstanceOf[Lst[A]].g._2)
      .find(x => x.root)

    final def rewrites:List[Obj] = model.gmap.fetchOrElse(TYPE, NOREC).gmap.values.flatMap(x => x.g._2).filter(x => x.domainObj.name.equals(Tokens.lift_op))

    final def ctypes:List[Obj] = Option(Option(model.g._2).getOrElse(NOROOT).fetchOrElse(ModelOp.TYPE, NOREC).g._2).getOrElse(NOMAP)
      .filter(x => !x._2.glist.exists(y => y.domainObj.name != Tokens.lift_op)) // little optimization hack that will go away as model becomes more cleverly organized
      .map(x => x._1.asInstanceOf[Type[Obj]])

    final def dtypes:List[Type[Obj]] = Option(Option(model.g._2).getOrElse(NOROOT).fetchOrElse(ModelOp.TYPE, NOREC).g._2).getOrElse(NOMAP)
      .flatMap(x => x._2.glist)
      .filter(x => !x.root || (x.domainObj.name != x.rangeObj.name))
      .filter(x => x.domainObj.name != Tokens.lift_op) // little optimization hack that will go away as model becomes more cleverly organized
      .asInstanceOf[List[Type[Obj]]]

    final def vars[A <: Obj](key:StrValue):Option[A] = {
      Option(model)
        .map(m => m.g._2)
        .flatMap(m => m.fetchOption(ModelOp.VAR))
        .map(m => m.g._2)
        .flatMap(m => m.fetchOption(key))
        .map(x => x.glist.head)
        .asInstanceOf[Option[A]]
    }

    final def vars(key:StrValue, value:Obj):Model = {
      if (model.vars(key).isDefined && value.isInstanceOf[Type[_]]) return model
      val map = Option(model.g._2).getOrElse(NOROOT)
      val typesMap = Option(map.fetchOrElse(ModelOp.VAR, NOREC).g._2).getOrElse(NOMAP)
      nameModel(
        rec(model.name, g = (Tokens.`,`, map.filter(x => !x._1.equals(ModelOp.VAR)) :+ (ModelOp.VAR ->
          rec(g = (Tokens.`,`, typesMap.filter(x => !x._1.equals(key)) :+ (key -> lst(g = (Tokens.`,`, List(value.rangeObj))))))))))
    }

    final def vars:List[(StrValue, Obj)] = Option(Option(model.g._2).getOrElse(NOROOT).fetchOrElse(ModelOp.VAR, NOREC).g._2).getOrElse(NOMAP).map(x => (x._1.asInstanceOf[StrValue], x._2.glist.last))

    final def defining(definition:Obj):Model = {
      val map = Option(model.g._2).getOrElse(NOROOT)
      val typesMap = Option(map.fetchOrElse(ModelOp.TYPE, NOREC).g._2).getOrElse(NOMAP)
      val typeList = Option(typesMap.fetchOrElse(definition.range, lst).g._2).getOrElse(Nil)
      nameModel(if (typeList.contains(definition)) model
      else rec(model.name, g = (Tokens.`,`, map.replace(ModelOp.TYPE -> rec(g = (Tokens.`,`, typesMap.replace(definition.range -> lst(g = (Tokens.`,`, typeList :+ definition)))))))))
    }

    final def merging(other:Model):Model = {
      if (other.name == model.name) return model
      else if (other.isEmpty) return model
      else if (model.isEmpty) return other
      ///
      var x:Model = other.g._2.
        fetchOrElse(ModelOp.TYPE, rec(g = (Tokens.`,`, NOMAP))).g._2.
        flatMap(x => x._2.g._2).
        foldLeft(model)((a, b) => a.defining(b))
      x = other.ctypes.foldLeft(x)((a, b) => a.defining(b))
      x = other.g._2.fetchOrElse(ModelOp.VAR, rec(g = (Tokens.`,`, NOMAP))).gmap.foldLeft(x)((a, b) => a.vars(b._1.asInstanceOf[StrValue], b._2.asInstanceOf[Lst[Obj]].glist.head))
      nameModel(x)
    }
  }
}