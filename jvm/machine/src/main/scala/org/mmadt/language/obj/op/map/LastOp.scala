package org.mmadt.language.obj.op.map
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.{Inst, Obj, Poly}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.obj.value.VInst

trait LastOp[A <: Obj] {
  this: Poly[A] =>
  def last(): A = LastOp().exec(this)
}
object LastOp extends Func[Obj, Obj] {
  def apply[A <: Obj](): Inst[Poly[A], A] = new VInst[Poly[A], A](g = (Tokens.last, Nil), func = this)
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    (start match {
      case apoly: Poly[_] => apoly.glist.reverse.find(_.alive).getOrElse(throw LanguageException.PolyException.noLast)
      case _ => start
    }).via(start, inst)
  }
}