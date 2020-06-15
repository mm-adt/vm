package org.mmadt.language.obj.op.trace
import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.{BranchInstruction, OpInstResolver, TraceInstruction}
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, Lst, Obj, Poly}
import org.mmadt.storage.StorageFactory._
import org.mmadt.storage.obj.value.VInst

trait DefineOp {
  this: Obj =>
  def define(obj: Obj): this.type = DefineOp(obj).exec(this)
}
object DefineOp extends Func[Obj, Obj] {
  def apply[O <: Obj](obj: Obj): Inst[O, O] = new VInst[O, O](g = (Tokens.define, List(obj)), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = if (!Obj.fetch(start, inst.arg0[Obj])) start.via(start, inst) else start

  // [define] related utility methods
  def getDefines(obj: Obj): List[Obj] = obj.trace.filter(x => x._2.op == Tokens.define).map(x => x._2.arg0[Obj]).sortBy(x => -x.domainObj[Obj]().trace.length)
  def putDefines(defines: List[Obj], obj: Obj): Obj = obj.trace.map(x => x._2).foldLeft(defines.foldLeft(obj.domainObj[Obj]())((x, y) => DefineOp(y).exec(x)))((x, y) => y.exec(x))
  private def getPolyOrObj(obj: Obj): Obj = obj.domain match {
    case alst: Lst[_] => alst.glist.head
    case _ => obj
  }

  private type Rewrite = (Obj, List[Inst[Obj, Obj]], Obj) => Obj
  private def rewriteInstArgs(defines: List[Obj], inst: Inst[Obj, Obj], rewrite: Rewrite): Inst[Obj, Obj] = inst match {
    case _: TraceInstruction => inst
    case _: BranchInstruction => inst
    case _ => OpInstResolver.resolve(inst.op, inst.args.map {
      case atype: Type[_] => traceScanCompiler(defines, atype, rewrite)
      case avalue: Value[_] => avalue
    })
  }
  private def mapInstructions(lhs: Inst[Obj, Obj], rhs: Inst[Obj, Obj]): Inst[Obj, Obj] = {
    if (lhs.equals(rhs)) return lhs
    if (lhs.op != rhs.op) return lhs.q(qZero)
    if (lhs.args.length != rhs.args.length) return lhs.q(qZero)
    val args = lhs.args.zip(rhs.args).map(x => if (x._1.equals(x._2)) x._2 else if (x._1.test(x._2)) x._1.compute(x._2) else x._2.q(qZero))
    if (args.forall(_.alive)) OpInstResolver.resolve(lhs.op, args) else lhs.q(qZero)
  }

  def traceScanCompiler[A <: Obj](defines: List[Obj], obj: A, rewrite: Rewrite): A = {
    var a: Obj = obj
    var b: Obj = obj
    defines.filter(x => !(x.isInstanceOf[Value[Obj]] && !x.isInstanceOf[Poly[_]])).filter(x => !__.isToken(x.range)).foreach(d => {
      a = b
      b = b.domainObj[Obj]()
      val range = getPolyOrObj(d.range)
      val domain = getPolyOrObj(d)
      val domainTrace = domain.trace.map(x => x._2)
      val length = domainTrace.length
      while (!a.root && a.trace.length >= length) {
        val atake: List[Inst[Obj, Obj]] = a.trace.map(x => x._2).map(x => rewriteInstArgs(defines, x, rewrite)).take(length)
        val arewrite = atake.zip(domainTrace).map(x => mapInstructions(x._1, x._2))
        if (arewrite.forall(x => x.alive)) {
          b = rewrite(range, arewrite, b)
          for (_ <- 1 to length) a = a.linvert()
        } else {
          b = atake.headOption.map(x => x.exec(b)).getOrElse(b) // atake.foldLeft(b)((x,y) => y.exec(x))
          a = a.linvert() //for (_ <- 1 to length) a = a.linvert()
        }
      }
    })
    b.trace.map(x => x._2).filter(x => x.op != Tokens.define).foldLeft(b.domainObj[Obj]())((x, y) => y.exec(x)).asInstanceOf[A]
    //b.asInstanceOf[A]
  }

  def chooseRewrite(range: Obj, trace: List[Inst[Obj, Obj]], query: Obj): Obj = query.split(range `|` trace.filter(x => x.op != Tokens.define).foldLeft(__.asInstanceOf[Obj])((x, y) => y.exec(x))).merge
  def replaceRewrite(range: Obj, trace: List[Inst[Obj, Obj]], query: Obj): Obj = {
    query.compute(trace
      .zip(range.trace.map(x => x._2))
      .map(x => OpInstResolver.resolve[Obj, Obj](x._2.op, x._1.args.zip(x._2.args).map(y => y._1.compute(y._2))))
      .foldLeft(range.domainObj[Obj]())((x, y) => y.exec(x)))
  }

  /*def main(args: Array[String]): Unit = {
    val headInt = int.define((int `;`) <= (int.plus(0) `;`)).define((int.plus(int) `;`) <= (int.mult(2) `;`)).define((int.mult(__("x")) `;`) <= (int.plus(__.is(int.eqs(-1)).to("x")) `;`)) // int.define((int`;`)<=(int.plus(330).plus(330)`;`))
    val queryInput = headInt.plus(10).plus(0).plus(0).mult(2).plus(-1) // int.define(__("nat")<=int.is(int.gt(0))).define(int<=int.plus(5)).as(__("nat")) //
    println(getDefines(queryInput))
    println(queryInput)
    val queryOutput = DefineOp.traceScanCompiler(getDefines(queryInput), queryInput, replaceRewrite)
    println(queryOutput)
    println((int(0) ==> queryOutput))
    assert(int(19) == ((int(0) ==> queryOutput)))
  }*/

}