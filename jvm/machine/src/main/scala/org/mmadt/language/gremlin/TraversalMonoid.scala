package org.mmadt.language.gremlin
import org.mmadt.language.LanguageException
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.filter.IsOp
import org.mmadt.language.obj.op.map.GetOp
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.StorageFactory._

import scala.collection.JavaConverters

object TraversalMonoid {
  def resolve(op: String, args: List[Obj]): List[Inst[Obj, Obj]] = {
    (op match {
      case "out" => GetOp(str("outE")) +: args.map(x => IsOp(__.get(str("label")).eqs(x))) :+ GetOp(str("inV"))
      case "outE" => GetOp(str("outE")) +: args.map(x => IsOp(__.get(str("label")).eqs(x)))
      case "inV" => List(GetOp(str("inV")))
      case "outV" => List(GetOp(str("outV")))
      case "V" => GetOp(str("V")) +: args.map(x => IsOp(__.get(str("id")).eqs(x)))
      case _ => throw LanguageException.unknownInstruction(op, JavaConverters.seqAsJavaList(args))
    }).asInstanceOf[List[Inst[Obj, Obj]]]
  }
}
