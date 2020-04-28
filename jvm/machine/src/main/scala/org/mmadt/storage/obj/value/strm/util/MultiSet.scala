package org.mmadt.storage.obj.value.strm.util

import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Obj, _}
import org.mmadt.storage.StorageFactory._

class MultiSet[A <: Obj](val map: Map[A, A] = Map.empty[A, A]) extends Seq[A] {

  def get(a: A): Option[A] = map.get(a)
  def put(a: A): MultiSet[A] = {
    val obj = map.get(a).map(x => a.q(plusQ(x.q, a.q))).getOrElse(a)
    new MultiSet[A](map + (obj -> obj))
  }
  def objSize: Long = map.size
  def qSize: IntQ = map.values.foldRight((int(0), int(0)))((a, b) => plusQ(a.q, b))
  override def length: scala.Int = objSize.toInt
  override def apply(idx: scala.Int): A = this.map.values.toSeq.apply(idx)
  override def iterator: Iterator[A] = this.map.valuesIterator
  override def toSeq: Seq[A] = MultiSet(map.values.toSeq)

  override def hashCode: scala.Int = map.hashCode()
  override def equals(other: Any): Boolean = other match {
    case multiSet: MultiSet[_] => multiSet.map == this.map
    case _ => false
  }
}

object MultiSet {
  def put[A <: Obj](objs: A*): MultiSet[A] = objs.foldLeft(new MultiSet[A]())((a, b) => a.put(b))
  def apply[A <: Obj](objs: Seq[A]): MultiSet[A] = MultiSet.put(objs: _*)
  def test(a: Strm[Obj], b: Strm[Obj]): Boolean = MultiSet(a.values) == MultiSet(b.values)
}
