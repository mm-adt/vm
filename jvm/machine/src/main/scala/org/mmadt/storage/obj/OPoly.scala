package org.mmadt.storage.obj

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.storage.StorageFactory.qOne

class OPoly[A <: Obj](val name: String = Tokens.empty, val ground: PolyTuple[A], val q: IntQ = qOne, val via: ViaTuple = base) extends Poly[A] {
  override def clone(name: String = this.name,
                     ground: Any = this.ground,
                     q: IntQ = this.q,
                     via: ViaTuple = this.via): this.type = new OPoly[A](name = name, ground = ground.asInstanceOf[PolyTuple[A]], q = q, via = via).asInstanceOf[this.type]
}
