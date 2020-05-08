package org.mmadt.language.obj.op.branch

import org.mmadt.language.Tokens
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.{Inst, IntQ, Obj, Poly}
import org.mmadt.storage.StorageFactory.qOne
import org.mmadt.storage.obj.value.VInst

trait CombineOp[A <: Obj] {
  this: Poly[A] =>
  def combine[B <: Obj](other: Poly[B]): Poly[B] = CombineOp[A, B](other).exec(this)
  final def :=[B <: Obj](other: Poly[B]): Poly[B] = this.combine[B](other)
}

object CombineOp {
  def apply[A <: Obj, B <: Obj](other: Poly[B]): CombineInst[A, B] = new CombineInst[A, B](other)

  class CombineInst[A <: Obj, B <: Obj](other: Poly[B], q: IntQ = qOne) extends VInst[Poly[A], Poly[B]]((Tokens.combine, Nil), q) with BranchInstruction {
    override def q(q: IntQ): this.type = new CombineInst[A, B](other, q).asInstanceOf[this.type]
    override def exec(start: Poly[A]): Poly[B] = {
      val combinedPoly = other.clone(start.groundList.zip(other.groundList).map(a => Inst.resolveArg(a._1, a._2))).via(start, this)
      other.groundConnective match {
        case Tokens.:| => Poly.keepFirst(combinedPoly)
        case _ => combinedPoly
      }
    }
  }

}

