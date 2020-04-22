package org.mmadt.language.obj.op.map

import org.mmadt.language.Tokens
import org.mmadt.language.obj.branch.{Brch, Coprod, Prod}
import org.mmadt.language.obj.{IntQ, Obj}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait PlusBOp[O <: Obj] {
  this: Brch[O] =>
  def plus(other: Brch[O]): Brch[O] = (this match {
    case prodA: Prod[O] => other match {
      case prodB: Prod[O] => coprod(prodA, prodB)
      case coprodB: Coprod[O] => coprod(prodA, coprodB)
    }
    case coprodA: Coprod[O] => other match {
      case prodB: Prod[O] => coprod(coprodA, prodB)
      case coprodB: Coprod[O] => coprod().clone(value = coprodA.value ++ coprodB.value)
    }
  }).via(this, PlusBOp(other)).asInstanceOf[Brch[O]]
}

object PlusBOp {
  def apply[O <: Obj](other: Brch[O]): PlusBInst[O] = new PlusBInst[O](other)

  class PlusBInst[O <: Obj](other: Brch[O], q: IntQ = qOne) extends VInst[Brch[O], Brch[O]]((Tokens.plus, List(other)), q) {
    override def q(q: IntQ): this.type = new PlusBInst[O](other, q).asInstanceOf[this.type]
    override def exec(start: Brch[O]): Brch[O] = start.plus(other).via(start, this)
  }

}

