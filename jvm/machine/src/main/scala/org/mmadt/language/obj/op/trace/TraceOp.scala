package org.mmadt.language.obj.op.trace

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Inst, Lst, Obj}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait TraceOp {
  this: Obj =>
  def tracer(): Lst[Obj] = TraceOp().exec(this)
  def tracer(pattern: Lst[Obj]): Lst[Obj] = TraceOp(pattern).exec(this)
}
object TraceOp extends Func[Obj, Lst[Obj]] {
  def apply(): Inst[Obj, Lst[Obj]] = TraceOp.apply(__ `;` __)
  def apply(pattern: Lst[Obj]): Inst[Obj, Lst[Obj]] = new VInst[Obj, Lst[Obj]](g = (Tokens.trace, List(pattern)), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Lst[Obj]]): Lst[Obj] = (start match {
    case _: Strm[_] => start
    case _ => lst(
      inst.arg0[Lst[Obj]].gsep,
      start.trace.foldLeft(List.empty[Obj])((a, b) => a ++ (b._1 `;` b._2).combine(inst.arg0[Lst[Obj]]).glist): _*)
  }).via(start, inst).asInstanceOf[Lst[Obj]]
}