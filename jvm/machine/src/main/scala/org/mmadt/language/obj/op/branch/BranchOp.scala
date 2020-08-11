package org.mmadt.language.obj.op.branch

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{Type, __}
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
  def branch[O <: Obj](branches: Poly[O]): O = BranchOp(branches).exec(this)
  def branch[O <: Obj](branches: __): O = BranchOp(branches).exec(this)
}

object BranchOp extends Func[Obj, Obj] {
  def apply[A <: Obj](branches: Obj): Inst[Obj, A] = new VInst[Obj, A](g = (Tokens.branch, List(branches)), func = this) with BranchInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    start match {
      case _: Strm[_] => start.via(start, inst)
      case _ => Inst.oldInst(inst).arg0[Poly[Obj]] match {
        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////
        case alst: Lst[Obj] => alst.gsep match {
          case Tokens.`,` =>
            val result: List[Obj] = Type.mergeObjs(alst.g._2.map(b => Inst.resolveArg(start, b)).filter(_.alive))
            val apoly: Lst[Obj] = alst.clone(g = (alst.gsep, result))
            apoly match {
              case _: Value[_] => strm(result.map(x => x.hardQ(q => multQ(q, inst.q))).filter(_.alive))
              case _: Type[_] =>
                if (result.isEmpty) zeroObj
                else if (1 == result.size) if (result.head.alive) Type.tryCtype(start `=>` result.head.q(inst.q)) else zeroObj
                else BranchInstruction.brchType[Obj](apoly, inst.q).clone(via = (start, inst.clone(g = (Tokens.branch, List(apoly)))))
            }
          case Tokens.`;` =>
            var running = start
            val result = alst.g._2.map(b => {
              running = running match {
                case astrm: Strm[_] => strm(astrm.values.map(r => Inst.resolveArg(r, b)))
                case r if b.isInstanceOf[Value[_]] => r `=>` b
                case r: Type[_] if r.root && b.root && r.name == b.name => r `=>` b
                case _ => Inst.resolveArg(running, b)
              }
              running
            })
            val apoly = alst.clone(g = (alst.gsep, result))
            if (result.exists(b => !b.alive)) zeroObj
            else if (result.forall(x => Type.isIdentity(x))) {
              val finalQ = multQ(result.last.q, inst.q)
              if (result.last.isInstanceOf[Value[_]]) return result.last.hardQ(finalQ)
              var finalO = if (!result.last.root) result.last.hardQ(finalQ) else Type.unity(result.last).q(finalQ)
              finalO = if (Type.isIdentity(finalO) && finalO.domain.q == qOne && finalO.range.q == qOne) finalO.range else Type.unity(finalO).q(finalQ) // TODO: ghetto repeat.
              if (Type.isIdentity(finalO) && finalO.domain.q == qOne && finalO.range.q == qOne) finalO.range else Type.unity(finalO).q(finalQ)
            }
            else apoly match {
              case _: Value[_] => result.last.hardQ(q => multQ(q, inst.q))
              case _: Type[_] => BranchInstruction.brchType[Obj](apoly, inst.q).clone(via = (start, inst.clone(g = (Tokens.branch, List(apoly)))))
            }
          case Tokens.`|` =>
            val result: List[Obj] = alst.g._2.map(b => Inst.resolveArg(start, b)).filter(_.alive)
            val apoly: Lst[Obj] = alst.clone(g = (alst.gsep, result))
            apoly match {
              case _: Value[_] => result.find(b => b.alive).map(x => x.hardQ(q => multQ(q, inst.q))).getOrElse(zeroObj)
              case _: Type[_] => BranchInstruction.brchType[Obj](apoly, inst.q).clone(via = (start, inst.clone(g = (Tokens.branch, List(apoly)))))
            }
        }
        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////
        case arec: Rec[Obj, Obj] => arec.gsep match {
          case Tokens.`,` =>
            val result: Seq[List[Obj]] = arec.gmap
              .map(kv => List(Inst.resolveArg(start, kv._1), kv._2))
              .filter(kv => kv.head.alive)
              .map(kv => List(kv.head, Inst.resolveArg(start, kv.tail.head))).toSeq
            arec.clone(g = (arec.gsep, result.map(x => x.head -> x.tail.head).toMap)) match {
              case _: Value[_] => strm(result.map(x => x.tail.head).map(x => x.hardQ(q => multQ(q, inst.q))))
              case apoly: Type[_] =>
                if (1 == result.size) result.head.tail.head.hardQ(q => multQ(q, inst.q))
                else BranchInstruction.brchType[Obj](apoly, inst.q).via(start, inst.clone(g = (Tokens.branch, List(apoly))))
            }
          case Tokens.`;` =>
            var running = start
            val result = arec.g._2.map(b => {
              running = running match {
                case astrm: Strm[_] => strm(astrm.values.map(r => if (Inst.resolveArg(r, b._1).alive) Inst.resolveArg(r, b._2) else zeroObj))
                case r if b._2.isInstanceOf[Value[_]] => if (Inst.resolveArg(r, b._1).alive) r `=>` b._2 else zeroObj
                case r: Type[_] if r.root && b._2.root && r.name == b._2.name => if (Inst.resolveArg(r, b._1).alive) r `=>` b._2 else zeroObj
                case _ => if (Inst.resolveArg(running, b._1).alive) Inst.resolveArg(running, b._2) else zeroObj
              }
              Inst.resolveArg(running, b._1) -> running
            }).filter(b => b._1.alive && b._2.alive).toMap
            if (result.isEmpty) zeroObj
            val apoly = arec.clone(g = (arec.gsep, result))
            apoly match {
              case _: Value[_] => apoly.g._2.last._2
              case _: Type[_] => BranchInstruction.brchType[Obj](apoly, inst.q).clone(via = (start, inst.clone(g = (Tokens.branch, List(apoly)))))
            }
          case Tokens.`|` =>
            val result: List[List[Obj]] = arec.g._2.map(b => {
              val key = Inst.resolveArg(start, b._1)
              List(key, (if (key.alive) Inst.resolveArg(start, b._2) else zeroObj))
            }).foldLeft(List.empty[List[Obj]])((a, b) => a :+ b)
            val apoly = arec.clone(g = (arec.gsep, result.map(x => x.head -> x.tail.head).toMap))
            apoly match {
              case _: Value[_] => result.find(b => b.head.alive).map(b => b.tail.head).getOrElse(zeroObj)
              case _: Type[_] =>
                if (result.size == 1)
                  result.head.tail.head.hardQ(q => multQ(q, inst.q))
                else
                  BranchInstruction.brchType[Obj](apoly, inst.q).clone(via = (start, inst.clone(g = (Tokens.branch, List(apoly)))))
            }
        }
      }
    }
  }
}
