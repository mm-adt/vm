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
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.value.Value
import org.mmadt.processor.{Processor, Traverser}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class CompilingProcessor(val model:Model = Model.id) extends Processor {
  override def apply[S <: Obj,E <: Obj](domainObj:S,rangeType:Type[E]):Iterator[Traverser[E]] ={
    assert(!domainObj.isInstanceOf[Value[Obj]],"The compiling processor only accepts types: " + domainObj)
    assert(!rangeType.isInstanceOf[__],"The compiling processor can not compile anonymous types: " + rangeType)
    model match {
      case Model.id => Iterator(new C1Traverser(domainObj).apply(rangeType))
      case _ =>
        val domainType       :E with Type[E] = domainObj.asInstanceOf[E with Type[E]]
        var mutatingTraverser:Traverser[E]   = new C1Traverser[E](obj = domainType,model = this.model)
        var previousTraverser:Traverser[E]   = new C1Traverser[E](obj = rangeType.asInstanceOf[E],model = this.model)
        while (previousTraverser != mutatingTraverser) {
          mutatingTraverser = previousTraverser
          previousTraverser = LeftRightSweepRewrite.rewrite(model,mutatingTraverser.obj().asInstanceOf[Type[E]],domainType.asInstanceOf[Type[E]],new C1Traverser(obj = domainType,model = this.model))
        }
        Iterator(new C2Traverser[E](domainObj.asInstanceOf[E],Map.empty,model).apply(mutatingTraverser.obj().asInstanceOf[Type[E]]))
    }
  }
}