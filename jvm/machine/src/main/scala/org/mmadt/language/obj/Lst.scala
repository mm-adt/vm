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
import org.mmadt.language.obj.Lst.LstTuple
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.branch.CombineOp
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory._

trait Lst[+A <: Obj] extends Poly[A]
  with CombineOp[Obj]
  with GetOp[Int, Obj]
  with PutOp[Int, Obj]
  with PlusOp[Lst[Obj]]
  with MultOp[Lst[Obj]]
  with ZeroOp[Lst[Obj]] {
  def g: LstTuple[A]
  def gsep: String = g._1
  lazy val glist: List[A] = if (null == g._2) List.empty[A] else g._2.map(x => if (this.isInstanceOf[Type[_]]) x else x.model(this.model))
  def ctype: Boolean = null == g._2 // type token

  override def equals(other: Any): Boolean = other match {
    case alst: Lst[_] => Poly.sameSep(this, alst) &&
      this.name.equals(alst.name) &&
      eqQ(this, alst) &&
      this.glist.zip(alst.glist).forall(b => b._1.equals(b._2))
    case _ => true // MAIN EQUALS IS IN TYPE
  }

  override final def `,`(next: Obj): Lst[next.type] = this.lstMaker(Tokens.`,`, next)
  override final def `;`(next: Obj): Lst[next.type] = this.lstMaker(Tokens.`;`, next)
  override final def `|`(next: Obj): Lst[next.type] = this.lstMaker(Tokens.`|`, next)

  private final def lstMaker(sep: String, obj: Obj): Lst[obj.type] = {
    obj match {
      case blst: Lst[Obj] => lst(g = (sep, List[obj.type](this.asInstanceOf[obj.type], blst.asInstanceOf[obj.type])))
      case _ => this.clone(g = (sep, this.g._2 :+ obj)).asInstanceOf[Lst[obj.type]]
    }
  }
}

object Lst {
  type LstTuple[+A <: Obj] = (String, List[A])
  def test[A <: Obj](alst: Lst[A], blst: Lst[A]): Boolean =
    Poly.sameSep(alst, blst) &&
      withinQ(alst, blst) &&
      (blst.ctype || {
        if (blst.isChoice) alst.g._2.exists(x => x.alive)
        else alst.g._2.size == blst.g._2.size
      }) &&
      alst.glist.zip(blst.glist).forall(pair => if (blst.isChoice && !pair._1.alive) true else pair._1.test(pair._2))

  def resolveSlots[A <: Obj](start: A, apoly: Lst[A], branch: Boolean = false): Lst[A] = {
    if (apoly.isSerial) {
      var local = start
      val z = apoly.clone(g = (apoly.gsep, apoly.glist.map(slot => {
        local = local match {
          case astrm: Strm[_] => strm(astrm.values.map(x => Inst.resolveArg(x, slot)))
          case x if slot.isInstanceOf[Value[_]] => slot.hardQ(q => multQ(x.q, q)) // TODO: hardcoded hack -- should really be part of Inst.resolveArg() and Obj.compute()
          case _ => Inst.resolveArg(local, slot)
        }
        local
      })))
      if (branch && z.g._2.exists(x => !x.alive)) z.q(qZero)
      else if (branch && z.g._2.forall(x => x.root)) z.clone(g = (z.gsep, List(z.g._2.last)))
      else z
    } else {
      if (branch && __.isAnonRoot(start) && apoly.g._2.map(x => x.hardQ(qOne)).toSet.size == 1 && apoly.g._2.forall(x => x.root))
        apoly.clone(g = (apoly.gsep, List(apoly.g._2.head.hardQ(apoly.g._2.foldLeft(qZero)((a, b) => plusQ(a, b.q)))))) // TODO: total hack
      else
        apoly.clone(g = (apoly.gsep, apoly.glist.map(slot => Inst.resolveArg(start, slot)).filter(x => !branch || x.alive)))
    }
  }

  def keepFirst[A <: Obj](apoly: Lst[A]): Lst[A] = {
    val first: scala.Int = apoly.glist.indexWhere(x => x.alive)
    apoly.clone(g = (apoly.gsep, apoly.glist.zipWithIndex.map(a => if (a._2 == first) a._1 else zeroObj.asInstanceOf[A])))
  }
}