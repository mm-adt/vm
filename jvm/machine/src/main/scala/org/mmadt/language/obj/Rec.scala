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

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.strm.util.MultiSet

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Rec[A <: Obj, B <: Obj] extends Poly[B]
  with PlusOp[Rec[A, B]]
  with GetOp[A, B]
  with PutOp[A, B]
  with ZeroOp[Rec[A, B]] {

  def g: RecTuple[A, B]
  def gmap: collection.Map[A, B] = if (this.isInstanceOf[Type[_]]) g._2 else g._2.map(x => Obj.copyDefinitions(this, x._1) -> Obj.copyDefinitions(this, x._2)).toMap
  def glist: Seq[B] = gmap.values.toSeq
  def gsep: String = g._1
  override def test(other: Obj): Boolean = {
    Inst.resolveToken(this, other) match {
      case aobj: Obj if !aobj.alive => !this.alive
      case anon: __ if __.isToken(anon) => this.test(Inst.resolveToken(this, anon))
      case anon: __ => Inst.resolveArg(this, anon).alive
      case astrm: Strm[_] => MultiSet.test(this, astrm)
      case arec: Rec[_, _] =>
        // this.name.equals(other.name) &&
        Poly.sameSep(this, arec) &&
          withinQ(this, arec) &&
          arec.gmap.count(x => qStar.equals(x._2.q) ||
            this.gmap.exists(y => y._1.test(x._1) && y._2.test(x._2))) == arec.gmap.size
      case atype: Type[_] => atype.name.equals(Tokens.obj)
      case _ => false
    }
  }

  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.g.hashCode()
  override def equals(other: Any): Boolean = other match {
    case obj: Obj if !this.alive => !obj.alive
    case astrm: Strm[_] => MultiSet.test(this, astrm)
    case arec: Rec[_, _] => Poly.sameSep(this, arec) &&
      this.name.equals(arec.name) &&
      eqQ(this, arec) &&
      this.gmap.size == arec.gmap.size &&
      this.gmap.zip(arec.gmap).forall(x => x._1._1.equals(x._2._1) && x._1._2.equals(x._2._2))
    case _ => false
  }
}
object Rec {
  def resolveSlots[A <: Obj, B <: Obj](start: A, arec: Rec[A, B]): Rec[A, B] = {
    if (arec.isSerial) {
      if (__.isAnonRoot(start)) return arec
      var local = start -> start
      arec.clone(g = (arec.gsep, arec.gmap.map(slot => {
        val key = Inst.resolveArg(local._1, slot._1)
        local = if (!key.alive) (key -> zeroObj.asInstanceOf[A]) else local._2 match {
          case astrm: Strm[_] => key -> strm(astrm.values.map(x => Inst.resolveArg(x, slot._2))).asInstanceOf[A]
          case _ => (key -> Inst.resolveArg(local._2, slot._2)).asInstanceOf[(A, A)]
        }
        local
      })), q = start.q)
    } else {
      arec.clone(g = (arec.gsep, arec.gmap.toSeq.map(slot => {
        val key = Inst.resolveArg(start, slot._1)
        (key, if (key.alive) Inst.resolveArg(start, slot._2) else zeroObj.asInstanceOf[B])
      }).foldLeft(Map.empty[A, B])((a, b) => a + (b._1 -> (if (b._2.isInstanceOf[Type[Obj]]) b._2 else {
        val alst: List[B] = List(b._2) ++ a.get(b._1).map(x => List(x)).getOrElse(List.empty)
        if (alst.size == 1) alst.head else strm(alst)
      })))))
    }
  }
  def keepFirst[A <: Obj, B <: Obj](start: Obj, arec: Rec[A, B]): Rec[A, B] = {
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
