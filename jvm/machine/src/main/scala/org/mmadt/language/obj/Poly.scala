package org.mmadt.language.obj

import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.op.branch.{CombineOp, MergeOp}
import org.mmadt.language.obj.op.map.HeadOp.HeadInst
import org.mmadt.language.obj.op.map.PlusOp.PlusInst
import org.mmadt.language.obj.op.map.TailOp.TailInst
import org.mmadt.language.obj.op.map.ZeroOp.ZeroInst
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{IntValue, StrValue, Value}
import org.mmadt.language.{LanguageException, LanguageFactory, Tokens}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.strm.util.MultiSet

trait Poly[A <: Obj] extends Obj
  with Type[Poly[A]]
  with Value[Poly[A]]
  with CombineOp[A]
  with MergeOp[A]
  with GetOp[Obj, A]
  with PutOp[Obj, A]
  with HeadOp[A]
  with TailOp
  with PlusOp[Poly[A]]
  with MultOp[Poly[A]]
  with ZeroOp[Poly[A]] {

  def ground: PolyTuple[A]
  def groundConnective: String = ground._1
  def groundList: List[A] = ground._2
  def groundKeys: List[String] = ground._3
  def hasKeys: Boolean = groundKeys.nonEmpty
  def isSerial: Boolean = this.groundConnective == Tokens.:/
  def isChoice: Boolean = this.groundConnective == Tokens.:|

  def zeroOp(inst: ZeroInst[A]): this.type = this.clone(List.empty[A]).via(this, inst)
  def tailOp(inst: TailInst[Poly[A]]): this.type = if (this.groundList.isEmpty) throw new LanguageException("no tail on empty poly") else this.clone(this.groundList.tail).via(this, inst)
  def headOp(inst: HeadInst[A]): A = if (!this.groundList.exists(_.alive)) throw new LanguageException("no head on empty poly") else this.groundList.filter(_.alive).head.via(this, inst)

  def plusOp(inst: PlusInst[Poly[A]]): this.type = {
    this.ground._1 match {
      case "|" => this.clone(ground = (ground._1, ground._2 ++ inst.arg0[Poly[A]]().ground._2, ground._3)).via(this, inst)
      case ";" => this.clone(ground = (ground._1, ground._2 ++ inst.arg0[Poly[A]]().ground._2, ground._3)).via(this, inst)
      case "/" => this.clone(ground = (ground._1, ground._2 ++ inst.arg0[Poly[A]]().ground._2, ground._3)).via(this, inst)
    }
  }

  def clone(values: List[A]): this.type = this.clone(ground = (groundConnective, values, groundKeys))

  override def get(key: Obj): A = {
    val valueType: A = key match {
      case astr: StrValue =>
        Poly.checkIndex(this, groundKeys.indexOf(astr.ground))
        this.groundList(groundKeys.indexOf(astr.ground))
      case aint: IntValue =>
        Poly.checkIndex(this, aint.ground.toInt)
        this.groundList(aint.ground.toInt)
      case _ => obj.asInstanceOf[A]
    }
    valueType.via(this, GetOp[Obj, A](key, valueType))
  }

  override def get[BB <: Obj](key: Obj, btype: BB): BB = btype.via(this, GetOp[Obj, BB](key, btype))

  def isValue: Boolean = this.isInstanceOf[Strm[_]] || (!this.ground._2.exists(x => x.alive && ((x.isInstanceOf[Type[_]] && !x.isInstanceOf[Poly[_]]) || (x.isInstanceOf[Poly[_]] && !x.asInstanceOf[Poly[_]].isValue))))
  def isType: Boolean = !this.ground._2.exists(x => x.alive && ((x.isInstanceOf[Value[_]] && !x.isInstanceOf[Poly[_]]) || (x.isInstanceOf[Poly[_]] && !x.asInstanceOf[Poly[_]].isType)))

  override def test(other: Obj): Boolean = other match {
    case astrm: Strm[_] => MultiSet.test(this, astrm)
    case serial: Poly[_] =>
      if (serial.groundList.isEmpty || this.groundList.equals(serial.groundList)) return true
      serial.groundConnective == this.groundConnective &&
        this.groundList.zip(serial.groundList).foldRight(true)((a, b) => a._1.test(a._2) && b)
    case _ => false
  }

  override def toString: String = LanguageFactory.printPoly(this)
  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.ground.hashCode()
  override def equals(other: Any): Boolean = other match {
    case astrm: Strm[_] => MultiSet.test(this, astrm)
    case apoly: Poly[_] =>
      apoly.groundConnective == this.groundConnective && apoly.name.equals(this.name) && eqQ(apoly, this) &&
        ((this.isValue && apoly.isValue && this.groundList.zip(apoly.groundList).foldRight(true)((a, b) => a._1.test(a._2) && b)) ||
          (this.groundList == apoly.groundList && this.via == apoly.via))
    case _ => false
  }
}

object Poly {
  def checkIndex(apoly: Poly[_], index: scala.Int): Unit = {
    if (index < 0) throw new LanguageException("poly index must be 0 or greater: " + index)
    if (apoly.groundList.length < (index + 1)) throw new LanguageException("poly index is out of bounds: " + index)
  }
  def keepFirst[A <: Obj](apoly: Poly[A]): Poly[A] = {
    val first: scala.Int = apoly.groundList.indexWhere(x => x.alive)
    apoly.clone(apoly.groundList.zipWithIndex.map(a => if (a._2 == first) a._1 else zeroObj.asInstanceOf[A]))
  }
  def resolveSlots[A <: Obj](start: A, apoly: Poly[A], inst: Inst[A, Poly[A]]): Poly[A] = {
    val arg = start match {
      case _: Value[_] => start.clone(via = (start, inst))
      case _ => start
    }
    apoly.clone(apoly.groundList.map(slot => Inst.resolveArg(arg, slot)))
  }
}