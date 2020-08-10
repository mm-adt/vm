package org.mmadt.language.obj.op.trace

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.op.sideeffect.LoadOp
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{StrValue, Value}
import org.mmadt.language.obj.{Inst, Lst, Obj, Rec}
import org.mmadt.storage.StorageFactory.{lst, rec, str, toBaseName}
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ModelOp {
  this: Obj =>
  def update(model: Model): this.type = ModelOp.updateModel(model, this)
  def model(model: Model): this.type = ModelOp(model).exec(this)
  def model(file: StrValue): this.type = ModelOp(LoadOp.loadObj[Model](file.g)).exec(this)
}

object ModelOp extends Func[Obj, Obj] {
  type Model = Rec[StrValue, ModelMap]
  type ModelMap = Rec[Obj, Lst[Obj]]
  val TYPE: StrValue = str("type")
  val PATH: StrValue = str("path")
  val VAR: StrValue = str("var")
  val NOROOT: collection.Map[StrValue, ModelOp.ModelMap] = Map.empty
  val NOMAP: collection.Map[Obj, Lst[Obj]] = Map.empty
  val NOREC: ModelMap = rec[Obj, Lst[Obj]]
  val EMPTY: Model = rec[StrValue, ModelOp.ModelMap]

  def apply[O <: Obj](definition: Model): Inst[O, O] = new VInst[O, O](g = (Tokens.model, List(definition).asInstanceOf[List[O]]), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = start match {
    case astrm: Strm[_] => astrm(x => inst.exec(x))
    case _: Value[Obj] => start.update(inst.arg0[Model])
    case _: Type[Obj] => start.via(start, inst).update(inst.arg0[Model])
  }
  def updateModel(amodel: Model, aobj: Obj): aobj.type = {
    if (amodel.isEmpty) aobj
    else if (aobj.root) aobj.clone(via = (aobj.model.merging(amodel), null))
    else aobj.rangeObj.clone(via = (aobj.trace.dropRight(1).foldLeft(aobj.domainObj.clone(via = (amodel, null)).asInstanceOf[Obj])((a, b) => b._1.via(a, b._2)), aobj.via._2))
  }
  def isMetaModel(inst: Inst[_, _]): Boolean = inst.op.equals(Tokens.model)

  @inline implicit def modelToRichModel(ground: Model): RichModel = new RichModel(ground)
  class RichModel(val model: Model) {
    private final def findType[A <: Obj](model: Model, label: String, source: Obj): Option[A] =
      (if (model.name.equals(label)) List(model).asInstanceOf[List[A]]
      else model.gmap.getOrElse[ModelMap](TYPE, NOREC).gmap.filter(x => x._1.name == label).flatMap(x => x._2.asInstanceOf[Lst[A]].g._2))
        .find(y => if (source.equals(__)) true else source.test(y.domain.hardQ(source.q)))

    final def search[A <: Obj](name: StrValue, matcher: A = __.asInstanceOf[A]): Option[A] =
      model.vars[A](name).map(x => if (x.isInstanceOf[Type[_]]) x.from(name) else x).orElse(findType[A](model, name.g, matcher).map(y => toBaseName(y))).map(x => x.update(model))
    final def rewrites: List[Obj] = model.gmap.getOrElse[ModelMap](PATH, NOREC).gmap.values
      .flatMap(y => y.g._2)
      .filter(y => y.isInstanceOf[Lst[Obj]] && y.domain.isInstanceOf[Lst[Obj]] && y.domain.asInstanceOf[Lst[Obj]].g._2.nonEmpty).toList

    final def definitions: List[Obj] = {
      val map = Option(model.g._2).getOrElse(NOROOT)
      val typesMap = Option(map.getOrElse(ModelOp.TYPE, NOREC).g._2).getOrElse(NOMAP)
      typesMap.flatMap(x => x._2.glist).toList
    }

    final def vars[A <: Obj](key: StrValue): Option[A] = {
      val map = Option(model.g._2).getOrElse(NOROOT)
      val typesMap = Option(map.getOrElse(ModelOp.VAR, NOREC).g._2).getOrElse(NOMAP)
      typesMap.get(key).map(x => x.glist.head).asInstanceOf[Option[A]]
    }

    final def varing(key: StrValue, value: Obj): Model = {
      if (model.vars(key).isDefined && value.isInstanceOf[Type[_]]) return model
      val map = Option(model.g._2).getOrElse(NOROOT)
      val typesMap = Option(map.getOrElse(ModelOp.VAR, NOREC).g._2).getOrElse(NOMAP)
      rec(g = (Tokens.`,`, map + (ModelOp.VAR -> rec(g = (Tokens.`,`, typesMap + (key -> lst(g = (Tokens.`,`, List(value.rangeObj)))))))))
    }

    final def rewriting(rewrite: Lst[Obj]): Model = {
      val map = Option(model.g._2).getOrElse(NOROOT)
      val typesMap = Option(map.getOrElse(ModelOp.PATH, NOREC).g._2).getOrElse(NOMAP)
      val typeList = Option(typesMap.getOrElse(rewrite.domain.asInstanceOf[Lst[Obj]].g._2.head.domain, lst).g._2).getOrElse(Nil)
      if (typeList.contains(rewrite)) model
      else rec(g = (Tokens.`,`, map + (ModelOp.PATH -> rec(g = (Tokens.`,`, typesMap + (rewrite.domain.asInstanceOf[Lst[Obj]].g._2.head.domain -> lst(g = (Tokens.`,`, typeList :+ rewrite))))))))
    }

    final def defining(definition: Obj): Model = {
      val map = Option(model.g._2).getOrElse(NOROOT)
      val typesMap = Option(map.getOrElse[ModelMap](ModelOp.TYPE, NOREC).g._2).getOrElse(NOMAP)
      val typeList = Option(typesMap.getOrElse(definition.range, lst).g._2).getOrElse(Nil)
      if (typeList.contains(definition)) model
      else rec(g = (Tokens.`,`, map + (ModelOp.TYPE -> rec(g = (Tokens.`,`, typesMap + (definition.range -> lst(g = (Tokens.`,`, typeList :+ definition))))))))
    }

    final def merging(other: Model): Model = {
      if (other.isEmpty) return model
      else if (model.isEmpty) return other
      var x: Model = other.g._2.getOrElse[ModelMap](ModelOp.TYPE, rec(g = (Tokens.`,`, NOMAP))).g._2.flatMap(x => x._2.g._2).foldLeft(model)((a, b) => a.defining(b))
      x = other.g._2.getOrElse[ModelMap](ModelOp.VAR, rec(g = (Tokens.`,`, NOMAP))).gmap.foldLeft(x)((a, b) => a.varing(b._1.asInstanceOf[StrValue], b._2.asInstanceOf[Lst[Obj]].glist.head))
      rec(g = (Tokens.`,`, x.g._2 + (ModelOp.PATH -> other.g._2.getOrElse(ModelOp.PATH, NOREC))))
    }
  }

}