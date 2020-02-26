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
import org.mmadt.language.model.rewrite.LeftRightSweepRewrite
import org.mmadt.language.obj.{OType, OValue, Obj, TType}
import org.mmadt.processor.{Processor, Traverser}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class CompilingProcessor[S <: Obj,E <: Obj](val model:Model = Model.id) extends Processor[S,E] {
  override def apply(domainObj:S,rangeType:TType[E]):Iterator[Traverser[E]] ={
    assert(!domainObj.isInstanceOf[OValue],"The compiling processor only accepts types: " + domainObj)

    // C1Traverser applies rewrite rules until a steady state is reached
    val domainType       :TType[E]     = domainObj.asInstanceOf[TType[E]]
    var mutatingTraverser:Traverser[E] = new C1Traverser[E](domainType)
    var previousTraverser:Traverser[E] = new C1Traverser[E](rangeType)
    while (previousTraverser != mutatingTraverser) {
      mutatingTraverser = previousTraverser
      previousTraverser = LeftRightSweepRewrite.rewrite(model,mutatingTraverser.obj().asInstanceOf[OType],domainType,new C1Traverser(domainType))
    }

    // C2Traverser performs type erasure, representing all types in terms of mm-ADT
    Iterator(model match {
      case Model.id => mutatingTraverser
      case _ => new C2Traverser[E](domainObj.asInstanceOf[E],Map.empty,model).apply(mutatingTraverser.obj().asInstanceOf[TType[E]])
    })
  }
}