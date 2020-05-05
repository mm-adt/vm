package org.mmadt.language.obj.op.branch

import org.mmadt.language.Tokens
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Inst, IntQ, Obj, Poly}
import org.mmadt.storage.StorageFactory.{qOne, strm}
import org.mmadt.storage.obj.value.VInst

trait ChoiceOp {
  this: Obj =>
  def choice[A <: Obj](brch: Poly[A]): Poly[A] = ChoiceOp(brch).exec(this.asInstanceOf[A])
  final def ~<[A <: Obj](brch: Poly[A]): Poly[A] = this.choice(brch)
}

object ChoiceOp {
  def apply[A <: Obj](branches: Poly[A]): ChoiceInst[A] = new ChoiceInst[A](branches)

  class ChoiceInst[A <: Obj](apoly: Poly[A], q: IntQ = qOne) extends VInst[A, Poly[A]]((Tokens.choice, List(apoly)), q) with BranchInstruction {
    override def q(q: IntQ): this.type = new ChoiceInst[A](apoly, q).asInstanceOf[this.type]
    override def exec(start: A): Poly[A] = {
      start match {
        case astrm: Strm[A] => strm(astrm.values.map(x => Poly.keepFirst(apoly.clone(apoly.ground._2.map(y => Inst.resolveArg(x, y)).filter(_.alive))))).clone(via = (start, this))
        case _ => Poly.keepFirst(apoly.clone(apoly.ground._2.map(x => Inst.resolveArg(start, x))).clone(via = (start, this)))
      }
    }
  }

}
