package org.mmadt.language.obj.op.branch

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Inst, IntQ, Obj}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait GivenOp {
  this: Obj =>
  def given[O <: Obj](other: O): O = GivenOp[O](other).exec(this)
  final def `-->`[O <: Obj](other: O): O = this.given[O](other)
}

object GivenOp {
  def apply[O <: Obj](other: O): Inst[Obj, O] = new GivenInst(other)

  class GivenInst[O <: Obj](other: O, q: IntQ = qOne) extends VInst[Obj, O]((Tokens.given, List(other)), q) with BranchInstruction {
    override def q(q: IntQ): this.type = new GivenInst[O](other, q).asInstanceOf[this.type]
    override def exec(start: Obj): O = {
      val rangeObj: O = Inst.resolveArg(lastBranch(start), other)
      val inst = new GivenInst[O](rangeObj, q)
      (start match {
        case astrm: Strm[_] => return astrm.via(start, inst).asInstanceOf[O]
        case _: Value[_] => rangeObj // TODO: look at split test and to/from in branch
        case _: Type[_] =>
          rangeObj match {
            case _: Strm[_] => rangeObj
            case _: Type[_] => rangeObj
            case _: Value[_] => asType[O](rangeObj).map(rangeObj)
          }
      }).via(start, inst)
    }
  }

  @scala.annotation.tailrec
  private def lastBranch[O](obj: Obj): O = {
    if (obj.root) return obj.asInstanceOf[O]
    if (obj.via._2.isInstanceOf[BranchInstruction])
      return obj.asInstanceOf[O]
    lastBranch[O](obj.via._1)
  }

}
