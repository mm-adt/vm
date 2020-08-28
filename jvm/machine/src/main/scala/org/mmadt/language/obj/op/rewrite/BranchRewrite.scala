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

package org.mmadt.language.obj.op.rewrite

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.Rec.Pairs
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.branch.BranchOp
import org.mmadt.language.obj.op.trace.ModelOp
import org.mmadt.language.obj.op.{BranchInstruction, RewriteInstruction}
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object BranchRewrite extends Func[Obj, Obj] {
  def apply(): Inst[Obj, Obj] = new VInst[Obj, Obj](g = (Tokens.rule_unity, Nil), func = this) with RewriteInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    start match {
      case _: __ if !__.isToken(start) => start.via(start, inst)
      case atype: Type[_] => processType(atype)
      case _ => start
    }
  }
  def processType[A <: Obj](atype: A): A = {
    if (atype.isInstanceOf[Value[_]] || __.isAnon(atype) || __.isToken(atype) || !atype.via.exists(x => x._2.op.equals(Tokens.branch))) return atype
    atype.trace.map(x => x._2).foldLeft(atype.domainObj)((a, b) => {
      if (b.op == Tokens.branch) {
        if (b.arg0[Obj].isInstanceOf[Poly[Obj]] && b.arg0[Poly[Obj]].gsep == Tokens.`;`) {
          val apoly: Poly[Obj] = b.arg0[Poly[Obj]] match {
            case alst: Lst[Obj] => if (alst.glist.exists(x => !x.alive)) alst.rangeObj.q(qZero) else alst
            case arec: Rec[Obj, Obj] => if (arec.gmap.exists(x => !x._1.alive || !x._2.alive)) arec.rangeObj.q(qZero) else arec
          }
          if (!apoly.alive) return zeroObj.asInstanceOf[A]
          val start = if (__.isAnonRoot(a)) apoly.glist.head.domainObj else a
          val branches: List[Obj] = apoly.glist
            .map(x => singleOrPair(x, s => IdRewrite.processType(s)))
            .map(x => singleOrPair(x, s => processObj(s)))
            .map(x => singleOrPair(x, s => removeRules(s)))
            .filter(x => !x.trace.forall(x => ModelOp.isMetaModel(x._2))).toList
          val end: Obj = {
            if (branches.isEmpty) start
            else if (branches.size == 1) Some(branches.head)
              .map(z => singleOrPair(z, y => removeRules(y)))
              .map(z => singleOrPair(z, z => z.trace.reconstruct[A](start))).get
            else if (singleOrPair[Poly[Obj], Boolean](apoly, x => x.glist.forall(z => Type.isIdentity(z)))) BranchInstruction.brchType[Obj](apoly)
            else BranchInstruction.brchType[Obj](apoly).clone(via = (start, BranchOp(apoly match {
              case arec: Rec[Obj, Obj] => arec.clone(_ => branches.asInstanceOf[Pairs[Obj, Obj]])
              case alst: Lst[Obj] => alst.clone(_ => branches)
            })))
          }
          backPropagateQ(IdRewrite.processType(end), b.q)
        } else b.exec(a)
      } else
        b.exec(a)
    }).asInstanceOf[A]
  }

  def processObj[A <: Obj](obj: A): A = {
    if (obj.isInstanceOf[Value[_]]) return obj
    obj.trace.map(x => x._2).map(x => downPropagateRule(x, y => BranchRewrite.processType(y))).foldLeft(obj.domainObj)((a, b) => b.exec(a)).asInstanceOf[A]
  }
}
