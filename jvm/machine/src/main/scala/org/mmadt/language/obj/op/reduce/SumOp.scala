package org.mmadt.language.obj.op.reduce

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.op.{ReduceInstruction, TraceInstruction}
import org.mmadt.language.obj.value.IntValue
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Inst, Int, Obj}
import org.mmadt.storage.StorageFactory.{int, qOne}
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait SumOp {
  this: Obj =>
  def sum(): Int = SumOp().exec(this)
}

object SumOp extends Func[Obj, Obj] {
  def apply(): Inst[Obj, Int] = new VInst[Obj, Int](g = (Tokens.sum, Nil), func = this) with ReduceInstruction[Int] with TraceInstruction {
    val seed: Int = int(0)
    val reducer: Int = __.to("x").get(0).plus(__.from("x").get(1).mult(__.quant())).as(int)
  }

  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = {
    start match {
      case strm: Strm[Int] => strm.values.foldLeft(int(0))((x, y) => x + int(y.g * y.q._1.g)).clone(q = qOne, via = (start, inst))
      case avalue: IntValue => int(avalue.g * avalue.q._1.g).clone(q = qOne, via = (start, inst))
      case _: Type[_] => int.via(start, inst).hardQ(qOne)
    }
  }
}