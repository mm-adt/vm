package org.mmadt.storage.obj

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.storage.StorageFactory.qOne

class OLst[A <: Obj](val name: String = Tokens.lst, val g: LstTuple[A], val q: IntQ = qOne, val via: ViaTuple = base) extends Lst[A] {
  override def clone(name: String = this.name,
                     g: Any = this.g,
                     q: IntQ = this.q,
                     via: ViaTuple = this.via): this.type = new OLst[A](name = name, g = g.asInstanceOf[LstTuple[A]], q = q, via = via).asInstanceOf[this.type]
}
