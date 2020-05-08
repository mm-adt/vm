package org.mmadt.language.obj.op.map

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{IntQ, Obj, Poly}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait TraceOp {
  this: Obj =>
  def tracer(): Poly[Obj] = TraceOp().exec(this)
  def tracer(pattern: Poly[Obj]): Poly[Obj] = TraceOp(pattern).exec(this)
}

object TraceOp {
  def apply(): TraceInst = TraceOp.apply(__ / __)
  def apply(pattern: Poly[Obj]): TraceInst = new TraceInst(pattern)

  class TraceInst(pattern: Poly[Obj], q: IntQ = qOne) extends VInst[Obj, Poly[Obj]]((Tokens.tracer, List(pattern)), q) {
    override def q(q: IntQ): this.type = new TraceInst(pattern, q).asInstanceOf[this.type]
    override def exec(start: Obj): Poly[Obj] = {
      (start match {
        case _: Strm[_] => start
        case _ => poly(Tokens.:/, start.trace.foldLeft(List.empty[Obj])((a, b) => a ++ (b._1 / b._2).combine(pattern).groundList) ++ (start / this).combine(pattern).groundList: _*)
      }).via(start, this).asInstanceOf[Poly[Obj]]
    }
  }

}