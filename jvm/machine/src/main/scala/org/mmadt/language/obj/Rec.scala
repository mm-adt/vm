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
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait Rec[A <: Obj, +B <: Obj] extends Poly[B]
  with PlusOp[Rec[A, Obj]]
  with GetOp[A, B]
  with PutOp[A, Obj]
  with ZeroOp[Rec[A, Obj]] {
  def g: RecTuple[A, B]
  def gsep: String = g._1
  lazy val gmap: PairList[A, B] =
    if (null == this.g._2) Nil
    else g._2.map(x => x._1.update(this.model).asInstanceOf[A] -> x._2.update(this.model).asInstanceOf[B])
  override def glist: Seq[B] = this.gmap.values
  override def ctype: Boolean = null == g._2 // type token

  def clone(f: PairList[A, B] => PairList[A, Obj]): this.type = this.clone(g = (this.gsep, f(this.gmap)))

  override def equals(other: Any): Boolean = other match {
    case arec: Rec[_, _] => Poly.sameSep(this, arec) &&
      this.name.equals(arec.name) &&
      eqQ(this, arec) &&
      this.size == arec.size &&
      this.gmap.zip(arec.gmap).forall(x => x._1._1.equals(x._2._1) && x._1._2.equals(x._2._2))
    case _ => true
  }

  final def `_,`(next: Tuple2[_, _]): this.type = this.`,`(next)
  final def `_;`(next: Tuple2[_, _]): this.type = this.`;`(next)
  final def `_|`(next: Tuple2[_, _]): this.type = this.`|`(next)
  final def `,`(next: Tuple2[_, _]): this.type = this.clone(g = (Tokens.`,`, this.g._2 :+ next))
  final def `;`(next: Tuple2[_, _]): this.type = this.clone(g = (Tokens.`;`, this.g._2 :+ next))
  final def `|`(next: Tuple2[_, _]): this.type = this.clone(g = (Tokens.`|`, this.g._2 :+ next))
}

object Rec {
  type RecTuple[A <: Obj, +B <: Obj] = (String, PairList[A, B])
  type PairList[A <: Obj, +B <: Obj] = List[ObjPair[A, B]]
  type ObjPair[A <: Obj, +B <: Obj] = Tuple2[A, B]

  @inline implicit def listToRichList[A <: Obj, B <: Obj](ground: PairList[A, B]): RichList[A, B] = new RichList[A, B](ground)
  protected class RichList[A <: Obj, B <: Obj](val list: PairList[A, B]) {
    def fetchOption(key: A): Option[B] = list.filter(x => key == x._1).map(x => x._2).headOption
    def fetch(key: A): B = fetchOption(key).get
    def fetchOrElse(key: A, other: B): B = fetchOption(key).getOrElse(other)
    def values: List[B] = list.map(x => x._2)
    def replace(other: PairList[A, B]): PairList[A, B] = other.foldLeft(list)((a, b) => a.replace(b))
    def replace(other: Tuple2[A, B]): PairList[A, B] =
      if (list.fetchOption(other._1).isDefined) list.map(x => if (other._1 == x._1) other._1 -> other._2 else x) // TODO: equality order matters! (cause of lst size)
      else list :+ other
  }

  def test[A <: Obj, B <: Obj](arec: Rec[A, B], brec: Rec[A, B]): Boolean = Poly.sameSep(arec, brec) && withinQ(arec, brec) &&
    (brec.ctype || brec.gmap.forall(x => qStar.equals(x._2.q) || arec.gmap.exists(y => y._1.test(x._1) && y._2.test(x._2))))

  def moduleStruct[A <: Obj, B <: Obj](start: A, gsep: String, pairs: PairList[A, B]): PairList[A, B] = gsep match {
    /////////// ,-rec
    case Tokens.`,` =>
      pairs.map(kv => (start ~~> kv._1) -> kv._2)
        .filter(kv => kv._1.alive)
        .map(kv => kv._1 -> (start ~~> kv._2))
        .groupBy(kv => kv._1)
        .map(kv => kv._1 -> {
          val mergedBranches: List[B] = Type.mergeObjs(kv._2.map(x => x._2)).asInstanceOf[List[B]]
          if (mergedBranches.size == 1) mergedBranches.head else __.branch(lst(g = (Tokens.`,`, mergedBranches)))
        }).toList
    /////////// ;-rec
    case Tokens.`;` =>
      var running = start -> start
      pairs.map(kv => {
        val key = running._1 ~~> kv._1
        running = (if (!key.alive) key -> zeroObj else key -> (running._2 ~~> kv._2)).asInstanceOf[(A, A)]
        running
      }).asInstanceOf[PairList[A, B]]
    /////////// |-rec
    case Tokens.`|` =>
      // var taken: Boolean = false
      pairs.map(kv => (start ~~> kv._1) -> kv._2)
        .filter(kv => kv._1.alive)
        /*.filter(kv =>
          if(taken) false
          else if (zeroable(kv._1.q)) true
          else {
            taken = true;
            true
          })*/
        .map(kv => kv._1 -> (start ~~> kv._2))
  }

  def moduleMult[A <: Obj, B <: Obj](start: A, arec: Rec[A, B]): Rec[A, B] = arec.clone(pairs => moduleStruct(start, arec.gsep, pairs))

  def keepFirst[A <: Obj, B <: Obj](start: Obj, arec: Rec[A, B]): Rec[A, B] = {
    var found: Boolean = false;
    arec.clone(_.map(x => {
      if (!found) {
        val keyResolve = start ~~> x._1
        if (keyResolve.alive) {
          found = true
          (keyResolve, start ~~> x._2)
        } else (zeroObj, zeroObj)
      } else
        (zeroObj, zeroObj)
    }).asInstanceOf[PairList[A, B]])
  }
}
