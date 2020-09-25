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
import org.mmadt.language.obj.Poly.fetchVars
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
  def g:LstTuple[A]
  def gsep:String = g._1
  def gstrm:Strm[A] = strm[A](glist)
  lazy val glist:List[A] = if (null == g._2) List.empty[A] else g._2.map(x => x.update(this.model))
  override def ctype:Boolean = null == g._2 // type token
  override def scalarMult(start:Obj):this.type = this.clone(values => Lst.moduleStruct(gsep, values, start))
  override def reload:this.type = this.update(fetchVars(this.model, this.g._2))

  override def equals(other:Any):Boolean = other match {
    case alst:Lst[_] => Poly.sameSep(this, alst) &&
      this.name.equals(alst.name) &&
      eqQ(this, alst) &&
      //this.glist.size == alst.glist.size &&
      (if (this.isParallel) alst.glist.forall(x => this.glist.contains(x)) // set semantics for abelian group
      else this.glist.zip(alst.glist).forall(b => b._1.equals(b._2))) // lst semantics for monoids
    case _ => true // MAIN EQUALS IS IN TYPE
  }
  def clone(f:List[A] => List[_]):this.type = this.clone(g = (this.gsep, f(this.glist)))
  final override def `,`:Lst[this.type] = lst(g = (Tokens.`,`, List(this)))
  final override def `,`(next:Obj):Lst[next.type] = this.lstMaker(Tokens.`,`, next)
  final override def `;`:Lst[this.type] = lst(g = (Tokens.`;`, List(this)))
  final override def `;`(next:Obj):Lst[next.type] = this.lstMaker(Tokens.`;`, next)
  final override def `|`:Lst[this.type] = lst(g = (Tokens.`|`, List(this)))
  final override def `|`(next:Obj):Lst[next.type] = this.lstMaker(Tokens.`|`, next)

  private final def lstMaker(sep:String, obj:Obj):Lst[obj.type] = {
    obj match {
      case _ if sep != this.gsep => lst(g = (sep, List(this, obj).asInstanceOf[List[obj.type]]))
      case _ => this.clone(g = (sep, this.g._2 :+ obj)).asInstanceOf[Lst[obj.type]]
    }
  }
}

object Lst {
  type LstTuple[+A <: Obj] = (String, List[A])

  def shapeTest(alst:Lst[Obj], blst:Lst[Obj]):Boolean = Poly.sameSep(alst, blst) && alst.size == blst.size && alst.glist.zip(blst.glist).forall(pair => WalkOp.testSourceToTarget(pair._1, pair._2))
  def exactTest(alst:Lst[Obj], bobj:Obj):Boolean = bobj match {
    case blst:Lst[Obj] => blst.ctype || (Poly.sameSep(alst, blst) && alst.size == blst.size &&
      alst.glist.zip(blst.glist).forall(p => __.isAnon(p._1) || (p._1.name.equals(p._2.name) && p._1.test(Obj.resolveToken(p._1, p._2)))))
    case _ => false
  }
  def test[A <: Obj](alst:Lst[A], blst:Lst[A]):Boolean = {
    alst.q.within(blst.q) &&
      (blst.ctype || {
        val eqsep:Boolean = alst.gsep == blst.gsep
        if (alst.isInstanceOf[Inst[Obj, Obj]]) alst.glist.zip(blst.glist).forall(pair => pair._1.rangeObj.test(pair._2.rangeObj))
        else alst.gsep match {
          // ,-lst
          case Tokens.`,` if eqsep => alst.gstrm.q.within(blst.gstrm.q) && alst.glist.forall(x => blst.glist.exists(y => x.rangeObj.hardQ(qOne).test(y.rangeObj.hardQ(qOne)))) ||
            (alst.size == blst.size && alst.glist.zip(blst.glist).forall(pair => pair._1.rangeObj.test(pair._2.rangeObj)))
          // ;-lst
          case Tokens.`;` if eqsep =>
            (alst.size == blst.size && alst.glist.zip(blst.glist).forall(pair => pair._1.rangeObj.test(pair._2.rangeObj) || pair._1.test(pair._2))) ||
              (alst.glist.contains(zeroObj) && blst.glist.exists(z => z.q.zeroable))
          // TODO: are we guaranteed the last check given it's a monoid?
          // |-lst
          case Tokens.`|` if eqsep => alst.glist.exists(a => blst.glist.exists(b => a.test(b)))
          case _ => alst.glist.forall(a => blst.glist.exists(b => a.test(b)))
        }
      })
  }

  //private def semi[A <: Obj](objs:List[A]):List[A] = if (objs.exists(x => !x.alive)) List(zeroObj.asInstanceOf[A]) else objs.filter(v => !__.isAnonRootAlive(v))
  def moduleStruct[A <: Obj](gsep:String, values:List[A], start:Obj = null):List[A] = gsep match {
    /////////// ,-lst
    case Tokens.`,` =>
      if (null == start) return Type.mergeObjs(values).filter(_.alive)
      Type.mergeObjs(Type.mergeObjs(values).map(v =>
        if (!__.isAnon(start) && v.isInstanceOf[Value[_]]) start `=>` v
        else start ~~> v)).filter(_.alive)
    /////////// ;-lst
    case Tokens.`;` =>
      if (null == start) return values
      var running = start
      values.map(v => {
        running = if (running.isInstanceOf[Strm[_]]) strm[A](running.toStrm.drain.map(r => r ~~> v):_*)
        else running ~~> v match {
          case x:Value[_] if v.isInstanceOf[Value[_]] => x.hardQ(q => multQ(running.q, q)).asInstanceOf[A]
          case x => x
        }
        running.named(v.name, ignoreAnon = true)
      }).asInstanceOf[List[A]]
    /////////// |-lst
    case Tokens.`|` =>
      val newStart:Obj = if (null == start) __ else start
      var taken:Set[Obj] = Set.empty
      values.map(v => {
        (newStart ~~> v) match {
          // TODO: we need a concept of stable vs. non-stable quantifiers as
          //  you don't want types to alter values quantifiers if they are zeroable.
          case avalue:OValue[A]
            if avalue.alive &&
              v.isInstanceOf[Type[_]] &&
              v.q.zeroable => avalue.hardQ(newStart.q)
          case x => x
        }
      }).filter(_.alive)
        .filter(v => {
          if (taken.exists(x => !x.q.zeroable && !__.isToken(x) && v.domainObj.test(x.domainObj) && v.rangeObj.test(x.rangeObj))) false
          else {
            taken += v
            true
          }
        })
  }
}