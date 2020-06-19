package org.mmadt.storage.obj.`type`
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.branch.{MergeOp, SplitOp}
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.{LanguageFactory, Tokens}
import org.mmadt.storage.StorageFactory.qOne
import org.mmadt.storage.obj.value.strm.util.MultiSet

class TLst[A <: Obj](val name: String = Tokens.lst, val g: LstTuple[A], val q: IntQ = qOne, val via: ViaTuple = base) extends Lst[A] with Type[Obj] with Inst[A, Obj] {
  //override def isType: Boolean = true
  //override def isValue: Boolean = false
  override val func: Func[_ <: Obj, _ <: Obj] = SplitOp.apply(this).func
  def exec(start: A): Obj = {
    val temp = MergeOp().exec(SplitOp(this).exec(start))
    temp.clone(via = (start, this))
  }
  override lazy val hashCode: scala.Int = this.name.hashCode ^ this.g.hashCode()
  override def toString: String = LanguageFactory.printLst(this)
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
  override def equals(other: Any): Boolean = other match {
    case astrm: Strm[_] => MultiSet.test(this, astrm)
    case alst: Lst[_] =>
      Poly.sameSep(this, alst) && alst.name.equals(this.name) && eqQ(alst, this) &&
        ((this.isValue && this.glist.zip(alst.glist).forall(b => b._1.equals(b._2))) ||
          (this.glist.equals(alst.glist) && this.via.equals(alst.via)))
    case _ => false
  }
  override def clone(name: String = this.name,
                     g: Any = this.g,
                     q: IntQ = this.q,
                     via: ViaTuple = this.via): this.type = new TLst[A](name = name, g = g.asInstanceOf[LstTuple[A]], q = q, via = via).asInstanceOf[this.type]

}



