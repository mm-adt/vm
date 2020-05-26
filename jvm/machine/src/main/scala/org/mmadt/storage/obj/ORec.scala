package org.mmadt.storage.obj
import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.storage.StorageFactory.qOne

class ORec[A <: Obj, B <: Obj](val name: String = Tokens.rec, val g: RecTuple[A, B] = (Tokens.`,`, Map.empty[A, B]), val q: IntQ = qOne, val via: ViaTuple = base) extends Rec[A, B] {
  override def clone(name: String = this.name,
                     g: Any = this.g,
                     q: IntQ = this.q,
                     via: ViaTuple = this.via): this.type = new ORec[A, B](name, g.asInstanceOf[RecTuple[A, B]], q, via).asInstanceOf[this.type]
}