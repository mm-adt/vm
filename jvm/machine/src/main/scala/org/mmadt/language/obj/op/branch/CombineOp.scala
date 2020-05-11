package org.mmadt.language.obj.op.branch

import org.mmadt.language.Tokens
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.{Inst, IntQ, Obj, Lst}
import org.mmadt.storage.StorageFactory.qOne
import org.mmadt.storage.obj.value.VInst

trait CombineOp[A <: Obj] {
  this: Lst[A] =>
  def combine[B <: Obj](other: Lst[B]): Lst[B] = CombineOp[A, B](other).exec(this)
  final def :=[B <: Obj](other: Lst[B]): Lst[B] = this.combine[B](other)
}

object CombineOp {
  def apply[A <: Obj, B <: Obj](other: Lst[B]): CombineInst[A, B] = new CombineInst[A, B](other)

  class CombineInst[A <: Obj, B <: Obj](other: Lst[B], q: IntQ = qOne) extends VInst[Lst[A], Lst[B]]((Tokens.combine, Nil), q) with BranchInstruction {
    override def q(q: IntQ): this.type = new CombineInst[A, B](other, q).asInstanceOf[this.type]
    override def exec(start: Lst[A]): Lst[B] = {
      val combinedPoly = other.clone(start.gvalues.zip(other.gvalues).map(a => Inst.resolveArg(a._1, a._2))).via(start, this)
      other.gsep match {
        case Tokens.| => Lst.keepFirst(combinedPoly)
        case _ => combinedPoly
      }
    }
  }

}

