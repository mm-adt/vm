/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 *  mm-ADT is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU Affero General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 *  License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 *  You can be released from the requirements of the license by purchasing a
 *  commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language

import org.mmadt.machine.obj._
import org.mmadt.machine.obj.impl.obj._
import org.mmadt.machine.obj.theory.obj.`type`.Type
import org.mmadt.machine.obj.theory.obj.value.{RecValue, Value}
import org.mmadt.machine.obj.theory.obj.{Inst, Obj}
import org.mmadt.machine.obj.theory.traverser.Traverser

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object Stringer {

  def qString(x: TQ): String = x match {
    case `qOne` => ""
    case `qZero` => "{0}"
    case `qMark` => "{?}"
    case `qPlus` => "{+}"
    case `qStar` => "{*}"
    case (x, y) if (x == y) => "{" + x + "}"
    case (x, y) if (y == int(Long.MaxValue)) => "{" + x + ",}"
    case (x, y) if (x == int(Long.MinValue)) => "{," + y + "}"
    case _ => "{" + x._1.value() + "," + x._2.value() + "}"
  }

  def traverserString(trav: Traverser): String = {
    "[" + trav.obj() + "|" + trav.state().foldRight("")((x, string) => string + x._1 + "->" + x._2 + ",").dropRight(1) + "]"
  }

  def typeString(t: Type[_]): String = {
    val range = Tokens.symbol(t) + qString(t.q())
    val domain = if (t.insts().isEmpty) "" else
      Tokens.symbol(t.insts().head._1) + qString(t.insts().head._1.q())
    (if (domain.equals("") || range.equals(domain)) range else range + "<=" + domain) +
      t.insts().map(_._2.toString()).fold("")((a, b) => a + b)
  }

  def valueString(v: Value[_]): String = v match {
    case x: RecValue[_, _] => x.value().foldRight("[")((x, string) => string + x._1 + ":" + x._2 + ",").dropRight(1) + "]"
    case _ => v.value() + qString(v.q())
  }


  def instString(inst: Inst): String = {
    inst.args() match {
      case Nil => "[" + inst.op() + "]"
      case args: List[Obj] => "[" + inst.op() + "," + args.map(x => x.toString + ",").fold("")((a, b) => a + b).dropRight(1) + "]"
    }
  }
}
