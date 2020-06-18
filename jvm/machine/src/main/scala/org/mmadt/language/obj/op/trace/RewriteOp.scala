package org.mmadt.language.obj.op.trace
import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.storage.obj.value.VInst

trait RewriteOp {
  this: Obj =>
  def rewrite(obj: Obj): this.type = RewriteOp(obj).exec(this)
}
object RewriteOp extends Func[Obj, Obj] {
  def apply[O <: Obj](obj: Obj): Inst[O, O] = new VInst[O, O](g = (Tokens.rewrite, List(obj)), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = if (!Obj.fetch(start, inst.arg0[Obj])) start.via(start, inst) else start
}