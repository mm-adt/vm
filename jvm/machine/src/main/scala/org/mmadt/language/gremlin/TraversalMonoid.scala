package org.mmadt.language.gremlin
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.op.map.{GetOp, TailOp}
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.language.{LanguageException, Tokens}
import org.mmadt.storage.StorageFactory._

import scala.collection.JavaConverters

object TraversalMonoid {
  def resolve(op: String, args: List[Obj]): List[Inst[Obj, Obj]] = {
    (op match {
      case "out" => List(GetOp(str("outE")), IsOp(__.get(str("label")).eqs(args.head)), GetOp(str("inV")))
      case "outE" => List(GetOp(str("outE")), IsOp(__.get(str("label")).eqs(args.head)))
      case "inV" => List(GetOp(str("inV")))
      case "V" => List(GetOp(str("V")), IsOp(__.get(str("id").eqs(args.head))))
      case Tokens.tail => TailOp()
      case _ => throw LanguageException.unknownInstruction(op, JavaConverters.seqAsJavaList(args))
    }).asInstanceOf[List[Inst[Obj, Obj]]]
  }
}
