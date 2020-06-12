package org.mmadt.language.obj.op.map
import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.obj.value.VInst

trait JuxtaOp {
  this: Obj =>
  def juxta[A <: Obj](right: A): A = JuxtaOp(right).exec(this)
  def `=>`[A <: Obj](right: A): A = this.juxta(right)
}
object JuxtaOp extends Func[Obj, Obj] {
  def apply[A <: Obj](right: A): Inst[Obj, A] = new VInst[Obj, A](g = (Tokens.juxt, List(right)), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = inst.arg0[Obj]
}