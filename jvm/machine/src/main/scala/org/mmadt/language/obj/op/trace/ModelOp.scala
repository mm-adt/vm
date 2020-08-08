package org.mmadt.language.obj.op.trace

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.op.sideeffect.LoadOp
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{StrValue, Value}
import org.mmadt.language.obj.{Inst, Lst, Obj, Rec}
import org.mmadt.storage.StorageFactory.{lst, rec, str}
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ModelOp {
  this: Obj =>
  def model(definition: Model): this.type = ModelOp(definition).exec(this)
  def model(file: StrValue): this.type = ModelOp(LoadOp.loadObj[Model](file.g)).exec(this)
}

object ModelOp extends Func[Obj, Obj] {
  type Model = Rec[StrValue, ModelMap]
  type ModelMap = Rec[Type[Obj], Lst[Obj]]
  val TYPE: StrValue = str("type")
  val PATH: StrValue = str("path")
  val NOREC: ModelMap = rec[Type[Obj], Lst[Obj]]
  val EMPTY: Model = rec[StrValue, ModelOp.ModelMap]

  def apply[O <: Obj](definition: Model): Inst[O, O] = new VInst[O, O](g = (Tokens.model, List(definition).asInstanceOf[List[O]]), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = start match {
    case astrm: Strm[_] => astrm(x => inst.exec(x))
    case _: Value[Obj] => ModelOp.updateModel(inst.arg0[Model], start)
    case _: Type[Obj] => ModelOp.updateModel(inst.arg0[Model], start.via(start, inst))
  }
  def updateModel(amodel: Model, aobj: Obj): aobj.type = {
    if (amodel.isEmpty) aobj
    else if (aobj.root) aobj.clone(via = (aobj.model.merging(amodel), null))
    else aobj.rangeObj.clone(via = (aobj.trace.dropRight(1).foldLeft(aobj.domainObj.clone(via = (amodel, null)).asInstanceOf[Obj])((a, b) => b._1.via(a, b._2)), aobj.via._2))
  }
  def findType[A <: Obj](model: Model, label: String, source: Obj): Option[A] =
    (if (model.name.equals(label)) List(model).asInstanceOf[List[A]]
    else model.gmap.getOrElse(TYPE, NOREC).gmap.filter(x => x._1.name == label).flatMap(x => x._2.asInstanceOf[Lst[A]].g._2))
      .find(y => source.test(y.domain.hardQ(source.q)))
  def getRewrites(model: Model): List[Obj] = model.gmap.getOrElse(PATH, NOREC).gmap.values.flatMap(y => y.g._2).filter(y => y.isInstanceOf[Lst[Obj]] && y.domain.isInstanceOf[Lst[Obj]] && y.domain.asInstanceOf[Lst[Obj]].g._2.nonEmpty).toList
  def isMetaModel(inst: Inst[_, _]): Boolean = inst.op.equals(Tokens.model)

  @inline implicit def modelToRichModel(ground: Model): RichModel = new RichModel(ground)
  class RichModel(val model: Model) {
    final def definitions: List[Obj] = {
      val map: collection.Map[StrValue, ModelOp.ModelMap] = Option(model.g._2).getOrElse(collection.Map.empty).asInstanceOf[collection.Map[StrValue, ModelOp.ModelMap]]
      val typesMap: collection.Map[Type[Obj], Lst[Obj]] = Option(map.getOrElse[Rec[Type[Obj], Lst[Obj]]](ModelOp.TYPE, rec[Type[Obj], Lst[Obj]]).g._2).getOrElse(collection.Map.empty)
      typesMap.flatMap(x => x._2.glist).toList
    }

    final def rewriting(rewrite: Lst[Obj]): Model = {
      val map: collection.Map[StrValue, ModelOp.ModelMap] = Option(model.g._2).getOrElse(collection.Map.empty).asInstanceOf[collection.Map[StrValue, ModelOp.ModelMap]]
      val typesMap: collection.Map[Type[Obj], Lst[Obj]] = Option(map.getOrElse[Rec[Type[Obj], Lst[Obj]]](ModelOp.PATH, rec[Type[Obj], Lst[Obj]]).g._2).getOrElse(collection.Map.empty)
      val typeList: List[Obj] = Option(typesMap.getOrElse[Lst[Obj]](rewrite.domain.asInstanceOf[Lst[Obj]].g._2.head.domain, lst).g._2).getOrElse(Nil)
      if (typeList.contains(rewrite)) model
      else rec(g = (Tokens.`,`, map + (ModelOp.PATH -> rec(g = (Tokens.`,`, typesMap + (rewrite.domain.asInstanceOf[Lst[Obj]].g._2.head.domain -> lst(g = (Tokens.`,`, typeList :+ rewrite))))))))
    }

    final def defining(definition: Obj): Model = {
      val map: collection.Map[StrValue, ModelOp.ModelMap] = Option(model.g._2).getOrElse(collection.Map.empty).asInstanceOf[collection.Map[StrValue, ModelOp.ModelMap]]
      val typesMap: collection.Map[Type[Obj], Lst[Obj]] = Option(map.getOrElse[Rec[Type[Obj], Lst[Obj]]](ModelOp.TYPE, rec[Type[Obj], Lst[Obj]]).g._2).getOrElse(collection.Map.empty)
      val typeList: List[Obj] = Option(typesMap.getOrElse[Lst[Obj]](definition.range, lst).g._2).getOrElse(Nil)
      if (typeList.contains(definition)) model
      else rec(g = (Tokens.`,`, map + (ModelOp.TYPE -> rec(g = (Tokens.`,`, typesMap + (definition.range -> lst(g = (Tokens.`,`, typeList :+ definition))))))))
    }
    final def merging(other: Model): Model = {
      if (other.isEmpty) return model
      else if (model.isEmpty) return other
      val x: Model = other.g._2.getOrElse[Rec[Type[Obj], Lst[Obj]]](ModelOp.TYPE, rec(g = (Tokens.`,`, Map.empty))).g._2.flatMap(x => x._2.g._2).foldLeft(model)((a, b) => a.defining(b))
      rec(g = (Tokens.`,`, x.g._2 + (ModelOp.PATH -> other.g._2.getOrElse(ModelOp.PATH, rec[Type[Obj], Lst[Obj]]))))
    }
  }

}