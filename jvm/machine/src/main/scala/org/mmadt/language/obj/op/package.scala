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

package org.mmadt.language.obj

import org.mmadt.language.obj.`type`.{RecType, Type}
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory.{int, trec}
import org.mmadt.storage.obj.`type`.TObj

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
package object op {

  trait BarrierInstruction

  trait BranchInstruction

  object BranchInstruction {
    def applyRec[IT <: Obj, OT <: Obj](current: Type[IT] with IT, branches: RecType[IT, OT]): RecType[IT, OT] = {
      trec(value = branches.value().map(x => (x._1 match {
        case atype: Type[IT] with IT => current.compose(atype).asInstanceOf[IT]
        case avalue: Value[IT] with IT => avalue
      }, x._2 match {
        case atype: Type[OT] with OT => current.compose(atype).asInstanceOf[OT]
        case avalue: Value[OT] with OT => avalue
      })))
    }

    def generalType[OT <: Obj](outs: Iterable[OT]): OT = { // TODO: record introspection for type generalization
      val types = outs.map {
        case atype: Type[Obj] => atype.range.asInstanceOf[OT]
        case avalue: OT => avalue
      }.filter(x => x.alive())

      (types.toSet.size match {
        case 1 => types.head
        case _ => new TObj().asInstanceOf[OT]
      }).q(types.map(x => x.q).reduce((a, b) => (
        int(Math.min(a._1.value, b._1.value)),
        int(Math.max(a._2.value, b._2.value))))) // the quantification is the largest span of the all the branch ranges
    }
  }

  trait FilterInstruction {
    def keep(obj: Obj): Boolean = !(obj.q._1.value == 0 && obj.q._2.value == 0)
  }

  trait FlatmapInstruction

  trait InitialInstruction

  trait MapInstruction

  trait QuantifierInstruction

  trait ReduceInstruction[O <: Obj] {
    val seed: (String, O)
    val reduction: Type[O]
  }

  trait SideEffectInstruction

  trait TerminalInstruction

  trait TraverserInstruction

}
