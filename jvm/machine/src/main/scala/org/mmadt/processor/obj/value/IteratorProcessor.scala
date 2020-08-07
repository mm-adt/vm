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

package org.mmadt.processor.obj.value

import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.{FilterInstruction, ReduceInstruction}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.processor.Processor
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class IteratorProcessor extends Processor {
  override def apply[S <: Obj, E <: Obj](domainObj: S, rangeType: Type[E]): E = {
    var output: Iterator[E] = (domainObj match {
      case strm: Strm[E] => strm.values.iterator
      case single: E => Iterator(single)
    }).filter(_.alive)
    for (tt <- rangeType.trace) {
      output = tt._2 match {
        //////////////REDUCE//////////////
        case reducer: ReduceInstruction[E] => Iterator(reducer.exec(strm(output.toSeq)).asInstanceOf[E])
        //////////////FILTER//////////////
        case _: FilterInstruction => output.map(x => tt._2.exec(x).asInstanceOf[E]).filter(_.alive)
        //////////////OTHER//////////////
        case _ => output
          .map(x => tt._2.exec(x))
          .filter(_.alive)
          .flatMap(x => x match {
            case strm: Strm[E] => strm.values.filter(_.alive)
            case single: E => Iterator(single)
          })
      }
    }
    Processor.strmOrSingle(output.map(x => { //LanguageException.testTypeCheck(x.range,rangeType.range.hardQ(1)) // iterator processor linearizes the stream
      x
    }))
  }
}