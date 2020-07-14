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

import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.branch.CombineOp
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory._

trait Lst[A <: Obj] extends Poly[A]
  with CombineOp[A]
  with GetOp[Int, A]
  with PutOp[Int, A]
  with PlusOp[Lst[A]]
  with MultOp[Lst[A]]
  with ZeroOp[Lst[A]] {

  def g: LstTuple[A]
  def gsep: String = g._1
  lazy val glist: List[A] = g._2 /*.map(x => x.hardQ(multQ(this.q, x.q)))*/ .map(x => if (this.isInstanceOf[Type[_]]) x else Obj.copyDefinitions(this, x))
  override def equals(other: Any): Boolean = other match {
    case alst: Lst[_] => Poly.sameSep(this, alst) &&
      this.name.equals(alst.name) &&
      eqQ(this, alst) &&
      // (this.glist.size == alst.glist.size) &&
      this.glist.zip(alst.glist).forall(b => b._1.equals(b._2))
    case _ => true
  }
}
object Lst {
  def test[A <: Obj](alst: Lst[A], blst: Lst[A]): Boolean = Poly.sameSep(alst, blst) && // TODO: this.name.equals(other.name) &&
    withinQ(alst, blst) &&
    (blst.glist.isEmpty || alst.glist.size == blst.glist.size) && // TODO: should lists only check up to their length
    alst.glist.zip(blst.glist).find(b => !b._1.test(b._2)).forall(_ => return false)
  def keepFirst[A <: Obj](apoly: Lst[A]): Lst[A] = {
    val first: scala.Int = apoly.glist.indexWhere(x => x.alive)
    apoly.clone(g = (apoly.gsep, apoly.glist.zipWithIndex.map(a => if (a._2 == first) a._1 else zeroObj.asInstanceOf[A])))
  }
  def resolveSlots[A <: Obj](start: A, apoly: Lst[A]): Lst[A] = {
    if (apoly.isSerial) {
      var local = start
      apoly.clone(g = (apoly.gsep, apoly.glist.map(slot => {
        local = local match {
          case astrm: Strm[_] => strm(astrm.values.map(x => Inst.resolveArg(x, slot)))
          case _ => Inst.resolveArg(local, slot)
        }
        local
      })))
    } else apoly.clone(g = (apoly.gsep, apoly.glist.map(slot => Inst.resolveArg(start, slot))))
  }
}