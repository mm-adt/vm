package org.mmadt.language.obj.op.trace

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.obj.value.VInst

trait TypeOp[+A <: Obj] {
  this: Obj =>
  def `type`(): Type[A] = TypeOp().exec(this).asInstanceOf[Type[A]]
}
object TypeOp extends Func[Obj, Type[Obj]] {
  def apply[A <: Obj](): Inst[A, Type[A]] = new VInst[A, Type[A]](g = (Tokens.`type`, Nil), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Type[Obj]]): Type[Obj] = start match {
    case atype: Type[Obj] => atype.via(start,inst)
    case _ => start.trace.map(x => x._2.via._1.asInstanceOf[Inst[Obj, Obj]]).foldLeft(start.domain[Obj])(op = (a, b) => b.exec(a).asInstanceOf[Type[Obj]])
  }
}