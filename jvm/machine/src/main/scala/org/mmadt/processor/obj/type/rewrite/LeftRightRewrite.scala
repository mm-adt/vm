package org.mmadt.processor.obj.`type`.rewrite
import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.{BranchInstruction, OpInstResolver, TraceInstruction}
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, Lst, Obj, withinQ}

object LeftRightRewrite extends Rewrite {
  def apply[O <: Obj](obj: O, writer: Writer): O = {
    if (!obj.trace.exists(x => x._2.op.equals(Tokens.define))) return obj

    val defines = obj.trace.map(x => x._2).filter(x => x.op.equals(Tokens.define)).map(x => x.arg0[Obj])
    var mutating: O = obj.domain.asInstanceOf[O]
    var previous: O = obj
    while (!rewriteLessEquals(previous, mutating)) {
      mutating = previous
      previous = LeftRightRewrite.rewrite(defines, mutating, obj.domain, obj.domain).asInstanceOf[O]
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
  private def rewriteArgs[S <: Obj](defines: List[Obj], start: S, inst: Inst[Obj, Obj]): List[Obj] = {
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
      case btype: Type[_] => btype
    }).filter(x => rewriteLessEquals(x, atype))
      .map(_ => {
        bindLeftValuesToRightVariables(a.range match {
          case apoly: Lst[_] => apoly.glist.head
          case btype: Type[_] => btype
        }, atype)
      }))
      .filter(_.isDefined)
      .map(_.get)
      .headOption
  }

  private def bindLeftValuesToRightVariables(left: Obj, right: Obj): Obj = {
    if (!left.toString.contains("<.")) return left // TODO: super ghetto (search for the binding variable with recurssion
    val temp = nodefine(left).zip(nodefine(right))
      .map(x => OpInstResolver.resolve[Obj, Obj](x._1.op, x._1.args.zip(x._2.args).map(x => x._2)))
      .foldLeft(right.domainObj.asInstanceOf[Obj])((a, b) => b.exec(a))
    temp
  }

  /*def deflessEquals(aobj: Obj, bobj: Obj): Boolean = {

    bobj match {
      case bobj: Obj if !bobj.alive => !aobj.alive
      case _: Value[_] if aobj.isInstanceOf[Type[Obj]] &&
        aobj.trace.exists(x => x._2.op.equals(Tokens.from)) &&
        !bobj.trace.exists(x => x._2.op.equals(Tokens.from)) => true
      case _: Value[_] => aobj.test(bobj)
      case atype: Type[_] if aobj.isInstanceOf[Type[Obj]] =>
        (aobj.name.equals(atype.name) || __.isAnon(aobj) || __.isAnon(atype)) && withinQ(aobj, atype) &&
          nodefine(aobj).size == nodefine(atype).size &&
          nodefine(aobj).zip(nodefine(atype)).
            forall(insts => insts._1.op.equals(insts._2.op) && insts._1.args.zip(insts._2.args).forall(a => {
              deflessEquals(a._1, a._2)
            }))
      case _ => false
    }
  }*/

  private def nodefine(aobj: Obj): List[Inst[Obj, Obj]] = aobj.trace.filter(x => !x._2.op.equals(Tokens.define)).map(_._2)
}
