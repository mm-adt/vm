package org.mmadt.language.obj

import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.branch.{CombineOp, MergeOp}
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{IntValue, Value}
import org.mmadt.language.{LanguageException, LanguageFactory}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.strm.util.MultiSet

trait Lst[A <: Obj] extends Poly[A]
  with Type[Lst[A]]
  with Value[Lst[A]]
  with CombineOp[A]
  with MergeOp[A]
  with GetOp[Int, A]
  with PutOp[Int, A]
  with HeadOp[A]
  with TailOp
  with PlusOp[Lst[A]]
  with MultOp[Lst[A]]
  //with OneOp[Lst[A]]
  with ZeroOp[Lst[A]] {

  def g: LstTuple[A]
  override def gsep: String = g._1
  override def glist: List[A] = g._2

  def clone(values: List[A]): this.type = this.clone(g = (gsep, values))

  override def get(key: Int): A = {
    val valueType: A = key match {
      case aint: IntValue =>
        Lst.checkIndex(this, aint.g.toInt)
        this.glist(aint.g.toInt)
      case _ => obj.asInstanceOf[A]
    }
    if (valueType.via == base) valueType.via(this, GetOp[Obj, A](key, valueType)) else valueType.via(valueType.via._1 | this, GetOp[Obj, A](key, valueType))
  }

  override def get[BB <: Obj](key: Int, btype: BB): BB = btype.via(this, GetOp[Obj, BB](key, btype))

  override def test(other: Obj): Boolean = other match {
    case astrm: Strm[_] => MultiSet.test(this, astrm)
    case alst: Lst[_] =>
      if (alst.glist.isEmpty || this.glist.equals(alst.glist)) return true
      Poly.sameSep(this, alst) && this.glist.zip(alst.glist).foldRight(true)((a, b) => a._1.test(a._2) && b)
    case _ => false
  }

  override def toString: String = LanguageFactory.printLst(this)
  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.g.hashCode()
  override def equals(other: Any): Boolean = other match {
    case astrm: Strm[_] => MultiSet.test(this, astrm)
    case alst: Lst[_] =>
      Poly.sameSep(this, alst) && alst.name.equals(this.name) && eqQ(alst, this) &&
        ((this.isValue && alst.isValue && this.glist.zip(alst.glist).foldRight(true)((a, b) => a._1.test(a._2) && b)) ||
          (this.glist == alst.glist && this.via == alst.via))
    case _ => false
  }
}

object Lst {
  def checkIndex(apoly: Lst[_], index: scala.Int): Unit = {
    if (index < 0) throw new LanguageException("poly index must be 0 or greater: " + index)
    if (apoly.glist.length < (index + 1)) throw new LanguageException("poly index is out of bounds: " + index)
  }
  def keepFirst[A <: Obj](apoly: Lst[A]): Lst[A] = {
    val first: scala.Int = apoly.glist.indexWhere(x => x.alive)
    apoly.clone(apoly.glist.zipWithIndex.map(a => if (a._2 == first) a._1 else zeroObj.asInstanceOf[A]))
  }
  def resolveSlots[A <: Obj](start: A, apoly: Lst[A], inst: Inst[A, Lst[A]]): Lst[A] = {
    val arg = start match {
      case _: Value[_] => start.clone(via = (start, inst))
      case _ => start
    }
    if (apoly.isSerial) {
      var local = arg
      apoly.clone(apoly.glist.map(slot => {
        local = Inst.resolveArg(local, slot)
        local
      }))
    } else
      apoly.clone(apoly.glist.map(slot => Inst.resolveArg(arg, slot)))
  }
}