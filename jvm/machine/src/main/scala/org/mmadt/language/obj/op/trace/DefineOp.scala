package org.mmadt.language.obj.op.trace
import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.__
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.{Inst, Lst, Obj}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait DefineOp {
  this: Obj =>
  def define(obj: Obj): this.type = DefineOp(obj).exec(this)
}
object DefineOp extends Func[Obj, Obj] {
  def apply[O <: Obj](obj: Obj): Inst[O, O] = new VInst[O, O](g = (Tokens.define, List(obj)), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = if (!Obj.fetch(start, inst.arg0[Obj])) start.via(start, inst) else start

  private def traceScanCompiler[A <: Obj](obj: A): A = {
    val defines: List[Obj] = obj.trace.filter(x => x._2.op == Tokens.define).map(x => x._2.arg0[Obj]).sortBy(x => -x.toString.length)
    println(defines)
    var a: Obj = obj
    var b: Obj = obj
    defines.foreach(d => {
      a = b
      b = b.domainObj[Obj]()
      val range = d.range.asInstanceOf[Lst[Obj]].glist.head
      val domain = d.domain[Obj].asInstanceOf[Lst[Obj]].glist.head
      val domainTrace = domain.trace.map(x => x._2)
      val length = domainTrace.length
      while (!a.root && a.trace.length >= length) {
        val atake = a.trace.map(x => x._2).take(length)
        if (atake.equals(domainTrace)) {
          b = b.split(range `|` atake.foldLeft(__.asInstanceOf[Obj])((x, y) => y.exec(x))).merge
          for (i <- 1 to length)
            a = a.linvert()
        } else {
          b = atake.head.exec(b)
          a = a.linvert()
        }
      }
    })
    b.asInstanceOf[A]
  }

  def main(args: Array[String]): Unit = {
    val headInt = int.define((int `;`) <= (int.plus(0) `;`)).define((int.plus(int) `;`) <= (int.mult(2) `;`)) //.define((int`;`)<=(int.plus(0).plus(0)`;`))
    val query = DefineOp.traceScanCompiler(headInt.plus(10).plus(0).plus(0).mult(2).plus(2))
    println(query)
    println((int(34) ==> query).trace)
  }

}