package org.mmadt.language.obj.op.trace

import org.mmadt.language.Tokens
import org.mmadt.language.mmlang.mmlangParser
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.op.sideeffect.LoadOp
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Inst, Lst, Obj, Rec}
import org.mmadt.storage.obj.value.VInst

import scala.collection.mutable
import scala.io.{BufferedSource, Source}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait ModelOp {
  this: Obj =>
  def model(signature: Type[Obj], definition: Rec[Obj, Obj]): this.type = ModelOp(signature, definition).exec(this)
  def model(file:StrValue):this.type = this `=>` LoadOp.loadObj(file.g)
}

object ModelOp extends Func[Obj, Obj] {
  def apply[O <: Obj](signature: Type[Obj], definition: Rec[Obj, Obj]): Inst[O, O] = new VInst[O, O](g = (Tokens.model, List(signature, definition).asInstanceOf[List[O]]), func = this) with TraceInstruction

  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    /*val undefined = inst.args.filter(x => !Obj.fetch(start, x))
    if (undefined.isEmpty) start
    else start.via(start, inst.clone(g = (Tokens.define, undefined)))*/
    start.via(start, inst)
  }

  private def findTypeBase[A <: Obj](model: Rec[Obj, Obj], label: String): Iterable[A] = model.g._2.filter(x => x._1.name == label).flatMap(x => x._2.asInstanceOf[Lst[A]].g._2)
  def findType[A <: Obj](model: Rec[Obj, Obj], label: String): Option[A] = findTypeBase[A](model, label).find(y => y.name.equals(label))
  def findType[A <: Obj](model: Rec[Obj, Obj], label: String, source: Obj): Option[A] = findTypeBase[A](model, label).find(y => source.test(y.domain))
  def findType[A <: Obj](model: Rec[Obj, Obj], source: Obj): Option[A] = findTypeBase[A](model, source.name).find(y => source.via.equals(y.via))
  def getRewrites(model: Rec[Obj, Lst[Obj]]): List[Obj] = model.g._2.values.flatMap(y => y.g._2).filter(y => y.isInstanceOf[Lst[Obj]] && y.domain.isInstanceOf[Lst[Obj]] && y.domain.asInstanceOf[Lst[Obj]].g._2.nonEmpty).toList
}