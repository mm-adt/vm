package org.mmadt.language.obj

import org.mmadt.language.LanguageFactory
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.value.Value

trait Coprod[A <: Obj] extends Brch[A]
  with Type[Coprod[A]]
  with Value[Coprod[A]] {

  override def test(other: Obj): Boolean = other match {
    case prod: Coprod[_] =>
      if (prod.value.isEmpty || this.value.equals(prod.value)) return true
      this.value.zip(prod.value).foldRight(true)((a, b) => a._1.test(a._2) && b)
    case _ => false
  }

  override def toString: String = LanguageFactory.printBrch(this)
  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.value.hashCode()
  override def equals(other: Any): Boolean = other match {
    case brch: Coprod[_] =>
      brch.name.equals(this.name) &&
        ((this.isValue && brch.isValue && this.value.zip(brch.value).foldRight(true)((a, b) => a._1.test(a._2) && b)) ||
          (this.value == brch.value && this.via == brch.via))
    case _ => false
  }
}
