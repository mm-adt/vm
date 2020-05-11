package org.mmadt.language.obj

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.branch.MergeOp
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm

trait Poly[A <: Obj] extends Obj with MergeOp[A] {
  def gsep: String
  def gvalues: Seq[A]
  def isSerial: Boolean = this.gsep == Tokens./ || this.gsep == Tokens.`;`
  def isParallel: Boolean = this.gsep == Tokens.\
  def isChoice: Boolean = this.gsep == Tokens.|

  def isValue: Boolean = this.isInstanceOf[Strm[_]] || (!this.gvalues.exists(x => x.alive && ((x.isInstanceOf[Type[_]] && !x.isInstanceOf[Poly[_]]) || (x.isInstanceOf[Poly[_]] && !x.asInstanceOf[Poly[_]].isValue))))
  def isType: Boolean = !this.gvalues.exists(x => x.alive && ((x.isInstanceOf[Value[_]] && !x.isInstanceOf[Poly[_]]) || (x.isInstanceOf[Poly[_]] && !x.asInstanceOf[Poly[_]].isType)))
}
object Poly {
  def resolveSlots[A <: Obj](start: A, apoly: Poly[A], inst: Inst[A, Poly[A]]): Poly[A] = {
    apoly match {
      case arec: Rec[Obj, A] => Rec.resolveSlots(start, arec, inst.asInstanceOf[Inst[Obj, Rec[Obj, A]]])
      case alst: Lst[A] => Lst.resolveSlots(start, alst, inst.asInstanceOf[Inst[A, Lst[A]]])
    }
  }
  def keepFirst[A <: Obj](apoly: Poly[A]): Poly[A] = {
    apoly match {
      case arec: Rec[Obj, A] => Rec.keepFirst(arec)
      case alst: Lst[A] => Lst.keepFirst(alst)
    }
  }
  def sameSep(apoly: Poly[_], bpoly: Poly[_]): Boolean = apoly.isChoice == bpoly.isChoice && apoly.isParallel == bpoly.isParallel && apoly.isSerial == bpoly.isSerial
}
