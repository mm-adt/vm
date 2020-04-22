package org.mmadt.language.obj.op.map

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.branch.{Brch, Coprod, Prod}
import org.mmadt.language.obj.{IntQ, Obj}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait MultBOp[O <: Obj] {
  this: Brch[O] =>
  def mult(other: Brch[O]): Brch[O] = {
    MultBOp.multObj[O](this match {
      case prodA: Prod[O] => other match {
        case prodB: Prod[O] => prod[O]().clone(value = prodA.value ++ prodB.value)
        case coprodB: Coprod[O] => coprod[O]().clone(value = coprodB.value.map(a => prod().clone(value = prodA.value :+ a)))
      }
      case coprodA: Coprod[O] => other match {
        case prodB: Prod[O] => coprod[O]().clone(value = coprodA.value.map(a => prod().clone(value = a +: prodB.value)))
        case coprodB: Coprod[O] => coprod[O]().clone(value = coprodA.value.flatMap(a => coprodB.value.map(b => prod(a, b))))
      }
    }).via(this, MultBOp(other))
  }
}

object MultBOp {
  def apply[O <: Obj](other: Brch[O]): MultBInst[O] = new MultBInst[O](other)

  class MultBInst[O <: Obj](other: Brch[O], q: IntQ = qOne) extends VInst[Brch[O], Brch[O]]((Tokens.mult, List(other)), q) {
    override def q(q: IntQ): this.type = new MultBInst[O](other, q).asInstanceOf[this.type]

    override def exec(start: Brch[O]): Brch[O] = start.mult(other).via(start, this)
  }

  def multObj[O <: Obj](brch: Brch[O]): Brch[O] = {
    if (!brch.isType) return brch
    brch.clone(value = List(brch.value.foldLeft(brch.value.head.asInstanceOf[Type[Obj]].domain[Obj]())((a, b) => a.compute[Obj](b.asInstanceOf[Type[Obj]]).asInstanceOf[Type[Obj]])))
  }

}

