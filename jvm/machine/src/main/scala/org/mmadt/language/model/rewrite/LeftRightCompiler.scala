package org.mmadt.language.model.rewrite
import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.{BranchInstruction, OpInstResolver, TraceInstruction}
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, Lst, Obj, withinQ}

object LeftRightCompiler {

  def execute[O <: Obj](obj: O): O = {
    if (!obj.trace.exists(x => x._2.op.equals(Tokens.define)))
      return obj

    val defines = obj.trace.map(x => x._2).filter(x => x.op.equals(Tokens.define)).map(x => x.arg1[Obj])

    var mutating: O = obj.domain.asInstanceOf[O]
    var previous: O = obj
    while (!deflessEquals(previous, mutating)) {
      mutating = previous
      previous = LeftRightCompiler.rewrite(defines, mutating, obj.domain, obj.domain).asInstanceOf[O]
    }
    mutating
  }

  def rewrite[S <: Obj](defines: List[Obj], atype: S, btype: S, start: S): S = {
    if (!atype.root) {
      search(defines, atype) match {
        case Some(right: S) => rewrite(defines, right, btype, start)
        case None =>
          val inst: Inst[Obj, Obj] = OpInstResolver.resolve(atype.via._2.op, rewriteArgs(defines, atype.rinvert[S]().range, atype.via._2.asInstanceOf[Inst[Obj, Obj]])).q(atype.via._2.q)
          rewrite(defines, atype.rinvert(), inst.exec(atype.rinvert[Obj]().range).compute(btype), start)
      }
    } else if (!btype.root) rewrite(defines, btype.linvert(), btype.linvert().domain, btype.trace.head._2.exec(start)).asInstanceOf[S]
    else start
  }

  // if no match, then apply the instruction after rewriting its arguments
  private def rewriteArgs[S <: Obj](defines: List[Obj], start: Type[S], inst: Inst[Obj, Obj]): List[Obj] = {
    inst match {
      case _: TraceInstruction => inst.args
      case _: BranchInstruction => inst.args
      case _ => inst.args.map {
        case avalue: Value[_] => avalue
        case atype: Type[_] => rewrite(defines, atype, start.domain, start.domain)
      }
    }
  }

  def search(defines: List[Obj], atype: Obj): Option[Obj] = {
    defines.map(a => Some(a.domain match {
      case apoly: Lst[_] => apoly.glist.head
      case btype: Type[Obj] => btype
    }).filter(x => deflessEquals(x, atype))
      .map(_ =>
        a.range match {
          case apoly: Lst[_] => apoly.glist.head
          case btype: Type[Obj] => btype
        }))
      .filter(_.isDefined)
      .map(_.get)
      .headOption
  }

  def deflessEquals(aobj: Obj, bobj: Obj): Boolean = bobj match {
    case bobj: Obj if !bobj.alive => !aobj.alive
    case atype: Type[_] if aobj.isInstanceOf[Type[Obj]] =>
      (aobj.name.equals(atype.name) || __.isAnon(aobj) || __.isAnon(atype)) && withinQ(aobj, atype) &&
        aobj.trace.count(x => !x._2.op.equals(Tokens.define)) == atype.trace.count(x => !x._2.op.equals(Tokens.define)) &&
        aobj.trace.filter(x => !x._2.op.equals(Tokens.define)).map(_._2).zip(atype.trace.filter(x => !x._2.op.equals(Tokens.define)).map(_._2)).
          forall(insts => insts._1.op.equals(insts._2.op) && insts._1.args.zip(insts._2.args).forall(a => {
            deflessEquals(a._1, a._2)
          }))
    case avalue: Value[_] => aobj.test(avalue)
    case _ => false
  }
}
