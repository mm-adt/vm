package org.mmadt.language.obj

import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.branch.{CombineOp, MergeOp}
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{IntValue, Value}
import org.mmadt.language.{LanguageException, LanguageFactory, Tokens}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.strm.util.MultiSet

trait Lst[A <: Obj] extends Obj
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
  def connective: String = ground._1
  def elements: List[A] = ground._2
  def isSerial: Boolean = this.connective == Tokens.:/
  def isParallel: Boolean = this.connective == Tokens.:\
  def isChoice: Boolean = this.connective == Tokens.:|

  def clone(values: List[A]): this.type = this.clone(ground = (connective, values))

  override def get(key: Int): A = {
    val valueType: A = key match {
      case aint: IntValue =>
        Lst.checkIndex(this, aint.ground.toInt)
        this.elements(aint.ground.toInt)
      case _ => obj.asInstanceOf[A]
    }
    valueType.via(this, GetOp[Obj, A](key, valueType))
  }

  override def get[BB <: Obj](key: Int, btype: BB): BB = btype.via(this, GetOp[Obj, BB](key, btype))

  def isValue: Boolean = this.isInstanceOf[Strm[_]] || (!this.ground._2.exists(x => x.alive && ((x.isInstanceOf[Type[_]] && !x.isInstanceOf[Lst[_]]) || (x.isInstanceOf[Lst[_]] && !x.asInstanceOf[Lst[_]].isValue))))
  def isType: Boolean = !this.ground._2.exists(x => x.alive && ((x.isInstanceOf[Value[_]] && !x.isInstanceOf[Lst[_]]) || (x.isInstanceOf[Lst[_]] && !x.asInstanceOf[Lst[_]].isType)))

  override def test(other: Obj): Boolean = other match {
    case astrm: Strm[_] => MultiSet.test(this, astrm)
    case serial: Lst[_] =>
      if (serial.elements.isEmpty || this.elements.equals(serial.elements)) return true
      serial.connective == this.connective &&
        this.elements.zip(serial.elements).foldRight(true)((a, b) => a._1.test(a._2) && b)
    case _ => false
  }

  override def toString: String = LanguageFactory.printLst(this)
  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.ground.hashCode()
  override def equals(other: Any): Boolean = other match {
    case astrm: Strm[_] => MultiSet.test(this, astrm)
    case apoly: Lst[_] =>
      apoly.connective == this.connective && apoly.name.equals(this.name) && eqQ(apoly, this) &&
        ((this.isValue && apoly.isValue && this.elements.zip(apoly.elements).foldRight(true)((a, b) => a._1.test(a._2) && b)) ||
          (this.elements == apoly.elements && this.via == apoly.via))
    case _ => false
  }
}

object Lst {
  def checkIndex(apoly: Lst[_], index: scala.Int): Unit = {
    if (index < 0) throw new LanguageException("poly index must be 0 or greater: " + index)
    if (apoly.elements.length < (index + 1)) throw new LanguageException("poly index is out of bounds: " + index)
  }
  def keepFirst[A <: Obj](apoly: Lst[A]): Lst[A] = {
    val first: scala.Int = apoly.elements.indexWhere(x => x.alive)
    apoly.clone(apoly.elements.zipWithIndex.map(a => if (a._2 == first) a._1 else zeroObj.asInstanceOf[A]))
  }
  def resolveSlots[A <: Obj](start: A, apoly: Lst[A], inst: Inst[A, Lst[A]]): Lst[A] = {
    val arg = start match {
      case _: Value[_] => start.clone(via = (start, inst))
      case _ => start
    }
    apoly.clone(apoly.elements.map(slot => Inst.resolveArg(arg, slot)))
  }
}