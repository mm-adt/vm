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
import org.mmadt.storage.StorageFactory.{int, trec, _}
import org.mmadt.storage.obj.`type`.TObj

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
package object op {

  trait BarrierInstruction

  trait BranchInstruction

  object BranchInstruction {
    def typeInternal[IT <: Obj, OT <: Obj](start: OType[IT], branches: RecType[IT, OT]): RecType[IT, OT] = {
      trec(ground = branches.ground.map(x => (x._1 match {
        case atype: OType[IT] => start.compute(atype)
        case avalue: OValue[IT] => avalue
      }, x._2 match {
        case btype: Type[OT] with OT => start.compute(btype)
        case bvalue: OValue[OT] => bvalue
      })))
    }

    def typeExternal[OT <: Obj](parallel: Boolean, branches: RecType[_, OT]): OT = {
      val types = branches.ground.values.filter(_.alive).map {
        case atype: Type[OT] => atype.hardQ(1).range
        case avalue: Value[OT] => asType(avalue)
      }.asInstanceOf[Iterable[OType[OT]]]

      val result: OType[OT] = types.toSet.size match {
        case 1 => types.head
        case _ => new TObj().asInstanceOf[OType[OT]] // if types are distinct, generalize to obj
      }
      if (parallel) { // [branch] sum the min/max quantification
        result.hardQ(minZero(branches.ground.values.map(x => x.q).reduce((a, b) => plusQ(a, b))))
      }
      else { // [choose] select min/max quantification
        result.hardQ(branches.ground.values.filter(_.alive).map(x => x.q).reduce((a, b) => (
          int(Math.min(a._1.ground, b._1.ground)),
          int(Math.max(a._2.ground, b._2.ground)))))
      }
    }

    def brchType[OT <: Obj](brch: Poly[_ <: Obj]): OT = {
      val types = brch.ground._2.filter(_.alive).map {
        case atype: Type[OT] => atype.hardQ(1).range
        case avalue: Value[OT] => asType(avalue)
      }.asInstanceOf[Iterable[OType[OT]]]

      val result: OType[OT] = types.toSet.size match {
        case 1 => types.head
        case _ => new TObj().asInstanceOf[OType[OT]] // if types are distinct, generalize to obj
      }
      if (brch.ground._1 == "|") { // [branch] sum the min/max quantification
        result.hardQ(minZero(brch.ground._2.map(x => x.q).reduce((a, b) => plusQ(a, b))))
      }
      else { // [choose] select min/max quantification
        result.hardQ(brch.ground._2.filter(_.alive).map(x => x.q).reduce((a, b) => (
          int(Math.min(a._1.ground, b._1.ground)),
          int(Math.max(a._2.ground, b._2.ground)))))
      }
    }
  }

  trait FilterInstruction {
    def keep(obj: Obj): Boolean = !(obj.q._1.ground == 0 && obj.q._2.ground == 0)
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
