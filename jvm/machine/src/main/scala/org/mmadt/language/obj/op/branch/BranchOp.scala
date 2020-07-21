package org.mmadt.language.obj.op.branch

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Inst, Obj, Poly}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait BranchOp {
  this: Obj =>
  def branch[O <: Obj](branches: Obj): O = BranchOp(branches).exec(this)
}
object BranchOp extends Func[Obj, Obj] {
  def apply[A <: Obj](branches: Obj): Inst[Obj, A] = new VInst[Obj, A](g = (Tokens.branch, List(branches)), func = this) with BranchInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    val branches: Poly[Obj] = Inst.oldInst(inst).arg0[Poly[Obj]]
    val split: Poly[Obj] = start.split(branches)
    MergeOp().q(inst.q).exec(split) match {
      case astrm: Strm[Obj] => strm(astrm.values.map(x => x.clone(via = (start, inst))))
      case atype: Type[_] => atype.clone(via = (start, inst.clone(g = (Tokens.branch, List(Poly.resolveSlots(start, branches))))))
      case avalue: Value[_] => avalue.clone(via = (start, inst))
    }

  }
}
