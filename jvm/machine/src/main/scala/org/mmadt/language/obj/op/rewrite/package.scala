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

package org.mmadt.language.obj.op

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Obj.IntQ
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.storage.StorageFactory.qOne

import scala.annotation.tailrec

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
package object rewrite {

  def removeRules[A<:Obj](atype: A): A = {
    if (atype.isInstanceOf[Value[_]]) return atype
    atype.trace.map(x => x._2).foldLeft(atype.domainObj)((a, b) =>
      if (b.op.startsWith("rule:")) a
      else if (b.op == Tokens.model) b.exec(a)
      else b.clone(args => args.map(arg => removeRules(arg))).exec(a)).asInstanceOf[A]
  }

  @tailrec
  def exists(aobj: Obj, op: String): Boolean = {
    if (aobj.root) false
    else if (aobj.via._2.op == op) true
    else exists(aobj.via._1, op)
  }

  def downPropagateRule(inst: Inst[Obj, Obj], f: Obj => Obj): Inst[Obj, Obj] = {
    if (inst.op.equals(Tokens.model)) return inst
    inst.clone(args => args.map {
      case arg: Type[_] => f(arg)
      case arg => arg
    })
  }

  def backPropagateQ[A <: Obj](aobj: A, q: IntQ): A = {
    if (q == qOne) aobj
    else if (aobj.root || aobj.via._2.op == Tokens.model) aobj.id.q(q)
    else aobj.q(q)
  }

}
