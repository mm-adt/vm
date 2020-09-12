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
import org.mmadt.language.obj.Rec._
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Rec[A <: Obj, +B <: Obj] extends Poly[B]
  with PlusOp[Rec[A, Obj]]
  with GetOp[A, B]
  with PutOp[A, Obj]
  with ZeroOp[Rec[A, Obj]] {
  def g:RecTuple[A, B]
  def gsep:String = g._1
  lazy val gmap:Pairs[A, B] =
    if (null == this.g._2) Nil
    else g._2.map(x => x._1.update(this.model).asInstanceOf[A] -> x._2.update(this.model).asInstanceOf[B])
  override def glist:Seq[B] = this.gmap.values
  override def ctype:Boolean = null == g._2 // type token
  override def scalarMult(start:Obj):this.type = this.clone(pairs => Rec.moduleStruct(gsep, pairs, start))

  def clone(f:Pairs[A, B] => Pairs[A, Obj]):this.type = this.clone(g = (this.gsep, f(this.gmap)))
  override def equals(other:Any):Boolean = other match {
    case arec:Rec[_, _] => Poly.sameSep(this, arec) &&
      this.name.equals(arec.name) &&
      eqQ(this, arec) &&
      this.size == arec.size &&
      this.gmap.zip(arec.gmap).forall(x => x._1._1.equals(x._2._1) && x._1._2.equals(x._2._2))
    case _ => true
  }

  final def `,`(next:Tuple2[_ <: Obj, _ <: Obj]):this.type = this.clone(g = (Tokens.`,`, this.g._2 :+ next.asInstanceOf[Tuple2[A, B]]))
  final def `;`(next:Tuple2[_ <: Obj, _ <: Obj]):this.type = this.clone(g = (Tokens.`;`, this.g._2 :+ next.asInstanceOf[Tuple2[A, B]]))
  final def `|`(next:Tuple2[_ <: Obj, _ <: Obj]):this.type = this.clone(g = (Tokens.`|`, this.g._2 :+ next.asInstanceOf[Tuple2[A, B]]))
  final def `_,`(next:Tuple2[_ <: Obj, _ <: Obj]):this.type = this.`,`(next)
  final def `_;`(next:Tuple2[_ <: Obj, _ <: Obj]):this.type = this.`;`(next)
  final def `_|`(next:Tuple2[_ <: Obj, _ <: Obj]):this.type = this.`|`(next)
}

object Rec {
  type RecTuple[A <: Obj, +B <: Obj] = (String, Pairs[A, B])
  type Pairs[A <: Obj, +B <: Obj] = List[Tuple2[A, B]]

  class RichTuple[A <: Obj, +B <: Obj](val ground:Tuple2[A, B]) {
    final def `;`(next:Tuple2[_ <: Obj, _ <: Obj]):Rec[A, B] = rec(g = (Tokens.`;`, List(ground, next.asInstanceOf[Tuple2[A, B]])))
    final def `,`(next:Tuple2[_ <: Obj, _ <: Obj]):Rec[A, B] = rec(g = (Tokens.`,`, List(ground, next.asInstanceOf[Tuple2[A, B]])))
    final def `|`(next:Tuple2[_ <: Obj, _ <: Obj]):Rec[A, B] = rec(g = (Tokens.`|`, List(ground, next.asInstanceOf[Tuple2[A, B]])))
    final def `_;`(next:Tuple2[_ <: Obj, _ <: Obj]):Rec[A, B] = this.`;`(next)
    final def `_,`(next:Tuple2[_ <: Obj, _ <: Obj]):Rec[A, B] = this.`,`(next)
    final def `_|`(next:Tuple2[_ <: Obj, _ <: Obj]):Rec[A, B] = this.`|`(next)
    final def `;`:Rec[A, B] = rec(g = (Tokens.`;`, List(ground)))
    final def `,`:Rec[A, B] = rec(g = (Tokens.`,`, List(ground)))
    final def `|`:Rec[A, B] = rec(g = (Tokens.`|`, List(ground)))
    final def `_;`:Rec[A, B] = this.`;`
    final def `_,`:Rec[A, B] = this.`,`
    final def `_|`:Rec[A, B] = this.`|`
  }

  @inline implicit def pairsToRichPairs[A <: Obj, B <: Obj](ground:Pairs[A, B]):RichPairs[A, B] = new RichPairs[A, B](ground)
  protected class RichPairs[A <: Obj, B <: Obj](val list:Pairs[A, B]) {
    def fetchOption(key:A):Option[B] = list.filter(x => key == x._1).map(x => x._2).headOption
    def fetch(key:A):B = fetchOption(key).get
    def fetchOrElse(key:A, other:B):B = fetchOption(key).getOrElse(other)
    def values:List[B] = list.map(x => x._2)
    def replace(other:Pairs[A, B]):Pairs[A, B] = other.foldLeft(list)((a, b) => a.replace(b))
    def replace(other:Tuple2[A, B]):Pairs[A, B] =
      if (list.fetchOption(other._1).isDefined) list.map(x => if (other._1 == x._1) other._1 -> other._2 else x) // TODO: equality order matters! (cause of lst size)
      else list :+ other
  }

  def test[A <: Obj, B <: Obj](arec:Rec[A, B], brec:Rec[A, B]):Boolean = Poly.sameSep(arec, brec) && withinQ(arec, brec) &&
    (brec.ctype || brec.gmap.forall(x => qStar.equals(x._2.q) || arec.gmap.exists(y => y._1.test(x._1) && y._2.test(x._2))))

  private def semi[A <: Obj, B <: Obj](objs:Pairs[A, B]):Pairs[A, B] = if (objs.exists(x => !x._1.alive || !x._2.alive)) List(zeroObj -> zeroObj).asInstanceOf[Pairs[A, B]] else objs.filter(kv => !__.isAnonRootAlive(kv._2))
  def moduleStruct[A <: Obj, B <: Obj](gsep:String, pairs:Pairs[A, B], start:Obj = null):Pairs[A, B] = gsep match {
    /////////// ,-rec
    case Tokens.`,` =>
      val nostart:Boolean = null == start
      pairs.map(kv => (if (nostart) kv._1 else (start ~~> kv._1)) -> kv._2)
        .filter(kv => kv._1.alive)
        .map(kv => kv._1 -> (if (nostart) kv._2 else Tokens.tryName(kv._2, start ~~> toBaseName(kv._2)))) // this is odd
        .filter(kv => kv._2.alive)
        .foldLeft(Map.empty[A, List[B]])((a, b) => a + (b._1 -> (a.get(b._1).map(c => c :+ b._2).getOrElse(List(b._2)))))
        .map(kv => kv._1 -> {
          val mergedBranches:List[B] = Type.mergeObjs(kv._2)
          if (mergedBranches.size == 1) mergedBranches.head
          else if (mergedBranches.exists(x => x.isInstanceOf[Type[_]])) __.branch(lst(g = (Tokens.`,`, mergedBranches)))
          else strm(mergedBranches)
        }).toList
    /////////// ;-rec
    case Tokens.`;` =>
      if (null == start) return semi(pairs)
      var running = start
      semi(pairs.map(kv => {
        val key = running ~~> kv._1
        val keyValue = (
          if (!key.alive) (key -> zeroObj)
          else (key -> (running ~~> kv._2))).asInstanceOf[Tuple2[A, A]]
        running = keyValue._2
        keyValue
      }).asInstanceOf[Pairs[A, B]])
    /////////// |-rec
    case Tokens.`|` =>
      val nostart:Boolean = null == start
      val newStart:Obj = if (nostart) __ else start
      var taken:Boolean = false
      pairs
        .filter(kv => kv._1.alive && kv._2.alive)
        .map(kv => (newStart ~~> kv._1) -> kv._2)
        .filter(kv => kv._1.alive)
        .filter(kv =>
          if (taken) false
          else if (zeroable(kv._1.q)) true
          else {
            taken = true;
            true
          })
        .map(kv => if (nostart) kv else kv._1 -> ((start ~~> kv._2) match {
          case x if kv._2.isInstanceOf[Value[_]] => x.hardQ(q => q.mult(kv._2.q)).asInstanceOf[B]
          case x => x
        }))
        .filter(kv => kv._1.alive && kv._2.alive)
  }
}
