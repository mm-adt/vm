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

import org.mmadt.language.model.Model
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{Type, TypeChecker}
import org.mmadt.language.obj.op.{FilterInstruction, ReduceInstruction}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.processor.{Processor, Traverser}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class IteratorProcessor(model:Model = Model.id) extends Processor {
  override def apply[S <: Obj,E <: Obj](domainObj:S,rangeType:Type[E]):E ={
    /*val domainObjAs:S = domainObj match {
      case atype:Type[S] with S => atype
      case avalue:Value[S] with S => model[S,S](rangeType.domain[S]())(avalue)
    }*/
    TypeChecker.typeCheck(domainObj,rangeType.domain())
    var lastStrm:Option[Strm[_]]        = None
    var output  :Iterator[Traverser[E]] = domainObj match {
      case strm:Strm[_] =>
        lastStrm = Option(strm)
        strm.value.map(x => Traverser.standard(x.asInstanceOf[E]))
      case single:E => Iterator(Traverser.standard(single))
    }
    for (tt <- Type.createInstList(Nil,rangeType)) {
      output = tt._2 match {
        //////////////REDUCE//////////////
        case reducer:ReduceInstruction[E] => Iterator(output.foldRight(reducer.seed._2)(
          (traverser,mutatingSeed) => Traverser.stateSplit(reducer.seed._1,mutatingSeed)(traverser).apply(reducer.reduction).obj())).map(e => Traverser.standard(e.q(qOne)))
        //////////////FILTER//////////////
        case filter:FilterInstruction => output.map(_.apply(tt._1.compose(tt._1,tt._2)).asInstanceOf[Traverser[E]]).filter(x => filter.keep(x.obj()))
        //////////////OTHER//////////////
        case _:Inst[Obj,Obj] => output
          .map(_.apply(tt._1.compose(tt._1,tt._2)))
          .flatMap(x => x.obj() match {
            case strm:Strm[E] =>
              strm.value.map(y => x.split(y))
            case single:E => Iterator(x.split(single))
          })
      }
    }
    Processor.strmOrSingle(output.map(x => {
      TypeChecker.typeCheck(x.obj(),if (rangeType.range.alive()) rangeType.range.q(1,rangeType.range.q._2) else rangeType.range) // iterator processor linearizes the stream
      x.obj()
    }))
  }
}
