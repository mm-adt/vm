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

import org.mmadt.language.model.Model
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.Type
import org.mmadt.processor.Traverser

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class TypeFunctorTraverser[S <: Obj](val obj:S,val state:State,val model:Model = Model.id) extends Traverser[S] {
  def this(obj:S) = this(obj,Map.empty)

  override def split[E <: Obj](obj:E,state:State = this.state):Traverser[E] =
    new TypeFunctorTraverser[E](model.resolve(obj),state,this.model)
  override def apply[E <: Obj](rangeType:Type[E]):Traverser[E] ={
    val next:Traverser[E] = model.get(obj.asInstanceOf[Type[Obj]].domain()) match {
      case Some(atype) => this.split[E](atype.asInstanceOf[E].q(obj.q))
      case None => this.asInstanceOf[Traverser[E]]
    }
    (Type.nextInst(rangeType) match {
      case None =>
        assert(rangeType.domain() == rangeType.range)
        return next
      case Some(inst) => inst.apply(next).asInstanceOf[Traverser[E]]
    }).apply(rangeType.linvert())
  }
}
