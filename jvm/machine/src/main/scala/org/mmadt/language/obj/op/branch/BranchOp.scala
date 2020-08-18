/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */
package org.mmadt.language.obj.op.branch

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.BranchInstruction
import org.mmadt.language.obj.op.rewrite.IdRewrite
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory.{zeroObj, _}
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
        ////////////////////////////   LST  /////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////
        case alst: Lst[Obj] => alst.gsep match {
          case Tokens.`,` => Lst.moduleMult(start, alst) match {
            case blst if blst.isEmpty => zeroObj
            case blst: Value[_] => strm(blst.glist.map(x => x.hardQ(q => multQ(q, inst.q))))
            case blst: Type[_] =>
              if (1 == blst.size) IdRewrite().exec((start `=>` blst.glist.head).q(inst.q))
              else BranchInstruction.brchType[Obj](blst, inst.q).clone(via = (start, inst.clone(_ => List(blst))))
          }
          case Tokens.`;` => Lst.moduleMult(start, alst) match {
            case blst if blst.isEmpty => zeroObj
            case blst: Value[_] => blst.glist.last.hardQ(q => multQ(q, inst.q))
            case blst: Type[_] =>
              val result = blst.glist
              if (result.forall(x => Type.isIdentity(x))) {
                val finalQ = multQ(result.last.q, inst.q)
                if (result.last.isInstanceOf[Value[_]]) return result.last.hardQ(finalQ)
                var finalO = if (!result.last.root) result.last.hardQ(finalQ) else Type.unity(result.last).q(finalQ)
                finalO = if (Type.isIdentity(finalO) && finalO.domain.q == qOne && finalO.range.q == qOne) finalO.range else Type.unity(finalO).q(finalQ) // TODO: ghetto repeat.
                if (Type.isIdentity(finalO) && finalO.domain.q == qOne && finalO.range.q == qOne) finalO.range else Type.unity(finalO).q(finalQ)
              } else
                BranchInstruction.brchType[Obj](blst, inst.q).clone(via = (start, inst.clone(_ => List(blst))))
          }
          case Tokens.`|` => Lst.moduleMult(start, alst) match {
            case blst if blst.isEmpty => zeroObj
            case blst: Value[_] => blst.glist.head.hardQ(q => multQ(q, inst.q))
            case blst: Type[_] =>
              if (blst.size == 1) IdRewrite().exec((start `=>` blst.glist.head).q(inst.q))
              else BranchInstruction.brchType[Obj](blst, inst.q).clone(via = (start, inst.clone(_ => List(blst))))
          }
        }
        /////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////   REC  /////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////
        case arec: Rec[Obj, Obj] => arec.gsep match {
          case Tokens.`,` => Rec.moduleMult(start, arec) match {
            case brec: Value[_] => strm(brec.gmap.map(kv => kv._2.hardQ(q => multQ(q, inst.q))))
            case brec: Type[_] =>
              if (1 == brec.size) IdRewrite().exec((start `=>` brec.gmap.head._2).q(inst.q))
              else BranchInstruction.brchType[Obj](brec, inst.q).via(start, inst.clone(_ => List(brec)))
          }
          case Tokens.`;` => Rec.moduleMult(start, arec) match {
            case brec if brec.isEmpty => zeroObj
            case brec: Value[_] => brec.gmap.last._2.hardQ(q => multQ(q, inst.q))
            case brec: Type[_] => IdRewrite().exec(brec.gmap.last._2.q(inst.q)) // TODO: not generalized enough
          }
          case Tokens.`|` => Rec.moduleMult(start, arec) match {
            case brec if brec.isEmpty => zeroObj
            case brec: Value[_] => brec.gmap.head._2
            case brec: Type[_] =>
              if (brec.size == 1) IdRewrite().exec((start `=>` brec.gmap.head._2).q(inst.q))
              else BranchInstruction.brchType[Obj](brec, inst.q).clone(via = (start, inst.clone(_ => List(brec))))
          }
        }
      }
    }
  }
}
