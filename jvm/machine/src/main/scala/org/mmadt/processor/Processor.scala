/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.processor

import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.Type
import org.mmadt.processor.obj.`type`.CompilingProcessor
import org.mmadt.processor.obj.value.IteratorProcessor
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Processor {
  def apply[S <: Obj, E <: Obj](domainObj: S, rangeType: Type[E]): E
  def apply[S <: Obj, E <: Obj](rangeType: Type[E]): Type[E] = this.apply(rangeType.domain, rangeType).asInstanceOf[Type[E]]
}

object Processor {
  def compiler: Processor = new CompilingProcessor()
  def iterator: Processor = new IteratorProcessor()

  def strmOrSingle[O <: Obj](itty: Iterator[O]): O = {
    if (itty.hasNext) {
      val first = itty.next()
      first match {
        case _ if itty.hasNext => strm[O](first +: itty.toList)
        case _ => first
      }
    } else
      zeroObj.asInstanceOf[O]
  }
}
