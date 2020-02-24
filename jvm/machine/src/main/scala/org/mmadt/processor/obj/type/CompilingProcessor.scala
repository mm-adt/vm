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
import org.mmadt.language.obj.{OValue, Obj, TType}
import org.mmadt.processor.{Processor, Traverser}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class CompilingProcessor[S <: Obj,E <: Obj](val model:Model = Model.id) extends Processor[S,E] {
  override def apply(domainObj:S,rangeType:TType[E]):Iterator[Traverser[E]] ={
    // C1Traverser applies rewrite rules until a steady state is reached
    val traverser:Traverser[E] = domainObj match {
      case domainValue:OValue => throw new IllegalArgumentException("The compiling processor only accepts types: " + domainValue)
      case domainType:TType[E] => LeftRightSweepRewrite.rewrite[E](model,domainType,rangeType)
    }

    // C2Traverser performs type erasure, representing all types in terms of mm-ADT
    Iterator(model match {
      case Model.id => traverser
      case _ => new C2Traverser[E](domainObj.asInstanceOf[E],Map.empty,model).apply(traverser.obj().asInstanceOf[TType[E]])
    })
  }
}