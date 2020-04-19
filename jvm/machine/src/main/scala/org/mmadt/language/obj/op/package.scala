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
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
package object op {

  trait BarrierInstruction

  trait BranchInstruction

  object BranchInstruction {
    def typeInternal[IT <: Obj, OT <: Obj](start: OType[IT], branches: RecType[IT, OT]): RecType[IT, OT] = {
      trec(value = branches.value.map(x => (x._1 match {
        case atype: OType[IT] => start.compute(atype)
        case avalue: OValue[IT] => avalue
      }, x._2 match {
        case btype: Type[OT] with OT => start.compute(btype)
        case bvalue: OValue[OT] => bvalue
      })))
    }

    def typeExternal[OT <: Obj](parallel: Boolean, branches: RecType[_, OT]): OT = {
      val types = branches.value.values.map {
        case atype: Type[OT] => atype.hardQ(1).range
        case avalue: Value[OT] => asType(avalue)
      }.filter(x => x.alive()).asInstanceOf[Iterable[OType[OT]]]

      val result:OType[OT] = types.toSet.size match {
        case 1 => types.head
        case _ => new TObj().asInstanceOf[OType[OT]] // if types are distinct, generalize to obj
      }
      if (parallel) { // [branch] sum the min/max quantification
        result.hardQ(minZero(branches.value.values.map(x => x.q).reduce((a, b) => plusQ(a,b))))
      }
      else { // [choose] select min/max quantification
        result.hardQ(branches.value.values.map(x => x.q).reduce((a, b) => (
          int(Math.min(a._1.value, b._1.value)),
          int(Math.max(a._2.value, b._2.value)))))
      }
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
