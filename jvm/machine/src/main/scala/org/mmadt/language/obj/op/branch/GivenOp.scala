package org.mmadt.language.obj.op.branch

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.value.Value
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
      val rangeObj: O = Inst.resolveArg(start, other)
      val inst = new GivenInst[O](rangeObj, q)
      start match {
        case _: Type[_] =>
          rangeObj match {
            case _: Value[_] => asType[O](rangeObj).map(rangeObj).via(start, inst)
            case _ => rangeObj
          }
        case _ => rangeObj.via(start, inst)
      }
    }
  }

}
