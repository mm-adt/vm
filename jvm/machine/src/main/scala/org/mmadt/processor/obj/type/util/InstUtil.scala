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

package org.mmadt.processor.obj.`type`.util

import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.processor.Traverser

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object InstUtil {

  def nextInst(insts: List[(Type[_], Inst)]): Option[Inst] = if (insts == Nil) None else Some(insts.head._2)

  /**
   * Before an instruction is applied, its arguments are computing by a split of the incoming traverser
   */
  def valueInst[S <: Obj, E <: Obj](traverser: Traverser[S], inst: Inst): Inst = {
    traverser.obj().inst(inst.op(), inst.args().map {
      case typeArg: Type[_] => traverser.split(traverser.obj() match {
        case tt: Type[_] => tt.pure()
        case vv: Value[_] => vv
      }).apply(typeArg).obj()
      case valueArg: Value[_] => valueArg
    })
  }
}
