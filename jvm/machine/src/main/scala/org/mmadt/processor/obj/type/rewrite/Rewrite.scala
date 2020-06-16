package org.mmadt.processor.obj.`type`.rewrite
import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.trace.DefineOp
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, Lst, Obj, withinQ}

trait Rewrite {
  type Writer = (List[Inst[Obj, Obj]], List[Inst[Obj, Obj]], Obj) => Obj
  def apply[A <: Obj](obj: A, writer: Writer): A
  def getDefines(obj: Obj): List[Obj] = obj.trace.filter(x => x._2.op == Tokens.define).map(x => x._2.arg0[Obj]).sortBy(x => -x.domainObj[Obj]().trace.length)
  def putDefines(defines: List[Obj], obj: Obj): Obj = obj.trace.map(x => x._2).foldLeft(defines.foldLeft(obj.domainObj[Obj]())((x, y) => DefineOp(y).exec(x)))((x, y) => y.exec(x))
  def removeDefines(obj: Obj): Obj = obj.trace.map(x => x._2).filter(x => x.op != Tokens.define).foldLeft(obj.domainObj[Obj]())((x, y) => y.exec(x))
  def getPolyOrObj(obj: Obj): Obj = obj.domain match {
    case alst: Lst[_] => alst.glist.head
    case _ => obj
  }

  def deflessEquals(aobj: Obj, bobj: Obj): Boolean = {
    bobj match {
      case bobj: Obj if !bobj.alive => !aobj.alive
      case _: Value[_] if aobj.isInstanceOf[Type[Obj]] &&
        aobj.trace.exists(x => x._2.op.equals(Tokens.from)) &&
        !bobj.trace.exists(x => x._2.op.equals(Tokens.from)) => true
      case _: Value[_] => aobj.test(bobj)
      case atype: Type[_] if aobj.isInstanceOf[Type[Obj]] =>
        (aobj.name.equals(atype.name) || __.isAnon(aobj) || __.isAnon(atype)) && withinQ(aobj, atype) &&
          removeDefines(aobj).trace.size == removeDefines(atype).trace.size &&
          removeDefines(aobj).trace.map(x => x._2).zip(removeDefines(atype).trace.map(x => x._2)).
            forall(insts => insts._1.op.equals(insts._2.op) && insts._1.args.zip(insts._2.args).forall(a => {
              deflessEquals(a._1, a._2)
            }))
      case _ => false
    }
  }
}
