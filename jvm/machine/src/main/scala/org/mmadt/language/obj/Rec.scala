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

import org.mmadt.language.obj.op.map.{GetOp, PlusOp, ZeroOp}
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Rec[A <: Obj, B <: Obj] extends Poly[B]
  with PlusOp[Rec[A, B]]
  with GetOp[A, B]
  with PutOp[A, B]
  with ZeroOp[Rec[A, B]] {

  def g: RecTuple[A, B]
  def gmap: collection.Map[A, B] = g._2
  def glist: Seq[B] = gmap.values.toSeq
  def gsep: String = g._1
  def clone(values: collection.Map[A, B]): this.type = this.clone(ground = (gsep, values))
}
object Rec {
  def resolveSlots[A <: Obj, B <: Obj](start: A, arec: Rec[A, B], inst: Inst[A, Rec[A, B]]): Rec[A, B] = {
    val arg = start match {
      case _: Value[_] => start.clone(via = (start, inst))
      case _ => start
    }
    arec.clone(arec.gmap.toSeq.map(slot => {
      val key = Inst.resolveArg(arg, slot._1)
      (key, if (key.alive) Inst.resolveArg(arg, slot._2) else zeroObj.asInstanceOf[B])
    }).foldLeft(Map.empty[A, B])((a, b) => a + (b._1 -> strm[B](List(b._2) ++ a.getOrElse(b._1, strm[B]).toStrm.values))))
  }
  def keepFirst[A <: Obj, B <: Obj](arec: Rec[A, B]): Rec[A, B] = {
    val first: (A, B) = arec.gmap.find(x => x._1.alive).getOrElse((zeroObj.asInstanceOf[A], zeroObj.asInstanceOf[B]))
    arec.clone(arec.gmap.map(a => if (a == first) a else (zeroObj.asInstanceOf[A], zeroObj.asInstanceOf[B])).toMap[A, B])
  }
}
