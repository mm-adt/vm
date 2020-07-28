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

package org.mmadt.language.obj

import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory.{int, _}
import org.mmadt.storage.obj.`type`.TObj

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
package object op {

  trait BarrierInstruction

  trait BranchInstruction

  object BranchInstruction {
    def brchType[OT <: Obj](brch: Poly[_ <: Obj]): OT = {
      val types = brch.glist.filter(_.alive).map {
        case atype: Type[OT] => atype.hardQ(1).range
        case avalue: Value[OT] => asType(avalue)
      }.asInstanceOf[Iterable[OType[OT]]]
      val result: OType[OT] = types.toSet.size match {
        case 1 => types.head
        case _ => new TObj().asInstanceOf[OType[OT]] // if types are distinct, generalize to obj
      }
      val x = if (brch.isParallel) { // [,] sum the min/max quantification
        result.hardQ(brch.glist.map(x => x.q).foldLeft(qZero)((a, b) => plusQ(a, b)))
      } else if (brch.isSerial) { // [;] last quantification
        brch match {
          case alst: Lst[Obj] => asType[OT](alst.glist.foldLeft(Option(brch.via._1).getOrElse(brch.glist.head.domain))((a, b) => a.compute(b)).asInstanceOf[OT])
          case arec: Rec[Obj, Obj] => asType[OT](arec.glist.lastOption.getOrElse(zeroObj).asInstanceOf[OT])
          //asType(arec.gmap.foldLeft(Option(brch.via._1).getOrElse(__))((a, b) => if (a.test(b._1)) a.compute(b._2) else zeroObj)).asInstanceOf[OT]
        }
      } else { // [|] min/max quantification
        result.hardQ(brch.glist.filter(_.alive).map(x => x.q).reduceLeftOption((a, b) => (
          int(Math.min(a._1.g, b._1.g)),
          int(Math.max(a._2.g, b._2.g)))).getOrElse(qZero))
      }
      x.hardQ(multQ(brch.q, x.q))
    }

    def multPolyQ(obj: Obj, poly: Poly[_], inst: Inst[_, _]): Obj = obj.hardQ(multQ(multQ(obj.q, poly.q), inst.q))
  }

  trait FilterInstruction

  trait FlatmapInstruction

  trait InitialInstruction

  trait MapInstruction

  trait QuantifierInstruction

  trait ReduceInstruction[O <: Obj] {
    val seed: O
    val reducer: O
  }

  trait SideEffectInstruction

  trait TerminalInstruction

  trait TraceInstruction

}
