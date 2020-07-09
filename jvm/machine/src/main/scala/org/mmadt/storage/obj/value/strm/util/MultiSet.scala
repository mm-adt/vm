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

package org.mmadt.storage.obj.value.strm.util

import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Obj, _}
import org.mmadt.storage.StorageFactory._

import scala.collection.immutable.ListSet

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class MultiSet[A <: Obj](val baseSet: ListSet[A] = ListSet.empty[A]) extends Seq[A] {
  def get(a: A): Option[A] = baseSet.find(b => a.asInstanceOf[Value[_]].g.equals(b.asInstanceOf[Value[_]].g))
  def put(a: A): MultiSet[A] = {
    val oldObj: Option[A] = this.get(a)
    new MultiSet[A](oldObj.map(x => baseSet - x).getOrElse(baseSet) + oldObj.map(x => x.hardQ(plusQ(a.q, x.q))).getOrElse(a))
  }
  def objSize: Long = baseSet.size
  def qSize: IntQ = baseSet.foldRight(qZero)((a, b) => plusQ(a.q, b))
  override def length: scala.Int = objSize.toInt
  override def apply(idx: scala.Int): A = this.baseSet.toSeq.apply(idx)
  override def iterator: Iterator[A] = this.baseSet.iterator
  override def toSeq: Seq[A] = baseSet.toSeq

  override def hashCode: scala.Int = baseSet.hashCode()
  override def equals(other: Any): Boolean = other match {
    case multiSet: MultiSet[_] => multiSet.baseSet == this.baseSet
    case _ => false
  }
}

object MultiSet {
  def put[A <: Obj](objs: A*): MultiSet[A] = objs.foldLeft(new MultiSet[A])((a, b) => a.put(b))
  def equals(a: Obj, b: Obj): Boolean = MultiSet(a.toStrm.values) == MultiSet(b.toStrm.values)
  def apply[A <: Obj](objs: Seq[A]): MultiSet[A] = objs.flatMap {
    case astrm: Strm[A] => astrm.values
    case x => List(x)
  }.foldLeft(new MultiSet[A])((a, b) => a.put(b))
}
