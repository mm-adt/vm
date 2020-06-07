package org.mmadt.language.obj

import org.mmadt.language.LanguageFactory
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.branch.{CombineOp, MergeOp}
import org.mmadt.language.obj.op.map._
import org.mmadt.language.obj.op.sideeffect.PutOp
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
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
  with LastOp[A]
  with PlusOp[Lst[A]]
  with MultOp[Lst[A]]
  //with OneOp[Lst[A]]
  with ZeroOp[Lst[A]] {

  def g: LstTuple[A]
  override def gsep: String = g._1
  override def glist: List[A] = g._2

  def clone(values: List[A]): this.type = this.clone(g = (gsep, values))

  override def test(other: Obj): Boolean = other match {
    case aobj: Obj if !aobj.alive => !this.alive
    case anon: __ => Inst.resolveArg(this, anon).alive
    case astrm: Strm[_] => MultiSet.test(this, astrm)
    case alst: Lst[_] => // Poly.sameSep(this, alst) &&
      withinQ(this, alst) &&
        (this.glist.length == alst.glist.length || alst.glist.isEmpty) && // TODO: should lists only check up to their length
        this.glist.zip(alst.glist).forall(b => Obj.copyDefinitions(this, b._1).test(b._2))
    case _ => false
  }

  override def toString: String = LanguageFactory.printLst(this)
  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.g.hashCode()
  override def equals(other: Any): Boolean = other match {
    case astrm: Strm[_] => MultiSet.test(this, astrm)
    case alst: Lst[_] =>
      Poly.sameSep(this, alst) && alst.name.equals(this.name) && eqQ(alst, this) &&
        ((this.isValue && this.glist.zip(alst.glist).forall(b => b._1.equals(b._2))) ||
          (this.glist.equals(alst.glist) && this.via.equals(alst.via)))
    case _ => false
  }
}

object Lst {
  def keepFirst[A <: Obj](apoly: Lst[A]): Lst[A] = {
    val first: scala.Int = apoly.glist.indexWhere(x => x.alive)
    apoly.clone(apoly.glist.zipWithIndex.map(a => if (a._2 == first) a._1 else zeroObj.asInstanceOf[A]))
  }
  def resolveSlots[A <: Obj](start: A, apoly: Lst[A]): Lst[A] = {
    if (apoly.isSerial) {
      var local = start
      apoly.clone(apoly.glist.map(slot => {
        local = local match {
          case astrm: Strm[_] => strm(astrm.values.map(x => Inst.resolveArg(x, slot)))
          case _ => Inst.resolveArg(local, slot)
        }
        local
      }))
    } else apoly.clone(apoly.glist.map(slot => Inst.resolveArg(start, slot)))

  }
}