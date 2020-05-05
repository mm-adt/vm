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

import org.mmadt.language.LanguageException
import org.mmadt.language.model.Model
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.{FilterInstruction, ReduceInstruction}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.processor.Processor
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class IteratorProcessor(model: Model = Model.id) extends Processor {
  override def apply[S <: Obj, E <: Obj](domainObj: S, rangeType: Type[E]): E = {
    LanguageException.testTypeCheck(domainObj.named(rangeType.domain[Obj]().name), rangeType.domain().q(0, rangeType.domain().q._2)) // TODO: don't rename obj (so lame)

    var output: Iterator[E] = this.model(domainObj) match {
      case strm: Strm[_] => strm.values.map(x => x.asInstanceOf[E]).iterator
      case single: E => Iterator(single)
    }

    for (tt <- IteratorProcessor.createInstList(Nil, rangeType)) {
      output = tt._2 match {
        //////////////REDUCE//////////////
        case reducer: ReduceInstruction[E] => Iterator(output.foldRight(reducer.seed._2)((e, mutatingSeed) => e.compute(reducer.reduction))).map(e => e.q(qOne)) // TODO: need a new seed method other than Traverser
        //////////////FILTER//////////////
        case filter: FilterInstruction => output.map(_.compute(tt._1.via(tt._1, tt._2)).asInstanceOf[E]).filter(x => filter.keep(x))
        //////////////OTHER//////////////
        case _: Inst[Obj, Obj] => output
          .map(_.compute(tt._1.via(tt._1, tt._2)))
          .filter(_.alive)
          .flatMap(x => x match {
            case strm: Strm[E] => strm.values.map(x => x)
            case single: E => Iterator(single)
          })
      }
    }

    Processor.strmOrSingle(output.map(x => {
      // LanguageException.testTypeCheck(x,if (rangeType.range.alive()) rangeType.range.q(1,rangeType.range.q._2) else rangeType.range) // iterator processor linearizes the stream
      x
    }))
  }
}

object IteratorProcessor {
  @scala.annotation.tailrec
  private def createInstList(list: List[(Type[Obj], Inst[Obj, Type[Obj]])], atype: Type[Obj]): List[(Type[Obj], Inst[Obj, Type[Obj]])] = {
    if (atype.root) list else createInstList(List((atype.range, atype.trace.last._2.asInstanceOf[Inst[Obj, Type[Obj]]])) ::: list, atype.trace.last._1.asInstanceOf[Type[Obj]])
  }
}
