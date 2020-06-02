package org.mmadt.language.obj.op.trace
import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.value.StrValue
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.obj.value.VInst

trait DefineOp {
  this: Obj =>
  def define(name: StrValue, obj: Obj): this.type = DefineOp(name, obj).exec(this)
}
object DefineOp extends Func[Obj, Obj] {
  def apply[O <: Obj](name: StrValue, obj: Obj): Inst[O, O] = new VInst[O, O](g = (Tokens.define, List(name, obj)), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = if (Obj.fetchOption(start, inst.arg0[StrValue].g).isEmpty) start.via(start, Inst.oldInst(inst)) else start
}