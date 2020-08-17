package org.mmadt.language.obj.op.rewrite

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.RewriteInstruction
import org.mmadt.language.obj.op.map.IdOp
import org.mmadt.language.obj.op.trace.NoOp
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.StorageFactory.qOne
import org.mmadt.storage.obj.value.VInst

import scala.annotation.tailrec

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */


object IdRewrite extends Func[Obj, Obj] {
  def apply(): Inst[Obj, Obj] = new VInst[Obj, Obj](g = (Tokens.rule_id, Nil), func = this) with RewriteInstruction

  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    start match {
      case _: __ => start.via(start, inst)
      case atype: Type[_] =>
        if (!exists(atype, IdOp())) return atype
        val newAtype = atype.trace.map(x => x._2).map(x => if (x.hardQ(qOne) == IdOp()) NoOp().q(x.q).asInstanceOf[Inst[Obj, Obj]] else x).foldLeft(atype.domainObj)((a, b) => b.exec(a)).asInstanceOf[atype.type]
        if (newAtype.pureQ != atype.pureQ)
          if (newAtype.root) newAtype.id.q(atype.pureQ) else newAtype.q(atype.pureQ)
        else newAtype
      case _ => start
    }
  }

  @tailrec
  def exists(aobj: Obj, inst: Inst[Obj, Obj]): Boolean = {
    if (aobj.root) false
    else if (aobj.via._2.q(qOne) == inst) true
    else exists(aobj.via._1, inst)
  }
}
