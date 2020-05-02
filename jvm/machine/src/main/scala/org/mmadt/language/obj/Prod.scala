package org.mmadt.language.obj

import org.mmadt.language.LanguageFactory
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.Value

trait Prod[A <: Obj] extends Brch[A]
  with Type[Prod[A]]
  with Value[Prod[A]] {

  override def test(other: Obj): Boolean = other match {
    case prod: Prod[_] =>
      if (prod.ground.isEmpty || this.ground.equals(prod.ground)) return true
      this.ground.zip(prod.ground).foldRight(false)((a, b) => a._1.test(a._2) || b)
    case _ => false
  }

  override def toString: String = LanguageFactory.printBrch(this)
  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.ground.hashCode()
  override def equals(other: Any): Boolean = other match {
    case brch: Prod[_] =>
      brch.name.equals(this.name) &&
        eqQ(brch, this) &&
        ((this.isValue && brch.isValue && this.ground.zip(brch.ground).foldRight(true)((a, b) => a._1.test(a._2) && b)) ||
          (this.ground == brch.ground && this.via == brch.via))
    case _ => false
  }
}
