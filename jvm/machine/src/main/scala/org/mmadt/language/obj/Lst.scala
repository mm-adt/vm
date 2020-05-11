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
  with ZeroOp[Lst[A]] {

  def ground: LstTuple[A]
  def gsep: String = ground._1
  override def gvalues: List[A] = ground._2

  def clone(values: List[A]): this.type = this.clone(ground = (gsep, values))

  override def get(key: Int): A = {
    val valueType: A = key match {
      case aint: IntValue =>
        Lst.checkIndex(this, aint.ground.toInt)
        this.gvalues(aint.ground.toInt)
      case _ => obj.asInstanceOf[A]
    }
    valueType.via(this, GetOp[Obj, A](key, valueType))
  }

  override def get[BB <: Obj](key: Int, btype: BB): BB = btype.via(this, GetOp[Obj, BB](key, btype))

  override def test(other: Obj): Boolean = other match {
    case astrm: Strm[_] => MultiSet.test(this, astrm)
    case alst: Lst[_] =>
      if (alst.gvalues.isEmpty || this.gvalues.equals(alst.gvalues)) return true
      Poly.sameSep(this, alst) && this.gvalues.zip(alst.gvalues).foldRight(true)((a, b) => a._1.test(a._2) && b)
    case _ => false
  }

  override def toString: String = LanguageFactory.printLst(this)
  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.ground.hashCode()
  override def equals(other: Any): Boolean = other match {
    case astrm: Strm[_] => MultiSet.test(this, astrm)
    case alst: Lst[_] =>
      Poly.sameSep(this, alst) && alst.name.equals(this.name) && eqQ(alst, this) &&
        ((this.isValue && alst.isValue && this.gvalues.zip(alst.gvalues).foldRight(true)((a, b) => a._1.test(a._2) && b)) ||
          (this.gvalues == alst.gvalues && this.via == alst.via))
    case _ => false
  }
}

object Lst {
  def checkIndex(apoly: Lst[_], index: scala.Int): Unit = {
    if (index < 0) throw new LanguageException("poly index must be 0 or greater: " + index)
    if (apoly.gvalues.length < (index + 1)) throw new LanguageException("poly index is out of bounds: " + index)
  }
  def keepFirst[A <: Obj](apoly: Lst[A]): Lst[A] = {
    val first: scala.Int = apoly.gvalues.indexWhere(x => x.alive)
    apoly.clone(apoly.gvalues.zipWithIndex.map(a => if (a._2 == first) a._1 else zeroObj.asInstanceOf[A]))
  }
  def resolveSlots[A <: Obj](start: A, apoly: Lst[A], inst: Inst[A, Lst[A]]): Lst[A] = {
    val arg = start match {
      case _: Value[_] => start.clone(via = (start, inst))
      case _ => start
    }
    if (apoly.isSerial) {
      var local = arg
      apoly.clone(apoly.gvalues.map(slot => {
        local = Inst.resolveArg(local, slot)
        local
        //local = local.via(x, IdOp())
       // x
      }))
    } else
      apoly.clone(apoly.gvalues.map(slot => Inst.resolveArg(arg, slot)))
  }
}