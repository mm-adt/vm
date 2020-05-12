package org.mmadt.language.obj.op.trace

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{IntQ, Lst, Obj}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait TraceOp {
  this: Obj =>
  def tracer(): Lst[Obj] = TraceOp().exec(this)
  def tracer(pattern: Lst[Obj]): Lst[Obj] = TraceOp(pattern).exec(this)
}

object TraceOp {
  def apply(): TraceInst = TraceOp.apply(__ `;` __)
  def apply(pattern: Lst[Obj]): TraceInst = new TraceInst(pattern)

  class TraceInst(pattern: Lst[Obj], q: IntQ = qOne) extends VInst[Obj, Lst[Obj]](ground = (Tokens.trace, List(pattern)), q = q) with TraceInstruction {
    override def q(q: IntQ): this.type = new TraceInst(pattern, q).asInstanceOf[this.type]
    override def exec(start: Obj): Lst[Obj] = {
      (start match {
        case _: Strm[_] => start
        case _ => lst(pattern.gsep, start.trace.foldLeft(List.empty[Obj])((a, b) => a ++ (b._1 `;` b._2).combine(pattern).gvalues) ++ (start `;` this).combine(pattern).gvalues: _*)
      }).via(start, this).asInstanceOf[Lst[Obj]]
    }
  }

}