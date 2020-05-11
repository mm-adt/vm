package org.mmadt.storage.obj

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.storage.StorageFactory.qOne

class OLst[A <: Obj](val name: String = Tokens.lst, val ground: LstTuple[A], val q: IntQ = qOne, val via: ViaTuple = base) extends Lst[A] {
  override def clone(name: String = this.name,
                     ground: Any = this.ground,
                     q: IntQ = this.q,
                     via: ViaTuple = this.via): this.type = new OLst[A](name = name, ground = ground.asInstanceOf[LstTuple[A]], q = q, via = via).asInstanceOf[this.type]
}
