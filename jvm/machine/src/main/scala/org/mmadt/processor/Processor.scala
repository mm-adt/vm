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

package org.mmadt.processor

import org.mmadt.language.model.Model
import org.mmadt.language.obj.{Obj, TType}
import org.mmadt.processor.obj.`type`.CompilingProcessor
import org.mmadt.processor.obj.value.IteratorProcessor

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Processor[S <: Obj,E <: Obj] {
  def apply(domainObj:S,rangeType:TType[E]):Iterator[Traverser[E]]
  def apply(rangeType:TType[E]):E = this.apply(rangeType.domain(),rangeType).next().obj()
}

object Processor {
  def compiler[S <: Obj,E <: Obj](model:Model = Model.id):Processor[S,E] = new CompilingProcessor[S,E](model)
  def iterator[S <: Obj,E <: Obj](model:Model = Model.id):Processor[S,E] = new IteratorProcessor[S,E]()
  // def recursive[S<:Obj,E<:Obj](model:Model = Model.id):Processor[S,E] = new RecursiveTraverser[E]()
}
