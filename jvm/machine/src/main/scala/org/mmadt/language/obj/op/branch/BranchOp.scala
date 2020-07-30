package org.mmadt.language.obj.op.branch

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
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
      case astrm: Strm[Obj] => strm(astrm.values.map(x => x.clone(via = (start, inst))).filter(_.alive))
      case atype: Type[_] =>
        val rpoly: Poly[Obj] = Poly.resolveSlots(start, branches, branch = true)
        if (!atype.alive || rpoly.isEmpty) zeroObj
        else if (1 == rpoly.glist.length && !(rpoly.isInstanceOf[Rec[Obj, Obj]] && rpoly.asInstanceOf[Rec[Obj, Obj]].g._2.head._1.q._1.g == 0))
          Inst.resolveArg(start,
            if (rpoly.glist.head.root) rpoly.glist.head.q(multQ(rpoly.glist.head.q, inst.q))
            else rpoly.glist.head.q(inst.q))
        else atype.clone(via = (start, inst.clone(g = (Tokens.branch, List(rpoly)))))
      case avalue: Value[_] => avalue.clone(via = (start, inst))
    }

  }
}
