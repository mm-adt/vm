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

package org.mmadt.processor.obj.`type`

import org.mmadt.language.model.{Model, SimpleModel}
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.Value
import org.mmadt.processor.{Processor, Traverser}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class CompilingProcessor[S <: Obj, E <: Obj](val model: Model = new SimpleModel) extends Processor[S, E] {
  private type EType = E with Type[_]

  override def apply(startObj: S, endType: E with Type[_]): Iterator[Traverser[E]] = {
    if (startObj.isInstanceOf[Value[_]]) throw new IllegalArgumentException("The compiling processor only accepts types: " + startObj)
    var mutatingType: EType = endType
    var mutatingTraverser: Traverser[E] = new C1Traverser[E](startObj.asInstanceOf[E])
    var bundle: (EType, Traverser[E]) = (endType, new C1Traverser(endType.asInstanceOf[E]))
    /////
    while (bundle._2 != mutatingTraverser) {
      mutatingType = bundle._1
      mutatingTraverser = bundle._2
      bundle = rewrite(bundle._2.obj().asInstanceOf[EType], endType.domain(), new C1Traverser(startObj.asInstanceOf[E]))
    }
    Iterator(bundle._2)
  }

  @scala.annotation.tailrec
  private def rewrite(atype: EType, btype: EType, traverser: Traverser[E]): (EType, Traverser[E]) = {
    if (atype.insts().nonEmpty) {
      model.get(atype) match {
        case Some(right: EType) => rewrite(right, btype, traverser)
        case None => rewrite(
          atype.rinvert(),
          atype.insts().last._2.apply(atype.range(), atype.insts().last._2.args()).asInstanceOf[EType].compose(btype),
          traverser)
      }
    } else if (btype.insts().nonEmpty) rewrite(btype.linvert(), btype.linvert().domain(), traverser.apply(btype))
    else (atype, traverser)
  }
}
