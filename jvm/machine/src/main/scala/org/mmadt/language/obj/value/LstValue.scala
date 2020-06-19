package org.mmadt.language.obj.value
import org.mmadt.language.obj.{Lst, LstTuple, Obj}

trait LstValue[A <: Obj] extends
   ObjValue
  with Value[Lst[A]] with Lst[A] {
  def g: LstTuple[A]
}
