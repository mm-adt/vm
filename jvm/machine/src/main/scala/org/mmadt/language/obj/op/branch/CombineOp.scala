package org.mmadt.language.obj.op.branch

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.{Inst, Lst, Obj}
import org.mmadt.storage.obj.value.VInst

trait CombineOp[A <: Obj] {
  this: Lst[A] =>
  def combine[B <: Obj](other: Lst[B]): Lst[B] = CombineOp[A, B](other).exec(this).asInstanceOf[Lst[B]]
  final def :=[B <: Obj](other: Lst[B]): Lst[B] = this.combine[B](other)
}

object CombineOp extends Func[Lst[Obj], Lst[Obj]] {
  def apply[A <: Obj, B <: Obj](other: Obj): Inst[Obj, Lst[Obj]] = new VInst[Obj, Lst[Obj]](g = (Tokens.combine, List(other)), func = this) with BranchInstruction
  override def apply(start: Lst[Obj], inst: Inst[Lst[Obj], Lst[Obj]]): Lst[Obj] = {
    val temp = if (inst.arg0.isInstanceOf[Lst[Obj]]) inst.arg0[Lst[Obj]] else return start // TODO: no via?
    val combinedPoly: Lst[Obj] = temp.clone(start.glist.zip(temp.glist).map(a => Inst.resolveArg(a._1, a._2))).via(start, inst)
    temp.gsep match {
      case Tokens.| => Lst.keepFirst(combinedPoly)
      case _ => combinedPoly
    }
  }
}

