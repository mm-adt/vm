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
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.RewriteInstruction
import org.mmadt.language.obj.op.map.IdOp
import org.mmadt.language.obj.op.trace.NoOp
import org.mmadt.language.obj.{Inst, Obj}
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
      case _: __ => start.via(start, inst)
      case atype: Type[_] =>
        if (!exists(atype, IdOp())) return atype
        val newAtype = atype.trace.map(x => x._2).map(x => if (x.hardQ(qOne) == IdOp()) NoOp().q(x.q).asInstanceOf[Inst[Obj, Obj]] else x).foldLeft(atype.domainObj)((a, b) => b.exec(a)).asInstanceOf[atype.type]
        if (newAtype.pureQ != atype.pureQ)
          if (newAtype.root) newAtype.id.q(atype.pureQ) else newAtype.q(atype.pureQ)
        else newAtype
      case _ => start
    }
  }

  @tailrec
  def exists(aobj: Obj, inst: Inst[Obj, Obj]): Boolean = {
    if (aobj.root) false
    else if (aobj.via._2.q(qOne) == inst) true
    else exists(aobj.via._1, inst)
  }
}
