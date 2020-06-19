package org.mmadt.language.obj.op.trace
import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.obj.value.VInst

trait DefineOp {
  this: Obj =>
  def define(obj: Obj): this.type = DefineOp(obj).exec(this)
}
object DefineOp extends Func[Obj, Obj] {
  def apply[O <: Obj](obj: Obj): Inst[O, O] = new VInst[O, O](g = (Tokens.define, List(obj.asInstanceOf[O])), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = if (!Obj.fetch(start, inst.arg0[Obj])) start.via(start, inst) else start
}