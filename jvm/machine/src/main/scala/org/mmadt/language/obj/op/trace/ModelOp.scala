package org.mmadt.language.obj.op.trace

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.op.sideeffect.LoadOp
import org.mmadt.language.obj.op.trace.ModelOp.Model
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Inst, Lst, Obj, Rec}
import org.mmadt.storage.StorageFactory.{rec, str}
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

  def apply[O <: Obj](definition: Model): Inst[O, O] = new VInst[O, O](g = (Tokens.model, List(definition).asInstanceOf[List[O]]), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = Obj.updateModel(start.domainObj.clone(via = (inst.arg0[Model], null)), start.via(start, inst))

  private def findTypeBase[A <: Obj](model: Model, label: String): Iterable[A] =
    if (model.name.equals(label)) List(model).asInstanceOf[List[A]]
    else model.gmap.getOrElse(TYPE, NOREC).gmap.filter(x => x._1.name == label).flatMap(x => x._2.asInstanceOf[Lst[A]].g._2)
  def findType[A <: Obj](model: Model, label: String): Option[A] = findTypeBase[A](model, label).find(y => y.name.equals(label))
  def findType[A <: Obj](model: Model, label: String, source: Obj): Option[A] = findTypeBase[A](model, label).find(y => source.test(y.domain))
  def findType[A <: Obj](model: Model, source: Obj): Option[A] = findTypeBase[A](model, source.name).find(y => source.via.equals(y.via))
  def getRewrites(model: Model): List[Obj] = model.gmap.getOrElse(PATH, NOREC).gmap.values.flatMap(y => y.g._2).filter(y => y.isInstanceOf[Lst[Obj]] && y.domain.isInstanceOf[Lst[Obj]] && y.domain.asInstanceOf[Lst[Obj]].g._2.nonEmpty).toList
  def isMetaModel(inst: Inst[_, _]): Boolean = inst.op.equals(Tokens.model) || inst.op.equals(Tokens.define) || inst.op.equals(Tokens.rewrite)
}