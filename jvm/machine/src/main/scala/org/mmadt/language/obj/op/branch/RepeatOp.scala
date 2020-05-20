package org.mmadt.language.obj.op.branch
import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.{IntValue, Value}
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.obj.value.VInst

trait RepeatOp[A <: Obj] {
  this: A =>

  def repeat(branch: A)(times: IntValue): A = RepeatOp(branch, times).exec(this)
}

object RepeatOp extends Func[Obj, Obj] {
  def apply[A <: Obj](branch: A, times: IntValue): Inst[A, A] = new VInst[A, A](g = (Tokens.repeat, List(branch, times)), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    start match {
      case _: Value[_] =>
        val times = inst.arg1.asInstanceOf[IntValue].g
        var repeatStart = start;
        var i = 0
        while (repeatStart.alive && i < times) {
          i = i + 1
          repeatStart = repeatStart ===> Inst.oldInst(inst).arg0
        }
        repeatStart
      case atype: Type[_] => atype.via(start, inst)
    }
  }
}