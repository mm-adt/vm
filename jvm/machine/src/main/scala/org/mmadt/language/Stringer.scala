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
import org.mmadt.machine.obj.theory.obj.`type`.Type
import org.mmadt.machine.obj.theory.obj.value.Value
import org.mmadt.machine.obj.theory.obj.{Inst, Obj}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object Stringer {

  def q(x: TQ): String = x match {
    case `qOne` => ""
    case `qZero` => "{0}"
    case `qMark` => "{?}"
    case _ => "{" + x._1.value() + "," + x._2.value() + "}"
  }

  // int.plus(int.plus(34)).mult(4).is(int.plus(4).gt(20)).gt(45).or(boolT)
  def typeString(t: Type[_]): String = {
    var domain = ""
    val range = Tokens.symbol(t)
    var insts = List[Inst]()
    var x = t
    while (x.inst() != null) {
      insts = insts.::(x.inst())
      if (null == x.domain().inst())
        domain = Tokens.symbol(x)
      x = x.domain()
    }
    if (domain.equals("")) range else
    // else if (range.equals(domain)) range + insts.map(i => "[" + i.op() + "," + instArgs(i.value()._2) + "]").fold("")((a, b) => a + b) else
      range + "<=" + domain + insts.map(i => "[" + i.op() + "," + instArgs(i.value()._2) + "]").fold("")((a, b) => a + b)
  }

  def valueString(v: Value[_]): String = v.value().toString

  def instArgs(args: List[Obj]): String = {
    args.map(x => x.toString + ",").fold("")((a, b) => a + b).dropRight(1)
  }
}
