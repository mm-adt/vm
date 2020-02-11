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

package org.mmadt.processor.obj.value

import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.strm.IntStrm
import org.mmadt.language.obj.{Inst, Obj}
import org.mmadt.processor.{Processor, Traverser}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class IteratorChainProcessor[S <: Obj, E <: Obj] extends Processor[S, E] {

  override def apply(startObj: S, endType: E with Type[_]): Iterator[Traverser[E]] = {
    var output: Iterator[Traverser[E]] = startObj match {
      case s: IntStrm => s.value().map(x => new SimpleTraverser[E](x.asInstanceOf[E]))
      case r => Iterator(new SimpleTraverser[E](r.asInstanceOf[E]))
    }
    for (tt <- createInstList(List(), endType)) {
      // System.out.println(tt)
      output = output.
        map(_.apply(tt._1.push(tt._1, tt._2)).asInstanceOf[Traverser[E]]).
        filter(_.obj().alive())
    }
    output
  }

  @scala.annotation.tailrec
  private def createInstList(list: List[(Type[_], Inst)], t: Type[_]): List[(Type[_], Inst)] = {
    if (t.insts().isEmpty) list else createInstList(List((t.pure(), t.insts().last._2)) ++ list, t.insts().last._1)
  }
}
