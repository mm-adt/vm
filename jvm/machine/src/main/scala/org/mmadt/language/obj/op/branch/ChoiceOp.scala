package org.mmadt.language.obj.op.branch

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{IntQ, Obj, Poly}
import org.mmadt.storage.StorageFactory.qOne
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
      val inst = new ChoiceInst[A](Poly.resolveSlots(start, apoly, this))
      (start match {
        case astrm: Strm[A] => return astrm.via(start, inst).asInstanceOf[Poly[A]]
        case _: Type[_] => inst.arg0[Poly[A]]()
        case _ => Poly.keepFirst(inst.arg0[Poly[A]]())
      }).clone(via = (start, inst))
    }
  }


}
