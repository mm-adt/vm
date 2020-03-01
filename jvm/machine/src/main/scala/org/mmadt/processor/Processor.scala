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
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.Type
import org.mmadt.processor.obj.`type`.CompilingProcessor
import org.mmadt.processor.obj.value.IteratorProcessor

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Processor {
  def apply[S <: Obj,E <: Obj](domainObj:S,rangeType:Type[E]):Iterator[Traverser[E]] // TODO: this should be a Strm[E] or just E (with Strm inspected)
  def apply[S <: Obj,E <: Obj](rangeType:Type[E]):E = this.apply(rangeType.domain[S](),rangeType).next().obj()
}

object Processor {
  def compiler(model:Model = Model.id):Processor = new CompilingProcessor(model)
  def iterator(model:Model = Model.id):Processor = new IteratorProcessor()
}
