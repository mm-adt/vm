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
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.storage.StorageFactory._

trait Lst[+A <: Obj] extends Poly[A]
  with GetOp[Obj, A]
  with PutOp[Int, Obj]
  with PlusOp[Lst[Obj]]
  with MultOp[Lst[Obj]]
  with ZeroOp[Lst[Obj]] {
  def g: LstTuple[A]
  def gsep: String = g._1
  lazy val glist: List[A] = if (null == g._2) List.empty[A] else g._2.map(x => x.update(this.model))
  override def ctype: Boolean = null == g._2 // type token
  override def scalarMult(start: Obj): this.type = this.clone(values => Lst.moduleStruct(gsep, values, start))

  override def equals(other: Any): Boolean = other match {
    case alst: Lst[_] => Poly.sameSep(this, alst) &&
      this.name.equals(alst.name) &&
      eqQ(this, alst) &&
      // this.glist.size == alst.glist.size &&
      this.glist.zip(alst.glist).forall(b => b._1.equals(b._2))
    case _ => true // MAIN EQUALS IS IN TYPE
  }
  def clone(f: List[A] => List[_]): this.type = this.clone(g = (this.gsep, f(this.glist)))
  final override def `,`: Lst[this.type] = lst(g = (Tokens.`,`, List(this)))
  final override def `,`(next: Obj): Lst[next.type] = this.lstMaker(Tokens.`,`, next)
  final override def `;`: Lst[this.type] = lst(g = (Tokens.`;`, List(this)))
  final override def `;`(next: Obj): Lst[next.type] = this.lstMaker(Tokens.`;`, next)
  final override def `|`: Lst[this.type] = lst(g = (Tokens.`|`, List(this)))
  final override def `|`(next: Obj): Lst[next.type] = this.lstMaker(Tokens.`|`, next)

  private final def lstMaker(sep: String, obj: Obj): Lst[obj.type] = {
    obj match {
      case _ if sep != this.gsep => lst(g = (sep, List(this, obj).asInstanceOf[List[obj.type]]))
      // case _ if this.ctype => this.clone(g = (sep, List(obj))).asInstanceOf[Lst[obj.type]]
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
        if (blst.isChoice) alst.glist.exists(x => x.alive)
        else alst.size == blst.size
      }) &&
      alst.glist.zip(blst.glist).forall(pair => if (blst.isChoice && pair._1.alive && pair._2.alive && pair._1 == pair._2) true else pair._1.test(pair._2))

  def moduleStruct[A <: Obj](gsep: String, values: List[A], start: Obj = null): List[A] = gsep match {
    /////////// ,-lst
    case Tokens.`,` =>
      if (null == start) return Type.mergeObjs(values)
      Type.mergeObjs(Type.mergeObjs(values).map(v =>
        if (!__.isAnon(start) && v.isInstanceOf[Value[_]]) start `=>` v
        else start ~~> v)).filter(_.alive)
    /////////// ;-lst
    case Tokens.`;` =>
      if (null == start) return values
      var running = start
      values.map(v => {
        running = if (running.isInstanceOf[Strm[_]]) strm[A](running.toStrm.values.map(r => r ~~> v): _*)
        else running ~~> v match {
          case x: Value[_] if v.isInstanceOf[Value[_]] => x.hardQ(q => multQ(running.q, q)).asInstanceOf[A]
          case x => x
        }
        running
      }).asInstanceOf[List[A]]
    /////////// |-lst
    case Tokens.`|` =>
      val newStart: Obj = if (null == start) __ else start
      var taken: Boolean = false
      values.map(v => newStart ~~> v)
        .filter(_.alive)
        .filter(v => {
          if (taken) false
          else if (zeroable(v.q)) true
          else {
            taken = true;
            true
          }
        })
  }

  def cmult[A <: Obj](apoly: Lst[A], bpoly: Lst[A]): Lst[A] = {
    var clist: List[A] = Nil
    apoly.glist.foreach(a => bpoly.glist.foreach(b => {
      clist = clist :+ (a `=>` b)
    }))
    lst(g = (Tokens.`,`, clist))
  }
}