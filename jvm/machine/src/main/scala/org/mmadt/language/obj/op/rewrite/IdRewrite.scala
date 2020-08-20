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
import org.mmadt.language.obj.Obj.IntQ
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.RewriteInstruction
import org.mmadt.language.obj.{Inst, Obj, multQ}
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
      case _: __ if !__.isToken(start) => start.via(start, inst) // if (!start.root && start.via._2.op == Tokens.rule_id) start else start.via(start, inst)
      case atype: Type[_] => backPropagateQ(stripId(atype), inst.q)
      case _ => start
    }
  }

  def stripId[A <: Obj](atype: A): A = {
    if (__.isToken(atype) || !exists(atype, Tokens.id)) atype
    else {
      var rollingQ: IntQ = qOne
      backPropagateQ(atype.trace.map(x => x._2).foldLeft(atype.domainObj)((a, b) => {
        if (b.op == Tokens.id || b.op == Tokens.rule_id) {
          rollingQ = multQ(rollingQ, b.q)
          a
        } else
          downPropagateRule(b).exec(a)
      }), rollingQ).asInstanceOf[atype.type]
    }
  }

  def backPropagateQ[A <: Obj](aobj: A, q: IntQ): A = {
    if (q == qOne) aobj
    else if (aobj.root || aobj.via._2.op == Tokens.model) aobj.id.q(q)
    else aobj.q(q)
  }

  def downPropagateRule(inst: Inst[Obj, Obj]): Inst[Obj, Obj] = {
    if (inst.op.equals(Tokens.model)) return inst
    inst.clone(args => args.map {
      case arg: Type[_] => IdRewrite.stripId(arg)
      case arg => arg
    })
  }

  @tailrec
  def exists(aobj: Obj, op: String): Boolean = {
    if (aobj.root) false
    else if (aobj.via._2.op == op) true
    else exists(aobj.via._1, op)
  }
}
