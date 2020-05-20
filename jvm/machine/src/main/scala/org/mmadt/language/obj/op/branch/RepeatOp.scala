package org.mmadt.language.obj.op.branch
import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.{BoolValue, IntValue, Value}
import org.mmadt.language.obj.{Bool, Inst, Obj}
import org.mmadt.storage.obj.value.VInst

trait RepeatOp[A <: Obj] {
  this: A =>

  def repeat(branch: A)(until: Obj): A = RepeatOp(branch, until).exec(this)
}

object RepeatOp extends Func[Obj, Obj] {
  def apply[A <: Obj](branch: A, until: Obj): Inst[A, A] = new VInst[A, A](g = (Tokens.repeat, List(branch, until)), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    start match {
      case _: Value[_] if inst.arg1.isInstanceOf[Bool] =>
        var repeatStart = start;
        while (repeatStart.alive && Inst.resolveArg(repeatStart, Inst.oldInst(inst).arg1).asInstanceOf[BoolValue].g) {
          repeatStart = repeatStart ===> Inst.oldInst(inst).arg0
        }
        repeatStart
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