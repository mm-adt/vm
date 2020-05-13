package org.mmadt.language.obj.op.trace

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.{Inst, OType, OValue, Obj}
import org.mmadt.storage.obj.value.VInst
import org.mmadt.storage.StorageFactory._

trait TypeOp[+A <: Obj] {
  this: Obj =>
  def `type`(): OType[A] = TypeOp().exec(this.asInstanceOf[OValue[A]])
}
object TypeOp extends Func[OValue[Obj], OType[Obj]] {
  def apply[A <: Obj](): Inst[OValue[A], OType[A]] = new VInst[OValue[A], OType[A]](g = (Tokens.`type`, Nil), func = this) with TraceInstruction
  override def apply(start: OValue[Obj], inst: Inst[OValue[Obj], OType[Obj]]): OType[Obj] = start match {
    case atype: OType[Obj] => atype
    case _ => start.trace.map(x => x._2.via._1.asInstanceOf[Inst[Obj, Obj]]).foldLeft(start.domain[Obj])(op = (a, b) => b.exec(a).asInstanceOf[Type[Obj]])
  }
}