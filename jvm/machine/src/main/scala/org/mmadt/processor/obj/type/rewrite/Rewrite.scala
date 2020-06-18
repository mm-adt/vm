package org.mmadt.processor.obj.`type`.rewrite
import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.trace.RewriteOp
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, Lst, Obj, _}

trait Rewrite {
  type Writer = (List[Inst[Obj, Obj]], List[Inst[Obj, Obj]], Obj) => Obj
  def apply[A <: Obj](obj: A, writer: Writer): A

  // utility methods
  def getRewrites(obj: Obj): List[Obj] = obj.trace.filter(x => x._2.op == Tokens.rewrite).map(x => x._2.arg0[Obj]).sortBy(x => -x.domainObj[Obj].trace.length)
  def putRewrites(rewrites: List[Obj], obj: Obj): Obj = obj.trace.map(x => x._2).foldLeft(rewrites.foldLeft(obj.domainObj[Obj])((x, y) => RewriteOp(y).exec(x)))((x, y) => y.exec(x))
  def removeRewrites(obj: Obj): Obj = obj.trace.map(x => x._2).filter(x => x.op != Tokens.rewrite).foldLeft(obj.domainObj[Obj])((x, y) => y.exec(x))
  def getPolyOrObj(obj: Obj): Obj = obj.domain match {
    case alst: Lst[_] => alst.glist.head
    case _ => obj
  }
  def rewriteLessEquals(aobj: Obj, bobj: Obj): Boolean = {
    bobj match {
      case bobj: Obj if !bobj.alive => !aobj.alive
      case _: Value[_] => aobj.equals(bobj)
      case atype: Type[_] if aobj.isInstanceOf[Type[Obj]] =>
        aobj.name.equals(atype.name) && eqQ(aobj, atype) &&
          removeRewrites(aobj).trace.size == removeRewrites(atype).trace.size &&
          removeRewrites(aobj).trace.map(x => x._2).zip(removeRewrites(atype).trace.map(x => x._2)).
            forall(insts => insts._1.op.equals(insts._2.op) && insts._1.args.zip(insts._2.args).forall(a => {
              rewriteLessEquals(a._1, a._2)
            }))
      case _ => false
    }
  }
}
