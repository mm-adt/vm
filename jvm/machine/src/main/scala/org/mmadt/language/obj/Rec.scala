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

import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.strm.util.MultiSet

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Rec[A <: Obj, B <: Obj] extends Poly[B]
  with Type[Rec[A, B]]
  with Value[Rec[A, B]]
  with PlusOp[Rec[A, B]]
  with GetOp[A, B]
  with PutOp[A, B]
  with HeadOp[B]
  with TailOp
  with LastOp[B]
  with ZeroOp[Rec[A, B]] {

  def g: RecTuple[A, B]
  def gmap: collection.Map[A, B] = g._2
  def glist: Seq[B] = gmap.values.toSeq
  def gsep: String = g._1
  def clone(values: collection.Map[A, B]): this.type = this.clone(g = (gsep, values))
  override def test(other: Obj): Boolean = other match {
    case aobj: Obj if !aobj.alive => !this.alive
    case astrm: Strm[_] => MultiSet.test(this, astrm)
    case arec: Rec[_, _] => Poly.sameSep(this, arec) &&
      withinQ(this, arec) && arec.gmap.count(x => qStar.equals(x._2.q) || this.gmap.exists(y => y._1.test(x._1) && y._2.test(x._2))) == arec.gmap.size
    case _ => false
  }

  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.g.hashCode()
  override def equals(other: Any): Boolean = other match {
    case astrm: Strm[_] => MultiSet.test(this, astrm)
    case arec: Rec[_, _] => Poly.sameSep(this, arec) &&
      arec.name.equals(this.name) &&
      eqQ(arec, this) &&
      this.gmap.size == arec.gmap.size &&
      this.gmap.zip(arec.gmap).foldRight(true)((a, b) => a._1._1.equals(a._2._1) && a._1._2.equals(a._2._2) && b)
    case _ => false
  }
}
object Rec {
  def resolveSlots[A <: Obj, B <: Obj](start: A, arec: Rec[A, B], inst: Inst[A, Rec[A, B]]): Rec[A, B] = {
    val arg = start match {
      case _: Value[_] => start.clone(via = (start, inst))
      case _ => start
    }
    if (arec.isSerial) {
      var local = arg -> arg
      arec.clone(g = (arec.gsep, arec.gmap.map(slot => {
        val key = Inst.resolveArg(local._1, slot._1)
        local = if (!key.alive) (key -> zeroObj.asInstanceOf[A]) else local._2 match {
          case astrm: Strm[_] => key -> strm(astrm.values.map(x => Inst.resolveArg(x, slot._2))).asInstanceOf[A]
          case _ => (key -> Inst.resolveArg(local._2, slot._2)).asInstanceOf[(A, A)]
        }
        local
      })))
    } else {
      arec.clone(g = (arec.gsep, arec.gmap.toSeq.map(slot => {
        val key = Inst.resolveArg(arg, slot._1)
        (key, if (key.alive) Inst.resolveArg(arg, slot._2) else zeroObj.asInstanceOf[B])
      }).foldLeft(Map.empty[A, B])((a, b) => a + (b._1 -> (if (b._2.isInstanceOf[Type[Obj]]) b._2 else strm[B](List(b._2) ++ a.getOrElse(b._1, strm[B]).toStrm.values))))))
    }
  }
  def keepFirst[A <: Obj, B <: Obj](start: Obj, inst: Inst[Obj, Obj], arec: Rec[A, B]): Rec[A, B] = {
    var found: Boolean = false;
    arec.clone(g = (arec.gsep, arec.gmap.map(x => {
      if (!found) {
        val keyResolve = Inst.resolveArg(start, x._1)
        if (keyResolve.alive) {
          found = true
          (keyResolve, Inst.resolveArg(start, x._2))
        } else (zeroObj, zeroObj)
      } else
        (zeroObj, zeroObj)
    })))
  }
}
