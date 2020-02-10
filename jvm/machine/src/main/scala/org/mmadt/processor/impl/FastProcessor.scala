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

package org.mmadt.processor.impl

import org.mmadt.machine.obj.impl.traverser.RecursiveTraverser
import org.mmadt.machine.obj.theory.obj.`type`.Type
import org.mmadt.machine.obj.theory.obj.value.strm.IntStrm
import org.mmadt.machine.obj.theory.obj.{Inst, Obj}
import org.mmadt.machine.obj.theory.traverser.Traverser
import org.mmadt.processor.Processor

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class FastProcessor extends Processor {

  override def apply(o: Obj, t: Type[_]): Iterator[Traverser] = {
    var output: Iterator[Traverser] = o match {
      case s: IntStrm => s.value().map(x => new RecursiveTraverser(x))
      case r => Iterator(new RecursiveTraverser(r))
    }
    for (tt <- createInstList(List(), t)) {
      // System.out.println(tt)
      output = output.
        map(trav => trav.apply(tt._1.push(tt._1, tt._2))).
        filter(trav => trav.obj[Obj]().alive())
    }
    output
  }

  private def createInstList(list: List[(Type[_], Inst)], t: Type[_]): List[(Type[_], Inst)] = {
    if (t.insts().isEmpty) list else createInstList(List((t.pure(), t.insts().last._2)) ++ list, t.insts().last._1)
  }
}
